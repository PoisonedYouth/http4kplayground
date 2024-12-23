package com.poisonedyouth.user

import java.util.UUID

interface UserInputPort {

    fun getUserBy(username: String): User?
    fun getUserBy(userId: UUID): User?
    fun createNewUser(user: User): User
}