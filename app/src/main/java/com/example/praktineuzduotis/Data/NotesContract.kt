package com.example.praktineuzduotis.Data

import android.net.Uri
import android.provider.BaseColumns

object NotesContract {
    const val AUTHORITY = "com.example.praktineuzduotis.provider"
    val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/${NotesEntry.TABLE_NAME}")

    object NotesEntry : BaseColumns { // čia turi būti paveldėjimas
        const val _ID = "_id"
        const val TABLE_NAME = "notes"
        const val COLUMN_TITLE = "title"
        const val COLUMN_CONTENT = "content"
    }
}