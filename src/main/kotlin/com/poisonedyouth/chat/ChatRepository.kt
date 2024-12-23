package com.poisonedyouth.chat

import java.util.UUID

interface ChatRepository {
    fun save(chat: Chat): Chat
    fun findById(id: UUID): Chat?
    fun deleteById(id: UUID)
    fun findAll(): List<Chat>
}