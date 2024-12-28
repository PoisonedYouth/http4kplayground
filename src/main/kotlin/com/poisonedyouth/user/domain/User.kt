package com.poisonedyouth.user.domain

import java.util.UUID

class User(val id: UUID, val username: String) {
    private val chats = mutableSetOf<UUID>()

    fun addChat(chatId: UUID): User {
        chats.add(chatId)
        return this
    }

    override fun equals(other: Any?): Boolean {
        return other is User && other.id == id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}