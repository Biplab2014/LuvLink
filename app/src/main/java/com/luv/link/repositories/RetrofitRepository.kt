package com.luv.link.repositories

import com.luv.link.models.User
import com.luv.link.network.services.ApiService
import com.luv.link.repositories.NetworkRepository

class RetrofitRepository(
    private val apiService: ApiService
) : NetworkRepository {
    override suspend fun getUser(id: Int): User = apiService.getUser(id)
}
