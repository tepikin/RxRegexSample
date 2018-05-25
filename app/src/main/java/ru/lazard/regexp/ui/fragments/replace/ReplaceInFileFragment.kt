package ru.lazard.regexp.ui.fragments.replace

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.NotificationCompat
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.view.*
import android.widget.Toast

import com.jakewharton.rxbinding2.widget.RxTextView
import com.trello.rxlifecycle2.android.FragmentEvent

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.internal.util.NotificationLite.disposable
import io.reactivex.schedulers.Schedulers
import ru.lazard.regexp.R
import ru.lazard.regexp.manage.SelectedFileManager
import ru.lazard.regexp.ui.fragments.BaseFragment
import kotlinx.android.synthetic.main.replace_in_file_fragment.*
import org.jetbrains.anko.notificationManager
import ru.lazard.regexp.application.regexManager
import ru.lazard.regexp.application.selectedFileManager
import ru.lazard.regexp.ui.activity.MainActivity
import java.io.File

/**
 * Created by Egor on 01.11.2016.
 */

class ReplaceInFileFragment : BaseFragment() {
    private var disposable: CompositeDisposable? = null
    fun onApplyClick(){
        selectedFileManager.selectedFile = selectedFileManager.selectedFile?.copy(content = hugeTextView?.text.toString());

    }

    override fun onDetach() {
        super.onDetach()
        (context as? MainActivity)?.hideFloatingActionButton()
        disposableFloatinActionButtonClick?.dispose()
    }

    override fun onDestroyView() {
        disposable?.dispose()
        super.onDestroyView()
    }
    private var disposableFloatinActionButtonClick: Disposable? = null;

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        (context as? MainActivity)?.showFloatingActionButton()
        disposableFloatinActionButtonClick = (context as? MainActivity)?.rxFloatinActionButtonClick?.subscribe {onApplyClick()}
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.replace_in_file_fragment, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        disposable?.dispose()
        disposable = CompositeDisposable()

        pattern.setText(regexManager.regex)
        replace_to.setText(regexManager.replacement)

        val selectedFile = selectedFileManager.selectedFile
        if (selectedFile != null) {
            hugeTextView.text = selectedFile.content?:""
        }

        disposable?.add(
        RxTextView.afterTextChangeEvents(pattern)
                .compose(bindUntilEvent(FragmentEvent.DESTROY))
                .map<String?> { event -> event?.editable()?.toString()?:"" }
                .distinctUntilChanged()
                .subscribe ({  regexManager.regex = it?:"" },
                { it.printStackTrace();statistic.text = "error: " + it.message }))
                disposable?.add(
        RxTextView.afterTextChangeEvents(replace_to)
                .compose(bindUntilEvent(FragmentEvent.DESTROY))
                .map<String?> { event -> event?.editable()?.toString()?:"" }
                .distinctUntilChanged()
                .subscribe ({  regexManager.replacement = it?:"" },
                {it.printStackTrace(); statistic.text = "error: " +it.message }))
                disposable?.add(
        Observable.merge(
                Observable.just(Any()),
                selectedFileManager.rxSelectedFileChanged,
                regexManager.events)
                .compose(bindUntilEvent(FragmentEvent.DESTROY))
                .map { event ->
                    val textString = selectedFileManager.selectedFile?.content?:""
                    regexManager.replace(textString)
                            .scan(SpannableStringBuilder()) { spannableStringBuilder, append ->
                                spannableStringBuilder.append(append.appendDst)
                                if (append.isMatched) {
                                    spannableStringBuilder.setSpan(BackgroundColorSpan(Color.argb(255 / 5, 0, 0, 255)), append.fromDst, append.toDst, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                                }
                                spannableStringBuilder
                            }.lastElement()//
                            .subscribeOn(Schedulers.computation())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ spannableStringBuilder ->
                                hugeTextView.text = SpannableStringBuilder(spannableStringBuilder)
                                val spans = spannableStringBuilder.getSpans(0, spannableStringBuilder.length, BackgroundColorSpan::class.java)
                                if (spans.size > 0) {
                                    hugeTextView.scrollToTextPosition(spannableStringBuilder.getSpanStart(spans[0]))
                                }
                                statistic.text = "find: " + spans.size
                            }
                            ) { throwable -> statistic.text = "error: " + throwable.message }
                }
                .scan { disposable, disposable2 ->
                    disposable.dispose()
                    disposable2
                }.subscribe({}){ it.printStackTrace();statistic.text = "error: " + it.message })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_replace_in_file, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.action_save_replaces_in_file){
            try {
                var file = selectedFileManager.save(hugeTextView.text.toString())


                var intent = Intent(Intent.ACTION_VIEW, Uri.parse(file.toURI().toString()));

                var resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
                val mBuilder = NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_menu_gallery)
                        .setContentTitle("File saved")
                        .setContentText(file.name)
                        .setContentIntent(resultPendingIntent )
                context.notificationManager.notify(1, mBuilder.build())

            }catch (e: Throwable){
                Toast.makeText(context,e.toString(),Toast.LENGTH_LONG).show();
            }
        }
        return super.onOptionsItemSelected(item)
    }

}

