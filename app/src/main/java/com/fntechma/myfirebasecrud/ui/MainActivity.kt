package com.fntechma.myfirebasecrud.ui

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.lifecycle.ViewModelProvider
import com.fntechma.myfirebasecrud.R
import com.fntechma.myfirebasecrud.databinding.ActivityMainBinding
import com.fntechma.myfirebasecrud.domain.Note
import com.fntechma.myfirebasecrud.domain.NotePost
import com.fntechma.myfirebasecrud.listener.RecyclerViewClickListener
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity(), RecyclerViewClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private var isUpdate = false
    private lateinit var mNote: Note

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        initData()
        initView()
        registerObserverFailureFirebaseDatabase()
        registerObserverSaveNote()
        registerObserverReadNote()
        registerObserverDeleteNote()
        registerObserverUpdateNote()
    }

    override fun onClick(position: Int, note: Note) {
        mNote = note
        Log.d(javaClass.simpleName, "onClick: ${position} ${note}")
        binding.run {
            if (!isUpdate) {
                edtInput.setText(note.note)
                btnNoteAdd.visibility = GONE
                btnUpdateNote.visibility = VISIBLE
                title.text = "Atualizar nota"
                isUpdate = true
            } else {
                if (note.note == null) {
                    edtInput.setText("")
                    btnNoteAdd.visibility = VISIBLE
                    btnUpdateNote.visibility = GONE
                    title.text = "Adicionar nota"
                } else {
                    edtInput.setText(note.note)
                    btnNoteAdd.visibility = GONE
                    btnUpdateNote.visibility = VISIBLE
                    title.text = "Atualizar nota"
                }
                isUpdate = false
            }
        }
    }

    override fun onLongClick(position: Int, note: Note) {
        val item = note
        Log.d(javaClass.simpleName, "onLongClick: ${position} ${note}")
    }

    override fun onDeleteNote(position: Int, note: Note) {
        viewModel.deleteNote(note)
    }

    private fun initData() {
        viewModel.readNote()
    }

    private fun initView() {
        binding.run {

            // region textinputedittext
            edtInput.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (p0 != null) {
                        if (p0.isEmpty() || p0.isBlank()) {
                            binding.btnUpdateNote.visibility = GONE
                            binding.btnNoteAdd.visibility = VISIBLE
                            title.text = "Adicionar nota"
                        }
                    }
                }

                override fun afterTextChanged(p0: Editable?) {

                }
            })
            //region button
            btnNoteAdd.setOnClickListener {
                if (edtInput.text.toString().isNotBlank()
                    || edtInput.text.toString().isNotEmpty()
                ) {
                    viewModel.addNote(
                        NotePost(
                            edtInput.text.toString()
                        )
                    )
                }
            }

            btnUpdateNote.setOnClickListener {
                if (edtInput.text.toString().isNotBlank()
                    || edtInput.text.toString().isNotEmpty()
                ) {
                    if (::mNote.isInitialized) {
                        mNote.note = edtInput.text.toString()
                        viewModel.updateNote(
                            mNote
                        )
                    }
                }
            }

        }
    }

    private fun registerObserverSaveNote() {
        viewModel.addNote.observe(this) {
            it.message?.let { message ->
                message(message)
                binding.edtInput.setText("")
                viewModel.readNote()
            } ?: run {
                message("Falha em salvar a nota")
            }
        }
    }

    private fun registerObserverFailureFirebaseDatabase() {
        viewModel.databaseFailure.observe(this) {
            it.message?.let { message ->
                message(message)
            } ?: run {
                message("Falha na criação do banco")
            }
        }
    }

    private fun registerObserverReadNote() {
        viewModel.readNote.observe(this) {
            if (it.isNullOrEmpty()) {
                message("Falha na leitura de notas no banco")
            } else {
                val mainAdapter = MainAdapter(it, this)
                binding.rvNotes.adapter = mainAdapter
                updateRecyclerView()
            }
        }
    }

    private fun registerObserverDeleteNote() {
        viewModel.deleteNote.observe(this) {
            if (it) {
                message("Nota removida com sucesso!")
                viewModel.readNote()
            } else {
                message("Falha ao remover nota!")
            }
        }
    }

    private fun registerObserverUpdateNote() {
        viewModel.updateNote.observe(this) {
            if (it) {
                message("Nota atualizada com sucesso!")
                viewModel.readNote()
            } else {
                message("Falha ao atualizar a nota!")
            }
            isUpdate = false
        }
    }

    private fun message(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateRecyclerView() {
        binding.rvNotes.adapter?.notifyDataSetChanged()
    }

}