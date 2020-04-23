package com.example.notes.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notes.R
import com.example.notes.models.Task
import com.example.notes.models.Todo
import kotlinx.android.synthetic.main.fragment_tasks_list.*

class TasksListFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
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
        val adapter = TaskAdapter(
            mutableListOf(
                Task("malaislicznazielonakuleczkatoczysiezgorki", mutableListOf(
                    Todo("Test One", true),
                    Todo("Test Two")
                )),
                Task("patrzyochoczonawszystkocojestnadole")
            )
        )
        recyclerView.adapter = adapter
    }


    companion object {
        fun newInstance(): TasksListFragment {
            val fragment = TasksListFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}