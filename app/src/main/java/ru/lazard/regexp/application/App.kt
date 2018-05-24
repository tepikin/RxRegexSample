package ru.lazard.regexp.application

import android.app.Application


import ru.lazard.regexp.manage.SelectedFileManager

/**
 * Created by Egor on 03.11.2016.
 */


class App : Application() {

    val repositroy: MutableMap<String, Any> = HashMap<String, Any>()

    override fun onCreate() {
        instance = this

        selectedFileManager.selectedFile

        super.onCreate()
    }

    companion object {
        lateinit private var instance: App

        @JvmStatic
        fun get(): App {
            return App.instance;
        }

    }

}
