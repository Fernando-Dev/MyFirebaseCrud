package com.fntechma.myfirebasecrud.listener

import com.fntechma.myfirebasecrud.domain.Note

interface RecyclerViewClickListener {

    fun onClick(position: Int, note: Note)
    fun onLongClick(position: Int, note: Note)

}