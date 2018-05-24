package ru.lazard.regexp.ui.fragments.view

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.support.v4.view.GestureDetectorCompat
import android.text.Editable
import android.text.InputType
import android.text.Selection
import android.text.Spanned
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.inputmethod.BaseInputConnection
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout

import java.lang.reflect.Field
import java.lang.reflect.Modifier

import android.util.Log.e

/**
 * Created by Egor on 18.04.2017.
 */

class CustomViewGroup : FrameLayout {
    private var mImm: InputMethodManager? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init()
    }

    private val gestureDetectorCompat = GestureDetectorCompat(context, object : GestureDetector.SimpleOnGestureListener() {
        //  private float diff= TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,4,getContext().getResources().getDisplayMetrics());

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            onClick()
            return super.onSingleTapUp(e)
        }

        //        @Override
        //        public boolean onContextClick(MotionEvent e) {
        //            if (Math.abs(downPoint.x-e.getX())>diff
        //                    ||
        //                    Math.abs(downPoint.y-e.getY())>diff){
        //                return false;
        //            }
        //            onClick();
        //            return super.onContextClick(e);
        //        }
        //private PointF downPoint = new PointF();
        //        @Override
        //        public boolean onDown(MotionEvent e) {
        //            downPoint.set(e.getX(),e.getY());
        //            return super.onDown(e);
        //        }
    })


    private fun onClick() {

        Log.e("TAG", "requestFocus=" + requestFocus())
        Log.e("TAG", "mImm.isActive()=" + mImm!!.isActive)
        e("TAG", "showSoftInput=" + mImm!!.showSoftInput(this@CustomViewGroup, 0))
        //mImm.isActive();

        //        //Log.e("TAG","showSoftInput="+mImm.showSoftInput(this,mImm.SHOW_FORCED));
        //        new Handler(Looper.getMainLooper()).post(new Runnable() {
        //            @Override
        //            public void run() {
        //                e("TAG", "showSoftInput=" + mImm.showSoftInput(CustomViewGroup.this, mImm.SHOW_FORCED));
        //            }
        //        });

    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        gestureDetectorCompat.onTouchEvent(ev)
        return super.dispatchTouchEvent(ev)
    }


    private fun init() {
        mImm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        isFocusableInTouchMode = true
        mEditable = Editable.Factory.getInstance().newEditable("")
        Selection.setSelection(mEditable, 0)
        mEditable!!.setSpan(mMyTextWatcher, 0, mEditable!!.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        // mImm.restartInput(this);

    }

    private var mEditable: Editable? = null
    private var mBatch: Int = 0
    private val mMyTextWatcher = MyTextWatcher()

    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection {
        outAttrs.inputType = InputType.TYPE_CLASS_TEXT
        outAttrs.imeOptions = EditorInfo.IME_ACTION_DONE
        outAttrs.initialSelStart = 0
        outAttrs.initialSelEnd = outAttrs.initialSelStart

        e("TAG", "onCreateInputConnection")

        // Создание коннектора к клавиатуре.
        val baseInputConnection = object : BaseInputConnection(this, false) {
            override fun getEditable(): Editable {
                e("TAG", "getEditable")
                return mEditable!!
            }

            override fun commitText(text: CharSequence, newCursorPosition: Int): Boolean {
                e("TAG", "commitText ")
                return super.commitText(text, newCursorPosition)
            }

            override fun closeConnection() {
                e("TAG", "closeConnection ")
                super.closeConnection()
            }

            override fun endBatchEdit(): Boolean {
                mBatch++
                e("TAG", "endBatchEdit")
                return super.endBatchEdit()
            }

            override fun beginBatchEdit(): Boolean {
                mBatch--
                e("TAG", "beginBatchEdit")
                return super.beginBatchEdit()
            }

        }



        return baseInputConnection
    }

    override fun onCheckIsTextEditor(): Boolean {
        e("TAG", "beginBatchEdit")
        return true
    }

    private inner class MyTextWatcher : TextWatcher {

        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            e("TAG", "Current text: " + charSequence)
        }

        override fun afterTextChanged(editable: Editable) {}
    }

    companion object {

        @Throws(Exception::class)
        internal fun setFinalStatic(field: Field, newValue: Any) {
            field.isAccessible = true

            val modifiersField = Field::class.java.getDeclaredField("accessFlags")
            modifiersField.isAccessible = true
            modifiersField.setInt(field, field.modifiers and Modifier.FINAL.inv())

            field.set(null, newValue)
        }
    }


}
