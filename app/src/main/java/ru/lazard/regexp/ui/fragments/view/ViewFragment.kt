package ru.lazard.regexp.ui.fragments.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import ru.lazard.regexp.application.selectedFileManager

import ru.lazard.regexp.manage.SelectedFileManager

/**
 * Created by Egor on 11.04.2017.
 */

class ViewFragment : Fragment() {
    init {
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val hugeTextView = HugeTextView(context)

        val selectedFile = selectedFileManager.selectedFile
        if (selectedFile != null) {
            hugeTextView.text =selectedFile.content?:""
        }

        selectedFileManager.rxSelectedFileChanged.subscribe()

        return hugeTextView
    }


    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        //inflater.inflate(R.menu.menu_test_find,menu);
    }


}

