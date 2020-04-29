package com.example.notes.tasks

import com.example.notes.models.Task

typealias SuccessCallback = (Boolean) -> Unit

interface ITaskModel {

    fun addTask(task: Task, callback: SuccessCallback)
    fun updateTask(task: Task, callback: SuccessCallback)
    fun deleteTask(task: Task, callback: SuccessCallback)
    fun retrieveTasks() : List<Task>

    fun getFakeData(): MutableList<Task>
}