package com.example.notes.notes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.notes.foundations.ApplicationScope
import com.example.notes.models.Note
import toothpick.Toothpick
import javax.inject.Inject

class NoteViewModel : ViewModel(), NoteListViewContract {

    @Inject
    lateinit var model: INoteModel

    private val _noteListLiveData: MutableLiveData<List<Note>> = MutableLiveData()
    val noteListLiveData: LiveData<List<Note>> = _noteListLiveData

    init {
        Toothpick.inject(this, ApplicationScope.scope)
        loadData()
    }

    fun loadData() {
        model.retrieveNotes { nullableList ->
            nullableList?.let {
                _noteListLiveData.postValue(it)
            }
        }
    }

    override fun onDeleteNote(note: Note) {
        model.deleteNote(note) {
            if (it) {
                loadData()
            }
        }
    }
}