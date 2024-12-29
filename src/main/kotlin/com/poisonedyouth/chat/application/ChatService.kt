package com.poisonedyouth.chat.application

import com.poisonedyouth.chat.domain.Chat
import com.poisonedyouth.chat.domain.ChatInputPort
import com.poisonedyouth.chat.domain.ChatOutputPort
import com.poisonedyouth.chat.domain.Message
import java.time.Instant
import java.util.UUID

class ChatService(
    private val chatOutputPort: ChatOutputPort,
) : ChatInputPort {
    override fun createNewChat(
        owner: UUID,
        messages: List<String>,
        userIds: List<UUID>,
    ): Chat {
        val chat =
            Chat(
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

        return chatOutputPort.save(chat)
    }

    override fun getAllChats(): List<Chat> {
        return chatOutputPort.findAll()
    }

    override fun getChat(id: UUID): Chat? {
        return chatOutputPort.findById(id)
    }

    override fun addMessageToChat(
        chatId: UUID,
        message: Message,
    ) {
        val chat = chatOutputPort.findById(chatId)
        requireNotNull(chat) { "Chat with id '$chatId' not found." }
        chatOutputPort.save(
            chat.addMessage(
                message = message.message,
                createdBy = message.createdBy,
                createdAt = message.createdAt,
            ),
        )
    }

    override fun addUsersToChat(
        chatId: UUID,
        userIds: List<UUID>,
    ) {
        val chat = chatOutputPort.findById(chatId)
        requireNotNull(chat) { "Chat with id '$chatId' not found." }
        chatOutputPort.save(
            chat.addUser(userIds.first()),
        )
    }

    override fun removeUserFromChat(
        chatId: UUID,
        userId: UUID,
    ) {
        val chat = chatOutputPort.findById(chatId)
        requireNotNull(chat) { "Chat with id '$chatId' not found." }
        chatOutputPort.save(
            chat.removeUser(userId),
        )
    }

    override fun deleteChat(chatId: UUID) {
        chatOutputPort.deleteById(chatId)
    }
}
