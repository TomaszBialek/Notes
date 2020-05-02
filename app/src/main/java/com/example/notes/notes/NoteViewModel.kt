package com.example.notes.notes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.notes.foundations.ApplicationScope
import com.example.notes.models.Note
import com.example.notes.models.Task
import com.example.notes.tasks.ITaskModel
import com.example.notes.tasks.SuccessCallback
import toothpick.Toothpick
import toothpick.config.Module
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
        _noteListLiveData.postValue(model.retrieveNotes())
    }

    override fun onDeleteNote(note: Note) {
        model.deleteNote(note) {
            if(it) {
                loadData()
            }
        }
    }
}