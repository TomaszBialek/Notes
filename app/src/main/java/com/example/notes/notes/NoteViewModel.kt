package com.example.notes.notes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.notes.models.Note

class NoteViewModel : ViewModel() {

    private val _noteListLiveData: MutableLiveData<List<Note>> = MutableLiveData();
    val noteListLiveData: LiveData<List<Note>> = _noteListLiveData

    init {
        _noteListLiveData.postValue(getFakeData())
    }

    fun getFakeData(): MutableList<Note> = mutableListOf(
        Note("atosechcialemwymysliccostakiegotomam"),
        Note("jednakulkajestniebieskadrugarozowaobiesamale")
    )
}