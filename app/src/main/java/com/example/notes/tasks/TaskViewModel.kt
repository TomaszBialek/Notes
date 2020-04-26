package com.example.notes.tasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.notes.models.Task

class TaskViewModel : ViewModel(), TaskListViewContract {

    private val model: TaskModel = TaskModel()

    private val _taskListLiveData: MutableLiveData<MutableList<Task>> = MutableLiveData()
    val taskListLiveData: LiveData<MutableList<Task>> = _taskListLiveData

    init {
        _taskListLiveData.postValue(model.getFakeData())
    }

    override fun onTodoUpdate(taskIndex: Int, toDoIndex: Int, isComplete: Boolean) {
        _taskListLiveData.value?.get(taskIndex)?.todos?.get(toDoIndex)?.isComplete = isComplete
    }

}