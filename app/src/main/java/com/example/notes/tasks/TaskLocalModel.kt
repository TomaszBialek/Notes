package com.example.notes.tasks

import android.util.Log
import com.example.notes.application.NoteApplication
import com.example.notes.database.RoomDatabaseClient
import com.example.notes.models.Task
import com.example.notes.models.Todo
import com.example.notes.notes.TIMEOUT_DURATION_MILLIS
import kotlinx.coroutines.*
import javax.inject.Inject


class TaskLocalModel @Inject constructor() : ITaskModel {

    private var databaseClient = RoomDatabaseClient.getInstance(NoteApplication.instance.applicationContext)

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

    override suspend fun addTask(task: Task, callback: SuccessCallback) {
        Log.d("TaskLocalModel", task.toString())

        val masterJob = GlobalScope.async {
            try {
                databaseClient.taskDAO().addTask(task)
            } catch (e: Exception) {
                callback.invoke(false)
            }
            addTodos(task)
        }

        masterJob.await()
        callback.invoke(true)
    }

    override suspend fun updateTask(task: Task, callback: SuccessCallback) {
        Log.d("TaskLocalModel", task.toString())
        performOperationWithTimeout({ databaseClient.taskDAO().updateTask(task) }, callback)
    }

    override suspend fun updateTodo(todo: Todo, callback: SuccessCallback) {
        Log.d("TaskLocalModel", todo.toString())
        performOperationWithTimeout({ databaseClient.taskDAO().updateTodo(todo) }, callback)
    }

    override suspend fun deleteTask(task: Task, callback: SuccessCallback) {
        Log.d("TaskLocalModel", task.toString())
        performOperationWithTimeout({ databaseClient.taskDAO().deleteTask(task) }, callback)
    }

    private fun addTodos(task: Task): Job = GlobalScope.async {
        task.todos.forEach { todo ->
            databaseClient.taskDAO().addTodo(todo)
        }
    }

    override fun retrieveTasks(callback: (List<Task>?) -> Unit) {
        GlobalScope.launch {
            val job = async {
                withTimeoutOrNull(TIMEOUT_DURATION_MILLIS) {
                    databaseClient.taskDAO().retrieveTasks()
                }
            }
            callback.invoke(job.await())
        }
    }
}