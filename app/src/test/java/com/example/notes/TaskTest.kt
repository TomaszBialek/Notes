package com.example.notes

import com.example.notes.models.Task
import com.example.notes.models.Todo
import org.junit.Before
import org.junit.Test

class TaskTest {

    lateinit var testTask: Task

    @Before
    fun seUpBeforeTest() {
        testTask = getFakeTask()
    }

    @Test
    fun taskIsIncompleteOnInit() {
        assert(!testTask.isComplete())
    }

    @Test
    fun taskIsCompleteAfterAllTodosChecked() {
        testTask.todos.forEach {
            it.isComplete= true
        }

        assert(testTask.isComplete())
    }

    fun getFakeTask(): Task = Task(
        title = "Testing Task",
        todos = getFakeTodos()
    )

    fun getFakeTodos(): MutableList<Todo> = mutableListOf(
        Todo(description = "todo one", isComplete = true),
        Todo(description = "todo two"),
        Todo(description = "todo three")
    )
}