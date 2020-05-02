package com.example.notes.tasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.notes.foundations.ApplicationScope
import com.example.notes.models.Task
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
        loadData()
    }

    fun loadData() {
        model.retrieveTasks {nullableList ->
            nullableList?.let {
                _taskListLiveData.postValue(it.toMutableList())
            }
        }
    }

    override fun onTodoUpdate(taskIndex: Int, toDoIndex: Int, isComplete: Boolean) {
        GlobalScope.launch {
            _taskListLiveData.value?.let {
                val todo = it[taskIndex].todos[toDoIndex]
                todo.apply {
                    this.isComplete = isComplete
                    this.taskId = it[taskIndex].uid
                }

                model.updateTodo(todo) {
                    loadData()
                }
            }
        }
    }

    override fun onTaskDeleted(taskIndex: Int) {
        GlobalScope.launch {
            _taskListLiveData.value?.let {
                model.deleteTask(it[taskIndex]) {
                    loadData()
                }
            }
        }
    }

}