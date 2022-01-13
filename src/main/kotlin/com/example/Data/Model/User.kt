package com.example.Data.Model

import io.ktor.auth.*

data class User(
    val email:String,
    val hashPassword:String,
    val userName:String
): Principal
