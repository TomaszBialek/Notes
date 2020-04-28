package com.example.notes.tasks

import com.example.notes.models.Task
import com.example.notes.models.Todo
import javax.inject.Inject


class TaskModel @Inject constructor(){

    fun getFakeData(): MutableList<Task> = mutableListOf(
        Task(
            "malaislicznazielonakuleczkatoczysiezgorki", mutableListOf(
                Todo("Test One", true),
                Todo("Test Two")
            )
        ),
        Task("patrzyochoczonawszystkocojestnadole"),
        Task(
            "testing three", mutableListOf(
                Todo("Test A"),
                Todo("Test B")
            )
        )
    )

}