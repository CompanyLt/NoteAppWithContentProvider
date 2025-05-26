package com.example.praktineuzduotis.Data

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.util.Log

class NotesContentProvider : ContentProvider() {

    companion object {
        private const val NOTES = 1
        private const val NOTE_ID = 2

        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(NotesContract.AUTHORITY, NotesContract.NotesEntry.TABLE_NAME, NOTES)
            addURI(NotesContract.AUTHORITY, "${NotesContract.NotesEntry.TABLE_NAME}/#", NOTE_ID)
        }
    }

    private lateinit var dbHelper: NotesDatabaseHelper

    override fun onCreate(): Boolean {
        dbHelper = NotesDatabaseHelper(context!!)
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val db = dbHelper.readableDatabase
        val qb = SQLiteQueryBuilder()
        qb.tables = NotesContract.NotesEntry.TABLE_NAME

        when (uriMatcher.match(uri)) {
            NOTES -> {

            }
            NOTE_ID -> {
                val id = uri.lastPathSegment
                qb.appendWhere("${NotesContract.NotesEntry._ID} = $id")
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }

        val cursor = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder)
        cursor.setNotificationUri(context?.contentResolver, uri)
        return cursor
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val db = dbHelper.writableDatabase
        val match = uriMatcher.match(uri)
        if (match != NOTES) {
            throw IllegalArgumentException("Invalid URI for insert: $uri")
        }
        val id = db.insert(NotesContract.NotesEntry.TABLE_NAME, null, values)
        if (id == -1L) {
            throw SQLException("Failed to insert row into $uri")
        }
        val insertedUri = ContentUris.withAppendedId(NotesContract.CONTENT_URI, id)
        context?.contentResolver?.notifyChange(insertedUri, null)
        return insertedUri
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        val db = dbHelper.writableDatabase
        val rowsUpdated: Int = when (uriMatcher.match(uri)) {
            NOTES -> db.update(NotesContract.NotesEntry.TABLE_NAME, values, selection, selectionArgs)
            NOTE_ID -> {
                val id = uri.lastPathSegment
                val sel = "${NotesContract.NotesEntry._ID} = $id" +
                        if (!selection.isNullOrEmpty()) " AND ($selection)" else ""
                db.update(NotesContract.NotesEntry.TABLE_NAME, values, sel, selectionArgs)
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        if (rowsUpdated > 0) {
            context?.contentResolver?.notifyChange(uri, null)
        }
        return rowsUpdated
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        val db = dbHelper.writableDatabase
        val rowsDeleted: Int = when (uriMatcher.match(uri)) {
            NOTES -> db.delete(NotesContract.NotesEntry.TABLE_NAME, selection, selectionArgs)
            NOTE_ID -> {
                val id = uri.lastPathSegment
                val sel = "${NotesContract.NotesEntry._ID} = $id" +
                        if (!selection.isNullOrEmpty()) " AND ($selection)" else ""
                db.delete(NotesContract.NotesEntry.TABLE_NAME, sel, selectionArgs)
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        if (rowsDeleted > 0) {
            context?.contentResolver?.notifyChange(uri, null)
        }
        return rowsDeleted
    }

    override fun getType(uri: Uri): String? {
        return when (uriMatcher.match(uri)) {
            NOTES -> "vnd.android.cursor.dir/vnd.${NotesContract.AUTHORITY}.${NotesContract.NotesEntry.TABLE_NAME}"
            NOTE_ID -> "vnd.android.cursor.item/vnd.${NotesContract.AUTHORITY}.${NotesContract.NotesEntry.TABLE_NAME}"
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }
}