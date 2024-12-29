package com.poisonedyouth.user.domain

import java.util.UUID

interface UserInputPort {
    fun getUserBy(username: String): Result<User>

    fun getUserBy(userId: UUID): Result<User>

    fun createNewUser(user: User): Result<User>
}
