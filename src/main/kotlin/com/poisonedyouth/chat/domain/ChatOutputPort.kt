package com.poisonedyouth.chat.domain

import java.util.UUID

interface ChatOutputPort {
    fun save(chat: Chat): Result<Chat>

    fun findById(id: UUID): Result<Chat?>

    fun deleteById(id: UUID): Result<Unit>

    fun findAll(): Result<List<Chat>>
}
