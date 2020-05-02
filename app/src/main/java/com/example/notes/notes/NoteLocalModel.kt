package com.example.notes.notes

import android.util.Log
import com.example.notes.application.NoteApplication
import com.example.notes.database.RoomDatabaseClient
import com.example.notes.models.Note
import javax.inject.Inject

class NoteLocalModel @Inject constructor() : INoteModel {

    private var databaseClient = RoomDatabaseClient.getInstance(NoteApplication.instance.applicationContext)

    override fun getFakeData(): MutableList<Note> = retrieveNotes().toMutableList()

//        mutableListOf(
//        Note(description = "atosechcialemwymysliccostakiegotomam"),
//        Note(description = "jednakulkajestniebieskadrugarozowaobiesamale")
//    )

    override fun addNote(note: Note, callback: SuccessCallback) {
        Log.d("NoteLocalModel", note.toString())
        databaseClient.noteDAO().addNote(note)
        callback.invoke(true)
    }

    override fun updateNote(note: Note, callback: SuccessCallback) {
        Log.d("NoteLocalModel", note.toString())
        databaseClient.noteDAO().updateNote(note)
        callback.invoke(true)
    }

    override fun deleteNote(note: Note, callback: SuccessCallback) {
        Log.d("NoteLocalModel", note.toString())
        databaseClient.noteDAO().deleteNote(note)
        callback.invoke(true)
    }

    override fun retrieveNotes(): List<Note> = databaseClient.noteDAO().retrieveNotes()

}