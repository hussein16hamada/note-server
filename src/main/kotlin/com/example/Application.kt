package com.example

import com.example.Auth.JwtService
import com.example.Auth.hash
import com.example.Rebository.DatabaseFactory
import com.example.Rebository.Repo
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.example.plugins.*
import com.example.routes.NoteRoutes
import com.example.routes.UserRoutes
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.locations.*

import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        DatabaseFactory.init()
        val db = Repo()
        val jwtService = JwtService()
        val hashFunction = { s:String -> hash(s) }
        configureSecurity()
        configureRouting()
        install(Authentication) {
            jwt("jwt") {

                verifier(jwtService.varifier)
                realm = "Note Server"
                validate {
                    val payload = it.payload
                    val email = payload.getClaim("email").asString()
                    val user = db.findUserByEmail(email)
                    user
                }

            }

        }
        install(Locations)

        install(ContentNegotiation) {
            gson {
            }
        }

        routing {


            get("/") {
                call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
            }

            UserRoutes(db, jwtService, hashFunction)
            NoteRoutes(db, hashFunction)
            route("/notes") {

                route("/create") {
                    // localhost:8081/notes/create
                    post {
                        val body = call.receive<String>()
                        call.respond(body)
                    }
                }

                delete {
                    val body = call.receive<String>()
                    call.respond(body)
                }
            }


        }
    }.start(wait = true)
}
