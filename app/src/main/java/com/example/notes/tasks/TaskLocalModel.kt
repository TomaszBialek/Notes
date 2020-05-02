package com.example.notes.tasks

import android.util.Log
import com.example.notes.application.NoteApplication
import com.example.notes.database.RoomDatabaseClient
import com.example.notes.models.Task
import com.example.notes.models.Todo
import javax.inject.Inject


class TaskLocalModel @Inject constructor() : ITaskModel {

    private var databaseClient = RoomDatabaseClient.getInstance(NoteApplication.instance.applicationContext)

    override fun getFakeData(): MutableList<Task> = mutableListOf(
        Task(
            "malaislicznazielonakuleczkatoczysiezgorki", mutableListOf(
                Todo(description = "Test One", isComplete = true),
                Todo(description = "Test Two")
            )
        ),
        Task("patrzyochoczonawszystkocojestnadole"),
        Task(
            "testing three", mutableListOf(
                Todo(description = "Test A"),
                Todo(description = "Test B")
            )
        )
    )

    override fun addTask(task: Task, callback: SuccessCallback) {
        Log.d("TaskLocalModel", task.toString())
        callback.invoke(true)
    }

    override fun updateTask(task: Task, callback: SuccessCallback) {
        TODO("Not yet implemented")
    }

    override fun deleteTask(task: Task, callback: SuccessCallback) {
        TODO("Not yet implemented")
    }

    override fun retrieveTasks(): List<Task> {
        TODO("Not yet implemented")
    }

}