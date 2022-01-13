package com.example.Data.Model



data class NoteRequest(
    val email:String,
    val noteTitle:String,
    val description:String,
    val date:Long
)