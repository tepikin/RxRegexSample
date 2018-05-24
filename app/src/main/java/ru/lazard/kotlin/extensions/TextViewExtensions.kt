package ru.lazard.kotlin.extensions

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView

/**
 * Created by Egor on 30.05.2017.
 */


private class EmptyTextWatcher(
        val afterTextChanged: ((s: Editable?) -> Unit)? = null,
        val beforeTextChanged: ((s: CharSequence?, start: Int, count: Int, after: Int) -> Unit)? = null,
        val onTextChanged: ((s: CharSequence?, start: Int, before: Int, count: Int) -> Unit)? = null
) : TextWatcher {
    override fun afterTextChanged(s: Editable?) {
        afterTextChanged?.invoke(s)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        beforeTextChanged?.invoke(s, start, count, after)
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        onTextChanged?.invoke(s, start, before, count)
    }

    fun register(textView: TextView): TextWatcher {
        textView.addTextChangedListener(this)
        return this
    }
}

fun TextView.afterTextChangedDistinct(body: (s: Editable?) -> Unit): TextWatcher {
    var text: String? = null;
    return EmptyTextWatcher(afterTextChanged = { s: Editable? ->
        if (text == s?.toString()) return@EmptyTextWatcher
        text = s?.toString()
        body.invoke(s)
    }).register(this)
}

fun TextView.afterTextChanged(body: (s: Editable?) -> Unit): TextWatcher {
    return EmptyTextWatcher(body).register(this)
}

fun TextView.beforeTextChanged(body: (s: CharSequence?, start: Int, count: Int, after: Int) -> Unit): TextWatcher {
    return EmptyTextWatcher(beforeTextChanged = body).register(this)
}

fun TextView.onTextChanged(body: (s: CharSequence?, start: Int, before: Int, count: Int) -> Unit): TextWatcher {
    return EmptyTextWatcher(onTextChanged = body).register(this)
}