package com.luv.link.repositories

import com.luv.link.models.User

interface NetworkRepository {
    suspend fun getUser(id: Int): User
}
