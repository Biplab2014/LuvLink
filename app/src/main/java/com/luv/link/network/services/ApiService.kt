package com.luv.link.network.services

import com.luv.link.models.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("api/v1/messages")
    fun getMessages(
        @Query("topic") topic: String
    ): Call<List<String>>

    @GET("users/{id}")
    suspend fun getUser(
        @Path("id") userId: Int
    ): User
}
