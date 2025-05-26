package com.example.praktineuzduotis.Data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

class NotesDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val sql = """
            CREATE TABLE ${NotesContract.NotesEntry.TABLE_NAME} (
           ${NotesContract.NotesEntry._ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${NotesContract.NotesEntry.COLUMN_TITLE} TEXT NOT NULL,
                ${NotesContract.NotesEntry.COLUMN_CONTENT} TEXT
            )
        """.trimIndent()
        db.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${NotesContract.NotesEntry.TABLE_NAME}")
        onCreate(db)
    }

    companion object {
        const val DATABASE_NAME = "notes.db"
        const val DATABASE_VERSION = 1
    }
}