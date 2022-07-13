package com.example.plugins

import com.example.routes.getAllHeroesAlternative
import com.example.routes.root
import com.example.routes.searchHeroes
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import javax.naming.AuthenticationException

fun Application.configureRouting() {

    routing {
        root()
//        getAllHeroes()
        getAllHeroesAlternative()
        searchHeroes()
        get("/test") {
            throw AuthenticationException()
        }
        static("/images") {
            resources("images")
        }
    }
}
