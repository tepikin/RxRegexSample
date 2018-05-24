package ru.lazard.regexp.ui.fragments.view

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView

/**
 * Created by Egor on 11.04.2017.
 */

class HugeTextView : ListView {
    var text: CharSequence = "No text"
        set(value) {
            field = value
            adapter.notifyDataSetChanged()
        }
    private val adapter = object : BaseAdapter() {


        override fun getCount(): Int {
            return text.length / PART_SIZE + if (text.length % PART_SIZE > 0) 1 else 0;
        }

        override fun getItem(position: Int): Any {
            return text.subSequence(Math.min(PART_SIZE * position, text.length), Math.min(PART_SIZE * (position + 1), text.length))
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertViewIn: View?, parent: ViewGroup?): View {
            var convertView = convertViewIn as? TextView ?: TextView(context)
//            if (convertView == null || convertView !is TextView) {
//                convertView = TextView(context)
//            }

            convertView.setTextIsSelectable(true)
            convertView.text = getItem(position) as CharSequence
            //            convertView1.setFocusableInTouchMode(false);
            //            convertView1.setFocusable(false);
            return convertView
        }
    }

    fun scrollToTextPosition(positionInText: Int) {
        var positionInText = positionInText
        if (positionInText < 0) positionInText = 0
        if (positionInText > text.length) positionInText = text.length
        val positionInList = positionInText / PART_SIZE
        val view = getAdapter().getView(positionInList, null, this)
        view.measure(View.MeasureSpec.makeMeasureSpec(this.width, View.MeasureSpec.AT_MOST),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
        val layout = (view as TextView).layout
        val lineNum = layout.getLineForOffset(positionInText - positionInList * PART_SIZE)
        val rect = Rect()
        layout.getLineBounds(lineNum, rect)
        //smoothScrollToPositionFromTop(positionInList,rect.top);
        setSelectionFromTop(positionInList, -rect.top)

    }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init()
    }


    private fun init() {
        setAdapter(adapter)
        //        setFocusableInTouchMode(false);
        //        setFocusable(false);
    }

    companion object {
        private val PART_SIZE = 1000
    }


}
