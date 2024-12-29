package com.poisonedyouth.user.domain

import java.util.UUID

interface UserOutputPort {
    fun save(user: User): Result<User>

    fun findById(id: UUID): Result<User?>

    fun findByUsername(username: String): Result<User?>

    fun deleteById(id: UUID): Result<Unit>

    fun findAll(): Result<List<User>>
}
