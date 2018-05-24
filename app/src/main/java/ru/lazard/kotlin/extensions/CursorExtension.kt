package ru.lazard.kotlin.extensions

import android.database.Cursor

/**
 * Created by Egor on 31.05.2017.
 */


inline operator fun Cursor.get(i : Int):String? =     try{getString(i)}catch (e :Throwable){null;}
inline operator fun Cursor.get(columnName : String):String? =     try{getString(getColumnIndex(columnName ))}catch (e:Throwable){null}

fun Cursor.forEachRow(action: (Cursor) -> Unit) {
    if (moveToFirst()) {
        do {
            action(this)
        } while (moveToNext())
    }
}