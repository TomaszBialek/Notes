package com.example.notes.notes

import android.util.Log
import com.example.notes.application.NoteApplication
import com.example.notes.database.RoomDatabaseClient
import com.example.notes.models.Note
import kotlinx.coroutines.*
import javax.inject.Inject

const val TIMEOUT_DURATION_MILLIS = 3000L

class NoteLocalModel @Inject constructor() : INoteModel {

    private var databaseClient =
        RoomDatabaseClient.getInstance(NoteApplication.instance.applicationContext)

    private fun performOperationWithTimeout(function: () -> Unit, callback: SuccessCallback) {
        GlobalScope.launch {
            val job = async {
                try {
                    withTimeout(TIMEOUT_DURATION_MILLIS) {
                        function.invoke()
                    }
                } catch (e: java.lang.Exception) {
                    callback.invoke(false)
                }
            }
            job.await()
            callback.invoke(true)
        }
    }

    override fun addNote(note: Note, callback: SuccessCallback) {
        Log.d("NoteLocalModel", note.toString())
        performOperationWithTimeout({ databaseClient.noteDAO().addNote(note) }, callback)
    }

    override fun updateNote(note: Note, callback: SuccessCallback) {
        Log.d("NoteLocalModel", note.toString())
        performOperationWithTimeout({ databaseClient.noteDAO().updateNote(note) }, callback)
    }

    override fun deleteNote(note: Note, callback: SuccessCallback) {
        Log.d("NoteLocalModel", note.toString())
        performOperationWithTimeout({ databaseClient.noteDAO().deleteNote(note) }, callback)
    }

    override fun retrieveNotes(callback: (List<Note>?) -> Unit) {
        GlobalScope.launch {
            val job = async {
                withTimeoutOrNull(TIMEOUT_DURATION_MILLIS) {
                    databaseClient.noteDAO().retrieveNotes()
                }
            }
            callback.invoke(job.await())
        }
    }
}