package com.example.notes.notes

import com.example.notes.models.Note
import javax.inject.Inject

class NoteModel @Inject constructor() {

    fun getFakeData(): MutableList<Note> = mutableListOf(
        Note("atosechcialemwymysliccostakiegotomam"),
        Note("jednakulkajestniebieskadrugarozowaobiesamale")
    )

}