package ru.lazard.regexp.model

import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import ru.lazard.kotlin.extensions.get
import ru.lazard.regexp.application.App
import android.os.Environment.DIRECTORY_PICTURES
import android.os.Environment.getExternalStoragePublicDirectory
import android.util.Log
import java.io.File
import java.util.*
import kotlin.coroutines.experimental.EmptyCoroutineContext.plus


/**
 * Created by Egor on 11.04.2017.
 */

data class SelectedFile(val content: String?,val displayName: String?,val mimeType: String?,val uriOrig: Uri) {
    companion object{
        fun load(uriOrig: Uri) : SelectedFile{
            val contentResolver = App.get().contentResolver

            var content = contentResolver.openInputStream(uriOrig).bufferedReader().useLines { it.fold(StringBuilder(), { builder, line -> builder.append(line) }).toString() }


            var displayName :String? =null
            var mimeType :String? ="text/plain"
            val cursor = contentResolver.query(uriOrig, null, null, null, null)
            cursor?.let {
                cursor.use {
                    if (!cursor.moveToFirst()) return@use
                    displayName = cursor[DocumentsContract.Document.COLUMN_DISPLAY_NAME]?:uriOrig.lastPathSegment
                    mimeType = cursor[DocumentsContract.Document.COLUMN_MIME_TYPE]?:"text/plain"
                }
            }
            return SelectedFile(content,displayName,mimeType,uriOrig);
        }
    }
}
