package com.luv.link.repositories

import com.luv.link.models.User
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class KtorRepository(
    private val client: HttpClient
) : NetworkRepository {
    override suspend fun getUser(id: Int): User =
        client
            .get("https://api.example.com/users/$id")
            .body()
}
