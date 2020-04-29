package com.example.notes.create

import android.content.Context
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.notes.R
import com.example.notes.foundations.StateChangeTextWatcher
import com.example.notes.views.CreateTodoView
import kotlinx.android.synthetic.main.activity_navigation.*
import kotlinx.android.synthetic.main.fragment_create_task.*
import kotlinx.android.synthetic.main.view_create_task.view.*
import kotlinx.android.synthetic.main.view_create_todo.view.*
import java.lang.RuntimeException

class CreateTaskFragment : Fragment() {

    private var listener: OnFragmentInteractionListener? = null

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
                } else if (!previousValue.isNullOrEmpty() && s.isNullOrEmpty()) {
                    removeTodoView(containerView.getChildAt(containerView.childCount - 1))
                }

                super.afterTextChanged(s)
            }
        })
    }

    private fun addTodoView() {
        val view = LayoutInflater.from(context).inflate(R.layout.view_create_todo, containerView, false) as CreateTodoView
        view.todoEditText.addTextChangedListener(object : StateChangeTextWatcher()  {
            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrEmpty() && previousValue.isNullOrEmpty()) {
                    addTodoView()
                } else if (!previousValue.isNullOrEmpty() && s.isNullOrEmpty()) {
                    removeTodoView(view)
                }
                super.afterTextChanged(s)
            }
        })

        containerView.addView(view)
    }

    private fun removeTodoView(view: View) {
        containerView.removeView(view)
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
