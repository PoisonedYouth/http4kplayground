package com.poisonedyouth.user.domain

import java.util.UUID

interface UserOutputPort{
    fun save(user: User): User
    fun findById(id: UUID): User?
    fun findByUsername(username: String): User?
    fun deleteById(id: UUID)
    fun findAll(): List<User>
}