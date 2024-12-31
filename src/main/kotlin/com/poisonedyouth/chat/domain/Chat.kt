package com.poisonedyouth.chat.domain

import java.time.Instant
import java.util.UUID

class Chat(val id: UUID, val createdAt: Instant, val owner: UUID) {
    private val messages: MutableSet<Message> = mutableSetOf()
    private val users: MutableSet<UUID> = mutableSetOf()

    fun addMessage(message: Message): Chat {
        messages.add(message)
        return this
    }

    fun getMessages() = messages.toList()

    fun addUser(user: UUID): Chat {
        users.add(user)
        return this
    }

    fun removeUser(user: UUID): Chat {
        users.remove(user)
        return this
    }

    fun getUsers() = users.toList()

    override fun equals(other: Any?): Boolean {
        return other is Chat && other.id == id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

class Message(val id: UUID, val message: String, val createdAt: Instant, val createdBy: UUID) {
    override fun equals(other: Any?): Boolean {
        return other is Message && id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
