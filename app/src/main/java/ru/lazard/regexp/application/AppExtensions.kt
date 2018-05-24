package ru.lazard.regexp.application

import ru.lazard.regexp.manage.RegexManager
import ru.lazard.regexp.manage.SelectedFileManager
import ru.lazard.regexp.manage.SettingsManager

/**
 * Created by Egor on 31.05.2017.
 */


val KEY_REGEX_MANAGER = "KEY_REGEX_MANAGER"
val KEY_SETTINGS_MANAGER = "KEY_SETTINGS_MANAGER"
val KEY_SELECTED_FILE_MANAGER = "KEY_SELECTED_FILE_MANAGER"

private inline fun <reified T> getFromRepositoryOrCreate(key: String, create: (App) -> T): T {
    val app = App.get()
    val repositroy = app.repositroy
    var value = repositroy.get(key) as T?
    if (value == null) {
        value = create.invoke(app)
        repositroy.put(key, value!!)
    }
    return value
}


val <T> T.regexManager: RegexManager
    get() = getFromRepositoryOrCreate(KEY_REGEX_MANAGER) { RegexManager(it, settingsManager) }

val <T> T.settingsManager: SettingsManager
    get() = getFromRepositoryOrCreate(KEY_SETTINGS_MANAGER) { SettingsManager(it) }

val <T> T.selectedFileManager: SelectedFileManager
    get() = getFromRepositoryOrCreate(KEY_SELECTED_FILE_MANAGER) { SelectedFileManager() }