package com.example.notes.tasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.notes.foundations.ApplicationScope
import com.example.notes.models.Task
import toothpick.Toothpick
import toothpick.config.Module
import javax.inject.Inject

class TaskViewModel : ViewModel(), TaskListViewContract {

    @Inject
    lateinit var model: ITaskModel

    private val _taskListLiveData: MutableLiveData<MutableList<Task>> = MutableLiveData()
    val taskListLiveData: LiveData<MutableList<Task>> = _taskListLiveData

    init {
//        val taskViewModelScope = Toothpick.openScopes(ApplicationScope.scope, this)
//        taskViewModelScope.installModules(Module().apply {
//            bind(ITaskModel::class.java).toInstance(TestModel())
//        })
        Toothpick.inject(this, ApplicationScope.scope)
        _taskListLiveData.postValue(model.getFakeData())
    }

    override fun onTodoUpdate(taskIndex: Int, toDoIndex: Int, isComplete: Boolean) {
        _taskListLiveData.value?.get(taskIndex)?.todos?.get(toDoIndex)?.isComplete = isComplete
    }

}


class TestModel : ITaskModel {
    override fun addTask(task: Task, callback: SuccessCallback) {
        TODO("Not yet implemented")
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

    override fun getFakeData(): MutableList<Task> = mutableListOf(
        Task("Test Model Task")
    )
}