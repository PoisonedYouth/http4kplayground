package com.poisonedyouth.chat.domain

import arrow.core.Either
import com.poisonedyouth.common.GenericException
import java.util.UUID

interface ChatInputPort {
    fun createNewChat(
        owner: UUID,
        messages: List<String>,
        userIds: List<UUID>,
    ): Either<GenericException, Chat>

    fun addMessageToChat(
        chatId: UUID,
        message: Message,
    ): Either<GenericException, Unit>

    fun addUsersToChat(
        chatId: UUID,
        userIds: List<UUID>,
    ): Either<GenericException, Unit>

    fun removeUserFromChat(
        chatId: UUID,
        userId: UUID,
    ): Either<GenericException, Unit>

    fun deleteChat(chatId: UUID): Either<GenericException, Unit>

    fun getAllChats(): Either<GenericException, List<Chat>>

    fun getChat(id: UUID): Either<GenericException, Chat>
}
