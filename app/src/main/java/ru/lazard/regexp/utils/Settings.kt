package ru.lazard.regexp.utils

/**************************************************************************
 * TODO copyright

 * $Id: Settings.java 45 2012-04-24 09:36:19Z tepikin $
 * $HeadURL: svn://rcs/android-commons/common_lib/trunk/EnterraCommons/src/com/enterra/android/lib/commons/settings/Settings.java $
 */

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
private val SETTINGS_FILE_NAME = "settings"
/**
 * Class for keep settings at application. <br></br>
 * Wrapper for [SharedPreferences].
 */
open class Settings {

    /**
     * Get SharedPreferences object.

     * @return
     */
    lateinit var preferences: SharedPreferences
        private set

    /**
     * Get SharedPreferences.Editor object.

     * @return
     */
    lateinit var preferencesEditor: SharedPreferences.Editor
        private set

    /**
     * Constructor.

     * @param context    - context of application.
     * *
     * @param fileName   - name of ".xml" file with shared preferences.
     * *
     * @param accessMode - access mode - one of (Context.MODE_APPEND,
     * *                   Context.MODE_PRIVATE, Context.MODE_WORLD_READABLE,
     * *                   Context.MODE_WORLD_WRITEABLE)
     */
    @JvmOverloads constructor(context: Context?, fileName: String? = SETTINGS_FILE_NAME, accessMode: Int = Context.MODE_PRIVATE) : super() {
        if (context == null) {
            throw IllegalArgumentException("Context should not be null")
        }
        if (fileName == null || fileName.length <= 0) {
            throw IllegalArgumentException("fileName should not be null")
        }
        if (accessMode != Context.MODE_APPEND
                && accessMode != Context.MODE_PRIVATE
                && accessMode != Context.MODE_WORLD_READABLE
                && accessMode != Context.MODE_WORLD_WRITEABLE) {
            throw IllegalArgumentException(
                    "accessMode should be is Context.MODE_APPEND, Context.MODE_PRIVATE, Context.MODE_WORLD_READABLE, Context.MODE_WORLD_WRITEABLE.")
        }
        this.preferences = context.getSharedPreferences(fileName, accessMode)
        preferencesEditor = preferences.edit()
    }

    /**
     * Constructor.

     * @param preferences - [SharedPreferences] instance.
     */
    constructor(preferences: SharedPreferences?) : super() {
        if (preferences == null) {
            throw IllegalArgumentException(
                    "SharedPreferences should not be null")
        }
        this.preferences = preferences
        preferencesEditor = preferences.edit()
    }

    /**
     * Add listener for settings changes.

     * @param listener
     */
    fun addListener(listener: OnSharedPreferenceChangeListener) {
        preferences.registerOnSharedPreferenceChangeListener(listener)
    }

    /**
     * Get boolean value from settings by key.

     * @param key
     * *
     * @param defValue
     * *
     * @return
     */
    fun getBoolean(key: String, defValue: Boolean): Boolean {
        return preferences.getBoolean(key, defValue)
    }

    /**
     * Get float value from settings by key.

     * @param key
     * *
     * @param defValue
     * *
     * @return
     */
    fun getFloat(key: String, defValue: Float): Float {
        return preferences.getFloat(key, defValue)
    }

    /**
     * Get int value from settings by key.

     * @param key
     * *
     * @param defValue
     * *
     * @return
     */
    fun getInt(key: String, defValue: Int): Int {
        return preferences.getInt(key, defValue)
    }

    /**
     * Get long value from settings by key.

     * @param key
     * *
     * @param defValue
     * *
     * @return
     */
    fun getLong(key: String, defValue: Long): Long {
        return preferences.getLong(key, defValue)
    }

    /**
     * Get String value from settings by key.

     * @param key
     * *
     * @param defValue
     * *
     * @return
     */
    fun getString(key: String, defValue: String): String {
        return preferences.getString(key, defValue)
    }

    /**
     * Remove listener by settings changes.

     * @param listener
     */
    fun removeListener(listener: OnSharedPreferenceChangeListener) {
        preferences.unregisterOnSharedPreferenceChangeListener(listener)
    }

    /**
     * Set boolean value from settings by key.

     * @param key
     * *
     * @param value
     */
    fun setBoolean(key: String, value: Boolean) {
        val editor = preferencesEditor
        editor.putBoolean(key, value)
        editor.commit()
    }

    /**
     * Set float value from settings by key.

     * @param key
     * *
     * @param value
     */
    fun setFloat(key: String, value: Float) {
        val editor = preferencesEditor
        editor.putFloat(key, value)
        editor.commit()
    }

    /**
     * Set int value from settings by key.

     * @param key
     * *
     * @param value
     */
    fun setInt(key: String, value: Int) {
        val editor = preferencesEditor
        editor.putInt(key, value)
        editor.commit()
    }

    /**
     * Set long value from settings by key.

     * @param key
     * *
     * @param value
     */
    fun setLong(key: String, value: Long) {
        val editor = preferencesEditor
        editor.putLong(key, value)
        editor.commit()
    }

    /**
     * Set String value from settings by key.

     * @param key
     * *
     * @param value
     */
    fun setString(key: String, value: String) {
        val editor = preferencesEditor
        editor.putString(key, value)
        editor.commit()
    }




}
/**
 * Constructor.

 * @param context - context of application.
 */
/**
 * Constructor.

 * @param context  - context of application.
 * *
 * @param fileName - name of ".xml" file with shared preferences.
 */