package com.example.praktineuzduotis


import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.praktineuzduotis.ui.theme.PraktineUzduotisTheme

import android.content.ContentValues
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.praktineuzduotis.Data.NotesContract

class MainActivity : AppCompatActivity() {

    private lateinit var Notes: TextView
    private lateinit var AddNote: Button
    private lateinit var noteTitle: EditText
    private lateinit var noteDesctiption: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Notes = findViewById(R.id.Notes)
        AddNote = findViewById(R.id.AddNote)
        noteTitle = findViewById(R.id.noteTitle)
        noteDesctiption = findViewById(R.id.noteDescription)
        AddNote.setOnClickListener {
            insertNote(noteTitle.text.toString(), noteDesctiption.text.toString())
            loadNotes()
        }

        loadNotes()
    }

    private fun insertNote(title: String, content: String) {
        val values = ContentValues().apply {
            put(NotesContract.NotesEntry.COLUMN_TITLE, title)
            put(NotesContract.NotesEntry.COLUMN_CONTENT, content)
        }
        contentResolver.insert(NotesContract.CONTENT_URI, values)
    }

    private fun loadNotes() {
        val cursor = contentResolver.query(
            NotesContract.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        val notesList = StringBuilder()
        cursor?.use {
            val titleIndex = it.getColumnIndexOrThrow(NotesContract.NotesEntry.COLUMN_TITLE)
            val contentIndex = it.getColumnIndexOrThrow(NotesContract.NotesEntry.COLUMN_CONTENT)

            while (it.moveToNext()) {
                val title = it.getString(titleIndex)
                val content = it.getString(contentIndex)
                notesList.append("Title: $title\nContent: $content\n\n")
            }
        }
        Notes.text = notesList.toString()
    }
}