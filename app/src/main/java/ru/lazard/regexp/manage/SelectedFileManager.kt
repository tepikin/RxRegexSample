package ru.lazard.regexp.manage

import android.app.Activity
import android.content.Intent
import android.os.Environment
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import ru.lazard.regexp.R
import ru.lazard.regexp.application.selectedFileManager
import ru.lazard.regexp.model.SelectedFile
import ru.lazard.regexp.ui.BaseActivity
import ru.lazard.regexp.ui.activity.MainActivity
import java.io.File
import java.io.FileOutputStream
import java.util.*

/**
 * Created by Egor on 03.11.2016.
 */

class SelectedFileManager {

    var selectedFile: SelectedFile? = null
        set(selectedFile) {
            field = selectedFile
            rxSelectedFileChanged.onNext(selectedFile!!)
        }

    val rxSelectedFileChanged: Subject<SelectedFile> = PublishSubject.create<SelectedFile>()


    fun selectFileIfNotSelected(activity: MainActivity, doOnSuccess: (Any?) -> Unit = {}) {
        if (selectedFile == null) {
            selectedFileManager.selectFile(activity, doOnSuccess)
        } else {
            doOnSuccess.invoke(selectedFile);
        }
    }

    fun selectFile(activity: MainActivity, doOnSuccess: (Any) -> Unit = {}) {
        val requestCode = (11111 + Math.random() * 10000).toInt();
        var dispose: CompositeDisposable = CompositeDisposable()
        dispose.add(activity.rxActivityResult
                .filter { it != null }
                .filter { it.requestCode == requestCode }
                .filter { it.resultCode == Activity.RESULT_OK }
                .filter { it.data != null}
                .map { it.data!!.data }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { activity.showFileChoiceWaiterForView() }
                .observeOn(Schedulers.io())
                .map { SelectedFile.load(it!!) }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { activity.hideFileChoiceWaiterForView() }
                .doOnNext { selectedFileManager.selectedFile = it }
                .doOnNext(doOnSuccess)
                .doOnNext { dispose.dispose() }
                .doOnError { Toast.makeText(activity, it.toString(), Toast.LENGTH_SHORT); }
                .doOnError { dispose.dispose() }
                .doOnError { activity.hideFileChoiceWaiterForView() }
                .subscribe())

        dispose.add(activity.rxLifeCycle.subscribe { if (it == BaseActivity.LifeCycle.DESTROY) dispose.dispose() })

        showFileChoicer(activity, requestCode)
    }

    private fun showFileChoicer(activity: Activity, requestCode: Int) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        try {
            activity.startActivityForResult(intent, requestCode)
        } catch (ex: android.content.ActivityNotFoundException) {
            ex.printStackTrace()
            Toast.makeText(activity, R.string.view_fragment_no_application_for_view, Toast.LENGTH_SHORT)
        }

    }


    fun save(content: String, file: File = createNewCopyName()):File {
        file.parentFile.mkdirs()
        FileOutputStream(file).bufferedWriter().use { it.write(content) }
        return file
    }


    fun createNewCopyName(): File {
        val directory = Environment.getExternalStoragePublicDirectory("Regex")
        directory.mkdirs()
        return File(directory, "${Date().time}_${selectedFile?.displayName ?: selectedFile?.uriOrig?.lastPathSegment ?: "Empty"}")
    }
}