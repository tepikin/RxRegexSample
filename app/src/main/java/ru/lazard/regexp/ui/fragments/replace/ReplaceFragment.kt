package ru.lazard.regexp.ui.fragments.replace

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.widget.RxTextView
import com.trello.rxlifecycle2.android.FragmentEvent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.replace_fragment.*
import ru.lazard.regexp.R
import ru.lazard.regexp.application.regexManager
import ru.lazard.regexp.ui.fragments.BaseFragment

/**
 * Created by Egor on 01.11.2016.
 */

class ReplaceFragment : BaseFragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.replace_fragment, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        pattern.setText(regexManager.regex)
        replace_to.setText(regexManager.replacement)

        RxTextView.afterTextChangeEvents(pattern!!)
                .compose(bindUntilEvent(FragmentEvent.DESTROY))
                .map<String> { event -> event.editable()!!.toString() }
                .distinctUntilChanged()
                .subscribe ({ regex -> regexManager.regex = regex }
                ){ it.printStackTrace();statistic.text = "error: " + it.message }

        RxTextView.afterTextChangeEvents(replace_to!!)
                .compose(bindUntilEvent(FragmentEvent.DESTROY))
                .map<String> { event -> event.editable()!!.toString() }
                .distinctUntilChanged()
                .subscribe ({ regex -> regexManager.replacement = regex }
                ){ it.printStackTrace();statistic.text = "error: " + it.message }

        Observable.merge(
                Observable.just(Any()),
                regexManager.events,
                RxTextView.afterTextChangeEvents(message).distinctUntilChanged { event -> event.editable()!!.toString() }).map { event ->
            val textString = message.text.toString()
//            if (message.text !is Spannable) {
//                message.setText(SpannableString(textString))
//            }
            val spannable = message.text as? Spannable ?: {message.setText(SpannableString(textString)); message.text}()
            val spans = spannable.getSpans(0, spannable.length, BackgroundColorSpan::class.java)
            for (span in spans) {
                spannable.removeSpan(span)
            }
            val spannableStringBuilder = SpannableStringBuilder()
            message.invalidate()
            regexManager.replace(textString)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ append ->
                        spannableStringBuilder.append(append.appendDst)
                        if (append.isMatched) {
                            spannable.setSpan(BackgroundColorSpan(Color.argb(255 / 5, 0, 0, 255)), append.fromSrc, append.toSrc, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                            spannableStringBuilder.setSpan(BackgroundColorSpan(Color.argb(255 / 5, 0, 0, 255)), append.fromDst, append.toDst, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                            message.invalidate()
                        }
                        statistic.text = "find: " + append.matchedCount
                        result.text = spannableStringBuilder
                    }
                    ) { throwable -> statistic.text = "error: " + throwable.message }
        }
                .scan { disposable, disposable2 ->
                    disposable.dispose()
                    disposable2
                }.subscribe({}){ it.printStackTrace();statistic.text = "error: " + it.message }
    }

}

