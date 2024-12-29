package com.poisonedyouth.chat.domain

import java.util.UUID

interface ChatInputPort {
    fun createNewChat(
        owner: UUID,
        messages: List<String>,
        userIds: List<UUID>,
    ): Chat

    fun addMessageToChat(
        chatId: UUID,
        message: Message,
    )

    fun addUsersToChat(
        chatId: UUID,
        userIds: List<UUID>,
    )

    fun removeUserFromChat(
        chatId: UUID,
        userId: UUID,
    )

    fun deleteChat(chatId: UUID)

    fun getAllChats(): List<Chat>

    fun getChat(id: UUID): Chat?
}
