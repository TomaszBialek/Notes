package com.example.notes.create

import android.content.Context
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.notes.R
import com.example.notes.foundations.ApplicationScope
import com.example.notes.foundations.NullFieldChecker
import com.example.notes.foundations.StateChangeTextWatcher
import com.example.notes.models.Task
import com.example.notes.models.Todo
import com.example.notes.tasks.ITaskModel
import com.example.notes.tasks.TaskLocalModel
import com.example.notes.views.CreateTodoView
import kotlinx.android.synthetic.main.activity_navigation.*
import kotlinx.android.synthetic.main.fragment_create_task.*
import kotlinx.android.synthetic.main.view_create_task.view.*
import kotlinx.android.synthetic.main.view_create_todo.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import toothpick.Toothpick
import java.lang.RuntimeException
import javax.inject.Inject

private const val MAX_TODOS_COUNT = 5

class CreateTaskFragment : Fragment() {

    @Inject
    lateinit var model: ITaskModel

    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Toothpick.inject(this, ApplicationScope.scope)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createTaskView.taskEditText.addTextChangedListener(object: StateChangeTextWatcher() {
            override fun afterTextChanged(s: Editable?) {

                if (!s.isNullOrEmpty() && previousValue.isNullOrEmpty()) {
                    addTodoView()
                }
//                else if (!previousValue.isNullOrEmpty() && s.isNullOrEmpty()) {
//                    removeTodoView(containerView.getChildAt(containerView.childCount - 1))
//                }

                super.afterTextChanged(s)
            }
        })
    }

    private fun addTodoView() {
        if(canAddTodos()) {
            val view = (LayoutInflater.from(context).inflate(R.layout.view_create_todo, containerView, false) as CreateTodoView).apply {
                todoEditText.addTextChangedListener(object : StateChangeTextWatcher()  {
                    override fun afterTextChanged(s: Editable?) {
                        if (!s.isNullOrEmpty() && previousValue.isNullOrEmpty()) {
                            addTodoView()
                        } else if (!previousValue.isNullOrEmpty() && s.isNullOrEmpty()) {
                            removeTodoView(this@apply)
                            if(containerView.childCount == MAX_TODOS_COUNT) {
                                addTodoView()
                            }
                        }
                        super.afterTextChanged(s)
                    }
                })
            }
            containerView.addView(view)
        }
    }

    private fun removeTodoView(view: View) {
        containerView.removeView(view)
    }

    private fun canAddTodos(): Boolean = (containerView.childCount < MAX_TODOS_COUNT + 1) &&
            !(containerView.getChildAt(containerView.childCount - 1) as NullFieldChecker).hasNullField()

    private fun isTaskEmpty(): Boolean = createTaskView.taskEditText.editableText.isNullOrEmpty()

    fun saveTask(callback: (Boolean) -> Unit) {
        GlobalScope.launch {
            createTask()?.let { task ->
                model.addTask(task) { success ->
                    callback.invoke(success)
                }
            } ?: callback.invoke(false)
        }
    }

    fun createTask(): Task? {
        if(!isTaskEmpty()) {
            containerView.run {

                var taskField: String? = null
                val todoList: MutableList<Todo> = mutableListOf()
                for (i in 0 until containerView.childCount) {
                    if (i == 0) {
                        taskField = containerView.getChildAt(i).taskEditText.editableText?.toString()
                    } else {
                        if (!containerView.getChildAt(i).todoEditText.editableText.isNullOrEmpty()){
                            todoList.add(
                                Todo(description = containerView.getChildAt(i).todoEditText.editableText.toString())
                            )
                        }
                    }
                }
                return taskField?.let {
                    Task(it, todoList)
                }
            }
        } else {
            return null
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction()
    }

    companion object {
        fun newInstance() = CreateTaskFragment()
    }
}
