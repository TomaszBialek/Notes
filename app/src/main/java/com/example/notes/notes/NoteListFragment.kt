package com.example.notes.notes

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.R
import com.example.notes.models.Note
import com.example.notes.tasks.TasksListFragment
import kotlinx.android.synthetic.main.fragment_note_list.*
import kotlinx.android.synthetic.main.fragment_tasks_list.*
import kotlinx.android.synthetic.main.fragment_tasks_list.recyclerView

class NoteListFragment : Fragment() {

    lateinit var viewModel: NoteViewModel
    lateinit var touchActionDelegate: TouchActionDelegate
    lateinit var adapter: NoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
        }
    }

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
        return inflater.inflate(R.layout.fragment_note_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = NoteAdapter(touchActionDelegate = touchActionDelegate)
        recyclerView.adapter = adapter

        bindViewModel()
    }

    fun bindViewModel() {
        viewModel = ViewModelProvider(this).get(NoteViewModel::class.java)

        viewModel.noteListLiveData.observe(viewLifecycleOwner, Observer { noteList ->
            adapter.updateList(noteList)
        })
    }

    companion object {
        fun newInstance(): NoteListFragment {
            val fragment = NoteListFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }


    interface TouchActionDelegate {
        fun onAddButtonClicked(value: String)
    }

}