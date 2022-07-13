package com.example

import com.example.di.koinModule
import com.example.models.ApiResponse
import com.example.repository.HeroRepository
import com.example.repository.NEXT_PAGE_KEY
import com.example.repository.PREVIOUS_PAGE_KEY
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.koin.core.context.loadKoinModules
import org.koin.java.KoinJavaComponent.inject
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {

//    private val heroRepository: HeroRepository by inject(HeroRepository::class.java)
    private val heroRepository by inject<HeroRepository>(HeroRepository::class.java)

    @Test
    fun `access root endpoint, assert correct text and status`() = testApplication {
        val response = client.get("/")
        assertEquals(expected = HttpStatusCode.OK, actual = response.status)
        assertEquals(expected = "Welcome to Boruto Api", actual = response.bodyAsText())
    }

    @Test
    fun `access all heroes endpoint, query all pages, assert correct information`() = testApplication {
        environment {
            developmentMode = true
        }
        loadKoinModules(koinModule)
        val pages = 1..5
        val heroes = listOf(
            heroRepository.page1,
            heroRepository.page2,
            heroRepository.page3,
            heroRepository.page4,
            heroRepository.page5
        )
        pages.forEach { page ->
            val response = client.get("/boruto/heroes?pages=$page")
            assertEquals(expected = HttpStatusCode.OK, actual = response.status)
            val actual = Json.decodeFromString<ApiResponse>(response.body())
            val expected = ApiResponse(
                success = true,
                message = "ok",
                previousPage = calculatePage(page)["prevPage"],
                nextPage = calculatePage(page)["nextPage"],
                heroes = heroes[page - 1],
                lastUpdated = actual.lastUpdated
            )
            assertEquals(expected = expected, actual = actual)
        }
    }

    private fun calculatePage(page: Int): Map<String, Int?> {
        var prevPage: Int? = page
        var nextPage: Int? = page
        if (page in 1..4) {
            nextPage = nextPage?.plus(  1)
        }
        if (page in 2..5) {
            prevPage = prevPage?.minus(1)
        }
        if (page == 1) {
            prevPage = null
        }
        if (page == 5) {
            nextPage = null
        }
        return mapOf(PREVIOUS_PAGE_KEY to prevPage, NEXT_PAGE_KEY to nextPage)
    }

    @Test
    fun `access all heroes endpoint, assert correct information`() = testApplication {
        environment {
            developmentMode = false
        }
        val response = client.get("/boruto/heroes?page=2")
        assertEquals(expected = HttpStatusCode.OK, actual = response.status)
        val expected = ApiResponse(
            success = true,
            message = "ok",
            previousPage = 1,
            nextPage = 3,
            heroes = heroRepository.page2
        )
        val actual = Json.decodeFromString<ApiResponse>(response.body())
        assertEquals(expected = expected, actual = actual)
    }

    @Test
    fun `access all heroes endpoint, query non existing page, assert error`() = testApplication {
        environment {
            developmentMode = false
        }
        val response = client.get("/boruto/heroes?page=6")
        assertEquals(expected = HttpStatusCode.NotFound, actual = response.status)
        val expected = ApiResponse(
            success = false,
            message = "Heroes not found. Only page numbers between 1 and 5 allowed",
        )
        val actual = Json.decodeFromString<ApiResponse>(response.body())
        println("actual: $actual")
        println("expected: $expected")
        assertEquals(expected = expected, actual = actual)
    }

    @Test
    fun `access all heroes endpoint, query invalid page, assert error`() = testApplication {
        environment {
            developmentMode = false
        }
        val response = client.get("/boruto/heroes?page=p")
        assertEquals(expected = HttpStatusCode.BadRequest, actual = response.status)
        val expected = ApiResponse(
            success = false,
            message = "Only numbers allowed",
        )
        val actual = Json.decodeFromString<ApiResponse>(response.body())
        println("actual: $actual")
        println("expected: $expected")
        assertEquals(expected = expected, actual = actual)
    }

    @Test
    fun `access search heroes endpoint, query hero name, assert single hero`() = testApplication {
        environment {
            developmentMode = false
        }
        val response = client.get("/boruto/heroes/search?name=sasuke")
        assertEquals(expected = HttpStatusCode.OK, actual = response.status)
        val actual = Json.decodeFromString<ApiResponse>(response.body()).heroes.size
        println("actual: $actual")
        assertEquals(expected = 1, actual = actual)
    }

    @Test
    fun `access search heroes endpoint, query hero name, assert multiple heroes`() = testApplication {
        environment {
            developmentMode = false
        }
        val response = client.get("/boruto/heroes/search?name=sa")
        assertEquals(expected = HttpStatusCode.OK, actual = response.status)
        val actual = Json.decodeFromString<ApiResponse>(response.body()).heroes.size
        println("actual: $actual")
        assertEquals(expected = 3, actual = actual)
    }

    @Test
    fun `access search heroes endpoint, query non existing hero, assert empty list as result`() = testApplication {
        environment {
            developmentMode = false
        }
        val response = client.get("/boruto/heroes/search?name=bob")
        assertEquals(expected = HttpStatusCode.OK, actual = response.status)
        val actual = Json.decodeFromString<ApiResponse>(response.body()).heroes
        println("actual: $actual")
        assertEquals(expected = emptyList(), actual = actual)
    }

    @Test
    fun `access search heroes endpoint, query an empty text, assert empty list as result`() = testApplication {
        environment {
            developmentMode = false
        }
        val response = client.get("/boruto/heroes/search?name=")
        assertEquals(expected = HttpStatusCode.OK, actual = response.status)
        val actual = Json.decodeFromString<ApiResponse>(response.body()).heroes
        println("actual: $actual")
        assertEquals(expected = emptyList(), actual = actual)
    }

    @Test
    fun `access non existing endpoint, assert not found`() = testApplication {
        environment {
            developmentMode = false
        }
        val response = client.get("/unknown")
        assertEquals(expected = HttpStatusCode.NotFound, actual = response.status)
        assertEquals(expected = "404: Page Not Found", actual = response.body())
    }

}