package com.example.notes.tasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.notes.models.Task
import com.example.notes.models.Todo

class TaskViewModel : ViewModel() {

    private val _taskListLiveData: MutableLiveData<MutableList<Task>> = MutableLiveData()
    val taskListLiveData: LiveData<MutableList<Task>> = _taskListLiveData

    init {
        _taskListLiveData.postValue(getFakeData())
    }

    private fun getFakeData(): MutableList<Task> = mutableListOf(
        Task("malaislicznazielonakuleczkatoczysiezgorki", mutableListOf(
            Todo("Test One", true),
            Todo("Test Two")
        )),
        Task("patrzyochoczonawszystkocojestnadole"),
        Task("testing three", mutableListOf(
            Todo("Test A"),
            Todo("Test B")
        ))
    )

}