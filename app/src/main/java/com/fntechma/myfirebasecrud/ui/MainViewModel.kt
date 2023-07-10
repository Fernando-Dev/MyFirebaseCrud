package com.fntechma.myfirebasecrud.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fntechma.myfirebasecrud.application.MyFirebaseCrudApp
import com.fntechma.myfirebasecrud.domain.FirebaseDatabaseFailure
import com.fntechma.myfirebasecrud.domain.Note
import com.fntechma.myfirebasecrud.domain.NotePost
import com.fntechma.myfirebasecrud.domain.NoteResponse
import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val myApplication = MyFirebaseCrudApp.getInstance()

    private fun getDatabase(): CollectionReference {
        return myApplication.db.collection("notes")
    }

    private val _addNote = MutableLiveData<NoteResponse>()
    val addNote: LiveData<NoteResponse>
        get() = _addNote

    private val _databaseFailure = MutableLiveData<FirebaseDatabaseFailure>()
    val databaseFailure: LiveData<FirebaseDatabaseFailure>
        get() = _databaseFailure

    fun addNote(notePost: NotePost) {
        CoroutineScope(Dispatchers.IO).launch {
            getDatabase().let { database ->
                database.add(notePost)
                    .addOnSuccessListener {
                        _addNote.value = NoteResponse("Nota salva com sucesso!")
                    }
                    .addOnFailureListener {
                        _databaseFailure.value = FirebaseDatabaseFailure(it.message)
                    }
            }
        }
    }

    private val _readNote = MutableLiveData<ArrayList<Note>>()
    val readNote: LiveData<ArrayList<Note>>
        get() = _readNote

    fun readNote() {
        CoroutineScope(Dispatchers.IO).launch {
            getDatabase().let { database ->
                database.get()
                    .addOnSuccessListener { notes ->
                        val listNote = ArrayList<Note>()
                        for (mNote in notes) {
                            val note = Note(
                                id = mNote.id,
                                note = mNote.getString("note")
                            )
                            listNote.add(note)
                        }
                        _readNote.value = listNote
                    }
                    .addOnFailureListener {
                        _databaseFailure.value = FirebaseDatabaseFailure(it.message)
                    }
            }
        }
    }

    private val _deleteNote = MutableLiveData<Boolean>()
    val deleteNote: LiveData<Boolean> get() = _deleteNote


    fun deleteNote(note: Note) {
        CoroutineScope(Dispatchers.IO).launch {
            getDatabase().let { database ->
                note.id?.let { id ->
                    database.document(id).delete()
                        .addOnSuccessListener {
                            _deleteNote.value = true
                        }
                        .addOnFailureListener {
                            _deleteNote.value = false
                        }
                } ?: run {
                    _databaseFailure.value = FirebaseDatabaseFailure("Id nulo")
                }

            }
        }
    }

    private val _updateNote = MutableLiveData<Boolean>()
    val updateNote: LiveData<Boolean> get() = _updateNote

    fun updateNote(note: Note) {
        CoroutineScope(Dispatchers.IO).launch {
            getDatabase().let { database ->
                note.id?.let { id ->
                    note.note?.let {
                        val map = hashMapOf<String, Any>("note" to it)
                        database.document(id).update(map)
                            .addOnSuccessListener {
                                _updateNote.value = true
                            }
                            .addOnFailureListener {
                                _updateNote.value = false
                            }
                    } ?: run {
                        _databaseFailure.value = FirebaseDatabaseFailure("Nota vazia")
                    }
                } ?: run {
                    _databaseFailure.value = FirebaseDatabaseFailure("Id nulo")
                }
            }
        }
    }


}