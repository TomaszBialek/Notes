package com.example.notes.tasks

interface TaskListViewContract {
    fun onTodoUpdate(taskIndex: Int, toDoIndex: Int, isComplete: Boolean)
}