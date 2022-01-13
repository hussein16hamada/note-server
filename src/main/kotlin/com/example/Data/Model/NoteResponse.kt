package com.example.Data.Model



data class NoteResponse(
    val id:Int,
    val noteTitle:String,
    val description:String,
    val date:Long
)