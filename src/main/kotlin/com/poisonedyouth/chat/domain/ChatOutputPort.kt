package com.poisonedyouth.chat.domain

import java.util.UUID

interface ChatOutputPort {
    fun save(chat: Chat): Chat
    fun findById(id: UUID): Chat?
    fun deleteById(id: UUID)
    fun findAll(): List<Chat>
}