package com.example.notes.notes

import android.util.Log
import com.example.notes.application.NoteApplication
import com.example.notes.database.RoomDatabaseClient
import com.example.notes.models.Note
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

const val TIMEOUT_DURATION_MILLIS = 3000L

class NoteLocalModel @Inject constructor() : INoteModel {

    private var databaseClient =
        RoomDatabaseClient.getInstance(NoteApplication.instance.applicationContext)

    private suspend fun performOperationWithTimeout(function: () -> Unit, callback: SuccessCallback) {
        val job = GlobalScope.async {
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

    override suspend fun addNote(note: Note, callback: SuccessCallback) {
        Log.d("NoteLocalModel", note.toString())
        performOperationWithTimeout({ databaseClient.noteDAO().addNote(note) }, callback)
    }

    override suspend fun updateNote(note: Note, callback: SuccessCallback) {
        Log.d("NoteLocalModel", note.toString())
        performOperationWithTimeout({ databaseClient.noteDAO().updateNote(note) }, callback)
    }

    override suspend fun deleteNote(note: Note, callback: SuccessCallback) {
        Log.d("NoteLocalModel", note.toString())
        performOperationWithTimeout({ databaseClient.noteDAO().deleteNote(note) }, callback)
    }

    override suspend fun retrieveNotes(callback: (List<Note>?) -> Unit) {
        val job = GlobalScope.async {
            withTimeoutOrNull(TIMEOUT_DURATION_MILLIS) {
                databaseClient.noteDAO().retrieveNotes()
            }
        }
        callback.invoke(job.await())
    }
}