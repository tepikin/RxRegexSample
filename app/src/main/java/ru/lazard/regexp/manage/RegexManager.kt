package ru.lazard.regexp.manage

import android.content.Context
import android.text.TextUtils

import java.util.regex.Pattern

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import ru.lazard.rxregex.OnAppend
import ru.lazard.rxregex.RxRegex

/**
 * Created by Egor on 19.04.2017.
 */

class RegexManager(val context: Context,
                   val settingsManager: SettingsManager = SettingsManager(context)) {

    var regex = "text"
        set(regex) {
            if (regex == this.regex) return
            field = regex
            publishSubject.onNext(Event.regex)
        }

    var replacement = "_$0_"
        set(replacement) {
            if (replacement == this.replacement) return
            field = replacement
            publishSubject.onNext(Event.replacement)
        }

    var flags: Int = settingsManager.regexFlags
        set(flags) {
            if (flags == this.flags) return
            field = flags
            settingsManager.regexFlags = flags
            publishSubject.onNext(Event.flags)
        }

    private val publishSubject = PublishSubject.create<Event>()
    val events: Observable<Event> = publishSubject


    fun find(input: String): Observable<OnAppend> {
        return RxRegex.replace(input, this.regex, "$0", flags)
    }

    fun replace(input: String): Observable<OnAppend> {
        return RxRegex.replace(input, this.regex, this.replacement, flags)
    }

    enum class Event {
        flags, regex, replacement
    }
}
