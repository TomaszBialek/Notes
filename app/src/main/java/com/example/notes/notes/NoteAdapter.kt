package com.example.notes.notes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.notes.R
import com.example.notes.foundations.BaseRecyclerAdapter
import com.example.notes.models.Note
import kotlinx.android.synthetic.main.item_note.view.*

class NoteAdapter(
    notes: MutableList<Note> = mutableListOf()
) : BaseRecyclerAdapter<Note>(notes) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false))

    class ViewHolder(view: View) : BaseRecyclerAdapter.ViewHolder<Note>(view) {
        override fun onBind(note: Note) {
            view.descriptionView.text = note.description
        }
    }
}