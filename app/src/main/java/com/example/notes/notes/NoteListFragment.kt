package com.example.notes.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.R
import com.example.notes.models.Note
import kotlinx.android.synthetic.main.fragment_note_list.*
import kotlinx.android.synthetic.main.fragment_tasks_list.*
import kotlinx.android.synthetic.main.fragment_tasks_list.recyclerView

class NoteListFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
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
        val adapter = NoteAdapter(
            mutableListOf(
                Note("atosechcialemwymysliccostakiegotomam"),
                Note("jednakulkajestniebieskadrugarozowaobiesamale")
        ))
        recyclerView.adapter = adapter
    }

    companion object {
        fun newInstance(): NoteListFragment {
            val fragment = NoteListFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}