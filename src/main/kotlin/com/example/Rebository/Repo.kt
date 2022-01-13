package com.example.Rebository

import com.example.Data.Model.Note
import com.example.Data.Model.NoteRequest
import com.example.Data.Model.NoteResponse
import com.example.Data.Model.User
import com.example.Data.Table.NoteTable
import com.example.Data.Table.UserTable
import com.example.Rebository.DatabaseFactory.dbQuery
import io.ktor.application.*
import org.jetbrains.exposed.sql.*

class Repo {

    suspend fun addUser(user:User){
        dbQuery{
            UserTable.insert { ut->
                ut[UserTable.email] = user.email
                ut[UserTable.hashPassword] = user.hashPassword
                ut[UserTable.name] = user.userName
            }
        }
    }

    suspend fun findUserByEmail(email:String) = dbQuery {
        UserTable.select { UserTable.email.eq(email) }
            .map { rowToUser(it) }
            .singleOrNull()
    }

    private fun rowToUser(row:ResultRow?):User?{
        if(row == null){
            return null
        }

        return User(
            email =  row[UserTable.email],
            hashPassword = row[UserTable.hashPassword],
            userName = row[UserTable.name]
        )
    }


    //    ============== NOTES ==============


    suspend fun addNote(note:NoteRequest,email: String){
        dbQuery {

            NoteTable.insert { nt->
//                nt[NoteTable.id] = note.id
                nt[NoteTable.userEmail] = email
                nt[NoteTable.noteTitle] = note.noteTitle
                nt[NoteTable.description] = note.description
                nt[NoteTable.date] = note.date
            }

        }

    }


    suspend fun getAllNotes(email:String):List<NoteResponse> = dbQuery {

        NoteTable.select {
            NoteTable.userEmail.eq(email)
        }.mapNotNull { rowToNote(it) }

    }


    suspend fun updateNote(note:NoteResponse,email: String,id :Int){

        dbQuery {

            NoteTable.update(
                where = {
                    NoteTable.userEmail.eq(email) and NoteTable.id.eq(id)
                }
            ){ nt->
                nt[NoteTable.id] = note.id
                nt[NoteTable.noteTitle] = note.noteTitle
                nt[NoteTable.description] = note.description
                nt[NoteTable.date] = note.date
            }

        }

    }
//
    suspend fun deleteNote(id:Int,email: String){
        dbQuery {
            NoteTable.deleteWhere {NoteTable.userEmail.eq(email)and  NoteTable.id.eq(id) }
        }
    }


    private fun rowToNote(row:ResultRow?): NoteResponse? {

        if(row == null){
            return null
        }

        return NoteResponse(
            id = row[NoteTable.id],
            noteTitle = row[NoteTable.noteTitle],
            description =  row[NoteTable.description],
            date = row[NoteTable.date]
        )

    }

}