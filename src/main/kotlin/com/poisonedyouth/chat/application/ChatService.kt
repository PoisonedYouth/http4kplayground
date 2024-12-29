package com.poisonedyouth.chat.application

import com.poisonedyouth.chat.domain.Chat
import com.poisonedyouth.chat.domain.ChatInputPort
import com.poisonedyouth.chat.domain.ChatNotFoundException
import com.poisonedyouth.chat.domain.ChatOutputPort
import com.poisonedyouth.chat.domain.Message
import com.poisonedyouth.user.domain.UserInputPort
import com.poisonedyouth.user.domain.UserNotFoundException
import java.time.Instant
import java.util.UUID

class ChatService(
    private val chatOutputPort: ChatOutputPort,
    private val userInputPort: UserInputPort,
) : ChatInputPort {
    override fun createNewChat(
        owner: UUID,
        messages: List<String>,
        userIds: List<UUID>,
    ): Result<Chat> = Result.runCatching {
        userInputPort.getUserBy(owner).getOrThrow()

        val notExistingUserIds = userIds.filter { userId -> userInputPort.getUserBy(userId).isFailure }
        if (notExistingUserIds.isNotEmpty()) {
            throw UserNotFoundException("User(s) '$notExistingUserIds' not found.")
        }

        val chat = Chat(
            id = UUID.randomUUID(),
            owner = owner,
            createdAt = Instant.now(),
        )
        messages.forEach {
            chat.addMessage(
                message = it,
                createdBy = owner,
                createdAt = Instant.now(),
            )
        }
        userIds.forEach {
            chat.addUser(it)
        }

        chatOutputPort.save(chat).getOrThrow()
    }

    override fun getAllChats(): Result<List<Chat>> {
        return chatOutputPort.findAll()
    }

    override fun getChat(id: UUID): Result<Chat> = Result.runCatching {
        chatOutputPort.findById(id).getOrThrow() ?: throw ChatNotFoundException("Chat with id '$id' not found.")
    }

    override fun addMessageToChat(
        chatId: UUID,
        message: Message,
    ): Result<Unit> = Result.runCatching {
        val chat = chatOutputPort.findById(chatId).getOrThrow()
        if (chat == null) {
            throw ChatNotFoundException("Chat with id '$chatId' not found.")
        } else {
            chatOutputPort.save(
                chat.addMessage(
                    message = message.message,
                    createdBy = message.createdBy,
                    createdAt = message.createdAt,
                ),
            )
        }
    }

    override fun addUsersToChat(
        chatId: UUID,
        userIds: List<UUID>,
    ): Result<Unit> = Result.runCatching {
        val chat = chatOutputPort.findById(chatId).getOrThrow() ?: throw ChatNotFoundException("Chat with id '$chatId' not found.")

        val notExistingUserIds = userIds.filter { userId -> userInputPort.getUserBy(userId).isFailure }
        if (notExistingUserIds.isNotEmpty()) {
            throw UserNotFoundException("User(s) '$notExistingUserIds' not found.")
        }

        chatOutputPort.save(
            chat.addUser(userIds.first()),
        )
    }

    override fun removeUserFromChat(
        chatId: UUID,
        userId: UUID,
    ): Result<Unit> = Result.runCatching {
        val chat = chatOutputPort.findById(chatId).getOrThrow() ?: throw ChatNotFoundException("Chat with id '$chatId' not found.")
        chatOutputPort.save(
            chat.removeUser(userId),
        )
    }

    override fun deleteChat(chatId: UUID): Result<Unit> {
        return chatOutputPort.deleteById(chatId)
    }
}
