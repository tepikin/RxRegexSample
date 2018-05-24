package ru.lazard.regexp.ui.activity

import android.support.design.widget.NavigationView
import android.view.View
import android.widget.ImageView
import android.widget.TextView

import io.reactivex.disposables.CompositeDisposable
import ru.lazard.regexp.R
import ru.lazard.regexp.application.selectedFileManager
import ru.lazard.regexp.ui.BaseActivity
import ru.lazard.regexp.manage.SelectedFileManager
import ru.lazard.regexp.model.SelectedFile

/**
 * Created by Egor on 11.04.2017.
 */

class SelectedFileViewController(private val activity: MainActivity) : View.OnClickListener {
        private val iconView: ImageView
    private val titleView: TextView
    private val detailsView: TextView
    internal var compositeDisposable = CompositeDisposable()

    override fun onClick(v: View?) {
        val activity : MainActivity =  (v?.context as? MainActivity) ?:return;
        selectedFileManager.selectFile(activity,{updateSelectedFile(it as SelectedFile)});
    }

    init {
        val headerView = (activity.findViewById(R.id.nav_view) as NavigationView).getHeaderView(0)
        iconView = headerView.findViewById(R.id.nav_header_icon) as ImageView
        titleView = headerView.findViewById(R.id.nav_header_title) as TextView
        detailsView = headerView.findViewById(R.id.nav_header_details) as TextView

        iconView.setOnClickListener(this)
        titleView.setOnClickListener(this)
        detailsView.setOnClickListener(this)

        compositeDisposable.add(selectedFileManager.rxSelectedFileChanged.subscribe{updateSelectedFile(it)})

        compositeDisposable.add(
                activity.rxLifeCycle
                        .filter { it == BaseActivity.LifeCycle.DESTROY }
                        .subscribe { compositeDisposable.dispose() })

    }

    private fun updateSelectedFile(selectedFile: SelectedFile) {
        titleView.text = selectedFile.displayName
        detailsView.text = selectedFile.mimeType
        iconView.setImageResource(R.drawable.ic_menu_gallery)
    }
}
