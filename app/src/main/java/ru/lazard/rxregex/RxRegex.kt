package ru.lazard.rxregex

import io.reactivex.Observable

/**
 * Class for work with Regex in Reactive style.
 *
 *
 * For create Observable use methods `RxRegex.replace` and `RxRegex.find`.
 * It's `Disposable` objects and you can use it for stop parsing process,
 * you can use method `dispose()` or just unsubscribe from Observable and parsing stops automatically.
 * <pre>`RxRegex.replace("Long text !12! for parsing !AB!", "!..!", "ABCD")
 * .subscribe(replace -> log(replace.toString()));  // logs out:
 * //    Long text   -> Long text
 * //    !12!        -> ABCD
 * //    for parsing -> for parsing
 * //    !AB!        -> ABCD
`</pre> *
 */

object RxRegex {

    /**
     * Create Observable for find parts matched to regex, without replace.

     * @param regex The regular expression
     * *
     * @param flags Match flags, a bit mask that may include
     * *              [java.util.regex.Pattern.CASE_INSENSITIVE], [java.util.regex.Pattern.MULTILINE], [java.util.regex.Pattern.DOTALL],
     * *              [java.util.regex.Pattern.UNICODE_CASE], [java.util.regex.Pattern.CANON_EQ], [java.util.regex.Pattern.UNIX_LINES],
     * *              [java.util.regex.Pattern.LITERAL], [java.util.regex.Pattern.UNICODE_CHARACTER_CLASS]
     * *              and [java.util.regex.Pattern.COMMENTS]
     * *
     * @throws IllegalArgumentException               If bit values other than those corresponding to the defined
     * *                                                match flags are set in <tt>flags</tt>
     * *
     * @throws java.util.regex.PatternSyntaxException If the expression's syntax is invalid
     */
    fun find(text: String, regex: String, flags: Int = 0): Observable<OnAppend> {
        return replace(text, regex,"$0", flags)
        }


    /**
     * Create Observable for Regex replace process.

     * @param text        The character sequence to be matched
     * *
     * @param regex       The regular expression
     * *
     * @param replacement Replacement text. Support groups $0-$9 and \n \r \t chars.
     * *
     * @param flags       Match flags, a bit mask that may include
     * *                    [java.util.regex.Pattern.CASE_INSENSITIVE], [java.util.regex.Pattern.MULTILINE], [java.util.regex.Pattern.DOTALL],
     * *                    [java.util.regex.Pattern.UNICODE_CASE], [java.util.regex.Pattern.CANON_EQ], [java.util.regex.Pattern.UNIX_LINES],
     * *                    [java.util.regex.Pattern.LITERAL], [java.util.regex.Pattern.UNICODE_CHARACTER_CLASS]
     * *                    and [java.util.regex.Pattern.COMMENTS]
     * *
     * @throws IllegalArgumentException               If bit values other than those corresponding to the defined
     * *                                                match flags are set in <tt>flags</tt>
     * *
     * @throws java.util.regex.PatternSyntaxException If the expression's syntax is invalid
     */
    fun replace(text: String, regex: String, replacement: String = "$0", flags: Int = 0): Observable<OnAppend> {
        return Observable.create<OnAppend> { emitter ->
            val cancellationSignal = Regex.CancellationSignalImpl()
            emitter.setCancellable(cancellationSignal)
            Regex.replace(text, regex, replacement, flags,
                    { fromSrc, toSrc, appendSrc, fromDst, toDst, appendDst, isMatched, progress, matchedCount -> emitter.onNext(OnAppend(fromSrc, toSrc, appendSrc, fromDst, toDst, appendDst, isMatched, progress, matchedCount)) }, cancellationSignal)
            emitter.onComplete()
        }
    }




}

/**
 * Class received in OnNext() method of Observer. Contains info about current parsed text part.
 *
 *
 * @param fromSrc      Start position of current part at original text
 * @param toSrc        End position of current part at original text
 * @param appendSrc    Current processed text part from original text
 * @param fromDst      Start position of current part at replaced text
 * @param toDst        End position of current part at replaced text
 * @param appendDst    Replaced text part
 * @param isMatched    Is current part matched to regex
 * @param progress     Current parsing progress (float from 0 - to 1)
 * @param matchedCount Count of matched perts at this moment
 */
class OnAppend(
        val fromSrc: Int,
        val toSrc: Int,
        val appendSrc: String,
        val fromDst: Int,
        val toDst: Int,
        val appendDst: String,
        val isMatched: Boolean,
        val progress: Float,
        val matchedCount: Int) {

    override fun toString(): String {
        return appendSrc + " -> " + appendDst
    }
}