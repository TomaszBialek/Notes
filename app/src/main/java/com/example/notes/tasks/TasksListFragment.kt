package com.example.notes.tasks

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notes.R
import kotlinx.android.synthetic.main.fragment_tasks_list.*

class TasksListFragment : Fragment() {

    lateinit var viewModel: TaskViewModel
    lateinit var adapter: TaskAdapter
    lateinit var touchActionDelegate: TouchActionDelegate

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context.let {
            if (it is TouchActionDelegate) {
                touchActionDelegate = it
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tasks_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = TaskAdapter(touchActionDelegate = touchActionDelegate)
        recyclerView.adapter = adapter

        bindViewModel()
    }

    private fun bindViewModel() {
        viewModel = ViewModelProvider(this).get(TaskViewModel::class.java)
        viewModel.taskListLiveData.observe(viewLifecycleOwner, Observer{ taskList ->
            adapter.updateList(taskList)
        })
    }


    companion object {
        fun newInstance(): TasksListFragment {
            val fragment = TasksListFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    interface TouchActionDelegate {
        fun onAddButtonClicked(value: String)
    }
}