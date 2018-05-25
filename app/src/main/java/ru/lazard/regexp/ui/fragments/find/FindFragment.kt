package ru.lazard.regexp.ui.fragments.find

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.jakewharton.rxbinding2.widget.RxTextView
import com.pawegio.kandroid.textWatcher
import com.trello.rxlifecycle2.android.FragmentEvent

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import ru.lazard.regexp.R
import ru.lazard.regexp.ui.fragments.BaseFragment
import kotlinx.android.synthetic.main.find_fragment.*
import ru.lazard.regexp.application.regexManager
import ru.lazard.regexp.application.selectedFileManager

/**
 * Created by Egor on 01.11.2016.
 */

class FindFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.find_fragment, container, false)
    private var disposable: CompositeDisposable? = null
    override fun onDestroyView() {
        disposable?.dispose()
        super.onDestroyView()
    }
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        disposable?.dispose()
        disposable = CompositeDisposable()

        pattern.setText(regexManager.regex ?: "")
        disposable?.add(
         RxTextView.afterTextChangeEvents(pattern)
                .distinctUntilChanged { event -> event.editable()!!.toString() }
                .compose(bindUntilEvent(FragmentEvent.DESTROY)).subscribe ({
            regexManager.regex = pattern.text.toString()
        })
        { it.printStackTrace();statistic.text = "error: " + it.message })

        disposable?.add(
        Observable.merge(
                Observable.just(Any()),
                regexManager.events,
                RxTextView.afterTextChangeEvents(message).distinctUntilChanged { event -> event.editable()!!.toString() })
                .compose(bindUntilEvent(FragmentEvent.DESTROY))
                .map<Disposable> { event ->
                    val textString = message.text.toString()
//                    if (message.text !is Spannable) {
//                        message.setText(SpannableString(textString))
//                    }
//                    val spannable = message.text
                    val spannable = message.text as? Spannable ?: {message.setText(SpannableString(textString)); message.text}()
                    val spans = spannable.getSpans(0, spannable.length, BackgroundColorSpan::class.java)
                    for (span in spans) {
                        spannable.removeSpan(span)
                    }
                    message.invalidate()
                    return@map regexManager.find(textString)
                            .subscribeOn(Schedulers.computation())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    {
                                        if (it.isMatched) {
                                            spannable.setSpan(BackgroundColorSpan(Color.argb(255 / 5, 0, 0, 255)), it.fromSrc, it.toSrc, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                                            message?.invalidate()
                                        }
                                        statistic?.text = "find: " + it.matchedCount
                                    }
                                    , { throwable -> statistic?.text = "error: " + throwable.message })
                }
                .scan { disposable, disposable2 ->
                    disposable.dispose()
                    disposable2
                }.subscribe({})
        { it.printStackTrace();statistic.text = "error: " + it.message })


    }


}

