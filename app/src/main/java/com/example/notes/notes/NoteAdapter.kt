package com.example.notes.notes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.notes.R
import com.example.notes.foundations.BaseRecyclerAdapter
import com.example.notes.models.Note
import com.example.notes.navigation.NavigationActivity
import com.example.notes.views.NoteView
import kotlinx.android.synthetic.main.view_add_button.view.*

class NoteAdapter(
    notes: MutableList<Note> = mutableListOf(),
    val touchActionDelegate: NoteListFragment.TouchActionDelegate
) : BaseRecyclerAdapter<Note>(notes) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        if (viewType == TYPE_ADD_BUTTON) {
            AddButtonViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.view_add_button, parent, false)
            )
        } else {
            NoteViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
            )
        }

    class NoteViewHolder(view: View) : BaseViewHolder<Note>(view) {
        override fun onBind(data: Note, listIndex: Int) {
            (view as NoteView).initView(data)
        }
    }

    inner class AddButtonViewHolder(view: View) : BaseRecyclerAdapter.AddButtonViewHolder(view) {
        override fun onBind(data: Unit, listIndex: Int) {
            view.buttonText.text = view.context.getText(R.string.add_button_note)

            view.setOnClickListener {
                touchActionDelegate.onAddButtonClicked(NavigationActivity.FRAGMENT_VALUE_NOTE)
            }
        }
    }
}