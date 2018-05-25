package ru.lazard.regexp.ui.fragments.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import ru.lazard.regexp.application.selectedFileManager

/**
 * Created by Egor on 11.04.2017.
 */

class ViewFragment : Fragment() {
    init {
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val hugeTextView = HugeTextView(context)
        updateText(hugeTextView)
        selectedFileManager.rxSelectedFileChanged.subscribe{updateText(hugeTextView)}
        return hugeTextView
    }

    private fun updateText(hugeTextView: HugeTextView) {
        val selectedFile = selectedFileManager.selectedFile
        if (selectedFile != null) {
            hugeTextView.text = selectedFile.content ?: ""
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        //inflater.inflate(R.menu.menu_test_find,menu);
    }


}

