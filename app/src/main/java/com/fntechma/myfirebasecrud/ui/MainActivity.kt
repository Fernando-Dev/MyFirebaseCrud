package com.fntechma.myfirebasecrud.ui

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.fntechma.myfirebasecrud.databinding.ActivityMainBinding
import com.fntechma.myfirebasecrud.domain.Note
import com.fntechma.myfirebasecrud.domain.NotePost
import com.fntechma.myfirebasecrud.listener.RecyclerViewClickListener
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity(), RecyclerViewClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

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
    }

    override fun onClick(position: Int, note: Note) {
        val item = note
        Log.d(javaClass.simpleName, "onClick: ${position} ${note}")
    }

    override fun onLongClick(position: Int, note: Note) {
        val item = note
        Log.d(javaClass.simpleName, "onLongClick: ${position} ${note}")
    }

    private fun initData() {
        viewModel.readNote()
    }

    private fun initView() {
        binding.run {
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

        }
    }

    private fun registerObserverSaveNote() {
        viewModel.addNote.observe(this) {
            it.message?.let { message ->
                message(message)
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

    @SuppressLint("NotifyDataSetChanged")
    private fun registerObserverReadNote() {
        viewModel.readNote.observe(this) {
            if (it.isNullOrEmpty()) {
                message("Falha na leitura de notas no banco")
            } else {
                val mainAdapter = MainAdapter(it, this)
                binding.rvNotes.adapter = mainAdapter
                mainAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun message(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

}