package com.poisonedyouth.chat

import java.time.Instant
import java.util.UUID

class ChatService(
    private val chatRepository: ChatRepository,
) : ChatInputPort {
    override fun createNewChat(owner: UUID, messages: List<String>, userIds: List<UUID>): Chat {
        val chat = Chat(
            id = UUID.randomUUID(),
            owner = owner,
            createdAt = Instant.now(),
        )
        messages.forEach {
            chat.addMessage(
                message = it,
                createdBy = owner,
                createdAt = Instant.now()
            )
        }
        userIds.forEach {
            chat.addUser(it)
        }

        return chatRepository.save(chat)
    }

    override fun getAllChats(): List<Chat> {
        return chatRepository.findAll()
    }

    override fun getChat(id: UUID): Chat? {
        return chatRepository.findById(id)
    }

    override fun addMessageToChat(chatId: UUID, message: Message) {
        val chat = chatRepository.findById(chatId)
        requireNotNull(chat) { "Chat with id '$chatId' not found." }
        chatRepository.save(
            chat.addMessage(
                message = message.message,
                createdBy = message.createdBy,
                createdAt = message.createdAt
            )
        )
    }

    override fun addUsersToChat(chatId: UUID, userIds: List<UUID>) {
        val chat = chatRepository.findById(chatId)
        requireNotNull(chat) { "Chat with id '$chatId' not found." }
        chatRepository.save(
            chat.addUser(userIds.first())
        )
    }

    override fun removeUserFromChat(chatId: UUID, userId: UUID) {
        val chat = chatRepository.findById(chatId)
        requireNotNull(chat) { "Chat with id '$chatId' not found." }
        chatRepository.save(
            chat.removeUser(userId)
        )
    }

    override fun deleteChat(chatId: UUID) {
        chatRepository.deleteById(chatId)
    }

}