package ru.lazard.regexp.manage

import android.content.Context

import java.util.regex.Pattern

import ru.lazard.regexp.utils.Settings

/**
 * Created by Egor on 19.04.2017.
 */

class SettingsManager(context: Context) : Settings(context) {
    private val KEY_REGEX_FLAGS = "KEY_REGEX_FLAGS"

    var regexFlags: Int
        get() = getInt(KEY_REGEX_FLAGS, Pattern.MULTILINE or Pattern.DOTALL)
        set(flags) = setInt(KEY_REGEX_FLAGS, flags)




}
