package com.poisonedyouth.chat.domain

import java.util.UUID

interface ChatInputPort {
    fun createNewChat(
        owner: UUID,
        messages: List<String>,
        userIds: List<UUID>,
    ): Result<Chat>

    fun addMessageToChat(
        chatId: UUID,
        message: Message,
    ): Result<Unit>

    fun addUsersToChat(
        chatId: UUID,
        userIds: List<UUID>,
    ): Result<Unit>

    fun removeUserFromChat(
        chatId: UUID,
        userId: UUID,
    ): Result<Unit>

    fun deleteChat(chatId: UUID): Result<Unit>

    fun getAllChats(): Result<List<Chat>>

    fun getChat(id: UUID): Result<Chat>
}
