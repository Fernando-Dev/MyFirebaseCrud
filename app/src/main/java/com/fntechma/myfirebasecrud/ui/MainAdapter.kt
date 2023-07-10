package com.fntechma.myfirebasecrud.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.fntechma.myfirebasecrud.R
import com.fntechma.myfirebasecrud.databinding.ItemNoteBinding
import com.fntechma.myfirebasecrud.domain.Note
import com.fntechma.myfirebasecrud.listener.RecyclerViewClickListener

class MainAdapter(private val list: ArrayList<Note>, private val listener: RecyclerViewClickListener) :
    RecyclerView.Adapter<MainAdapter.MainViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(
            ItemNoteBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val note = list[position]
        holder.bind(note)

        holder.itemView.findViewById<TextView>(R.id.item_text_note).setOnClickListener {
            listener.onClick(position, note)
            holder.goneRecycle()
        }

        holder.itemView.findViewById<TextView>(R.id.item_text_note).setOnLongClickListener {
            listener.onLongClick(position, note)
            holder.showRecycle()
            true
        }

        holder.itemView.findViewById<ImageView>(R.id.btn_delete_note).setOnClickListener {
            listener.onDeleteNote(position, note)
        }

    }

    override fun getItemCount(): Int = list.size

    inner class MainViewHolder(private val binding: ItemNoteBinding) :
        ViewHolder(binding.root) {

        fun bind(note: Note) {
            binding.itemTextNote.text = note.note
        }

        fun showRecycle() {
            binding.btnDeleteNote.visibility = VISIBLE
        }

        fun goneRecycle() {
            binding.btnDeleteNote.visibility = GONE
        }

    }

}