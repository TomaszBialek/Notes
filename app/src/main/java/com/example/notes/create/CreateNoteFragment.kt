package com.example.notes.create

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.notes.R
import com.example.notes.foundations.ApplicationScope
import com.example.notes.foundations.NullFieldChecker
import com.example.notes.models.Note
import com.example.notes.notes.INoteModel
import kotlinx.android.synthetic.main.fragment_create_note.*
import toothpick.Toothpick
import javax.inject.Inject

class CreateNoteFragment : Fragment(), NullFieldChecker {

    @Inject
    lateinit var model: INoteModel

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
        return inflater.inflate(R.layout.fragment_create_note, container, false)
    }

    fun saveNote(callback: (Boolean) -> Unit) {

        createNote()?.let {
            model.addNote(it) {
                callback.invoke(true)
            }
        } ?: callback.invoke(false)
    }

    private fun createNote(): Note? = if (!hasNullField()) {
        Note(description = noteEditText.editableText.toString())
    } else {
        null
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
        fun newInstance() = CreateNoteFragment()
    }

    override fun hasNullField(): Boolean = noteEditText.editableText.isNullOrEmpty()
}
