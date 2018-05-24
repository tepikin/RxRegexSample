package ru.lazard.rxregex


import io.reactivex.functions.Cancellable
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Class for work with regular expressions with parsing callback.
 * <pre>`// Use listener
 * Regex.replace("abcd", "bc", "BC", 0, listener );    // calls Listener with args "a -> a", "bc -> BC", "d -> d"

 * // Use CancelationSignal
 * CancelationSignal cancelationSignal = new CancelationSignalImpl();
 * Regex.replace("abcd", "(bc)", "_$1_", 0, listener, cancelationSignal );
 * cancelationSignal.cancel();                         // cancelationSignal stop parsing process.
`</pre> *
 */
typealias RegexListener = (Int, Int, String, Int, Int, String, Boolean, Float, Int) -> Unit
class Regex private constructor(private val mText: String,
                                private val mRegularExpression: String,
                                private val mReplacement: String,
                                private val mFlags: Int,
                                private val mListener: RegexListener,
                                private val mCancellationSignal: Regex.CancellationSignal) {
    private var mMatchedCount: Int = 0

    private fun start() {
        if (mCancellationSignal.isCanceled) return

        mMatchedCount = 0
        val pattern = Pattern.compile(this.mRegularExpression, mFlags)
        val matcher = pattern.matcher(mText)
        val bufferEvaluated = StringBuffer()
        val textLength = mText.length
        var appendPos = 0
        var dstLength = 0

        matcher.reset()
        while (matcher.find()) {
            if (mCancellationSignal.isCanceled) return
            if (matcher.start() == matcher.end() && matcher.start() != 0 && matcher.end() != textLength)
                throw IllegalArgumentException("Too short replace text in regularExpression")

            mMatchedCount++

            val substring = mText.substring(appendPos, matcher.start())
            mListener.invoke(appendPos, matcher.start(), substring, dstLength, dstLength + substring.length, substring, false, matcher.start().toFloat() / textLength, mMatchedCount)
            dstLength += substring.length

            if (mCancellationSignal.isCanceled) return

            bufferEvaluated.delete(0, bufferEvaluated.length)
            appendEvaluated(bufferEvaluated, mReplacement, matcher)

            if (mCancellationSignal.isCanceled) return

            val substringSrc = mText.substring(matcher.start(), matcher.end())
            val substringDst = bufferEvaluated.toString()
            mListener.invoke(matcher.start(), matcher.end(), substringSrc, dstLength, dstLength + substringDst.length, substringDst, true, matcher.end().toFloat() / textLength, mMatchedCount)
            dstLength += substringDst.length

            appendPos = matcher.end()
        }
        if (mCancellationSignal.isCanceled) return
        if (appendPos < matcher.regionEnd()) {
            val substring = mText.substring(appendPos, matcher.regionEnd())
            mListener.invoke(appendPos, matcher.regionEnd(), substring, dstLength, dstLength + substring.length, substring, false, matcher.regionEnd().toFloat() / textLength, mMatchedCount)
        }
    }

    /**
     * Internal helper method to append a given string to a given string buffer.
     * If the string contains any references to groups, these are replaced by
     * the corresponding group'mReplacement contents.

     * @param buffer      the string buffer.
     * *
     * @param replaceText the string to append.
     */
    private fun appendEvaluated(buffer: StringBuffer, replaceText: String, matcher: Matcher) {
        var escape = false
        var dollar = false

        for (i in 0..replaceText.length - 1) {
            val c = replaceText[i]
            if (c == '\\' && !escape) {
                escape = true
            } else if (c == '$' && !escape) {
                dollar = true
            } else if (c >= '0' && c <= '9' && dollar) {
                buffer.append(matcher.group(c - '0'))
                dollar = false
            } else if (c == 'n' && escape) {
                buffer.append("\n")
                dollar = false
                escape = false
            } else if (c == 'r' && escape) {
                buffer.append("\r")
                dollar = false
                escape = false
            } else if (c == 't' && escape) {
                buffer.append("\t")
                dollar = false
                escape = false
            } else if (escape) {
                buffer.append("\\" + c)
                dollar = false
                escape = false
            } else {
                buffer.append(c)
                dollar = false
                escape = false
            }
        }
        if (escape) {
            throw ArrayIndexOutOfBoundsException(replaceText.length)
        }
    }


    interface CancellationSignal {

        val isCanceled: Boolean

        fun cancel()
    }


    private class CancellationSignalFake : CancellationSignal {
        override val isCanceled: Boolean
            get() = false

        override fun cancel() {
            throw UnsupportedOperationException("Fake realization of CancellationSignal")
        }
    }

    class CancellationSignalImpl : Regex.CancellationSignal, Cancellable {
        override var isCanceled: Boolean = false
            internal set

        override fun cancel() {
            isCanceled = true
        }
    }

    companion object {
        fun find(text: String,
                 regularExpression: String,
                 flags: Int = 0,
                 listener: RegexListener = { _, _, _, _, _, _, _, _, _ -> },
                 cancellationSignal: CancellationSignal = CancellationSignalFake()) {
            replace(text, regularExpression, "$0", flags, listener, cancellationSignal)
        }

        fun replace(text: String,
                    regularExpression: String,
                    replaceText: String = "$0",
                    flags: Int = 0): String {
            val buffer = StringBuffer()
            replace(text, regularExpression, replaceText, flags,
                    { _, _, _, _, toDst, _, _, _, _ -> buffer.append(toDst) })
            return buffer.toString()
        }

        fun replace(text: String,
                    regularExpression: String,
                    replaceText: String = "$0",
                    flags: Int = 0, listener: RegexListener = { _, _, _, _, _, _, _, _, _ -> },
                    cancellationSignal: CancellationSignal = CancellationSignalFake()) {
            Regex(text, regularExpression, replaceText, flags, listener, cancellationSignal).start()
        }
    }
}