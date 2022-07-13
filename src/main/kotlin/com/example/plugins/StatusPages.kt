package com.example.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import javax.naming.AuthenticationException

fun Application.configureStatusPages() {
    install(StatusPages) {
//        status(HttpStatusCode.NotFound) { call, status ->
//            call.respondText(text = "404: Page Not Found", status = status)
//        }
        exception<AuthenticationException> { call, _ ->
            call.respondText(text = "Exception caught", status = HttpStatusCode.OK)
        }
    }
}