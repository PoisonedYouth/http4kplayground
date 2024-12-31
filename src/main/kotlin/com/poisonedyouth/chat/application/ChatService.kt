package com.poisonedyouth.chat.application

import arrow.core.Either
import arrow.core.raise.either
import com.poisonedyouth.chat.domain.Chat
import com.poisonedyouth.chat.domain.ChatInputPort
import com.poisonedyouth.chat.domain.ChatNotFoundException
import com.poisonedyouth.chat.domain.ChatOutputPort
import com.poisonedyouth.chat.domain.Message
import com.poisonedyouth.common.GenericException
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
    ): Either<GenericException, Chat> =
        either {
            userInputPort.getUserBy(owner).bind()

            val notExistingUserIds = userIds.filter { userId -> userInputPort.getUserBy(userId).bind() != null }
            if (notExistingUserIds.isNotEmpty()) {
                raise(UserNotFoundException("User(s) '$notExistingUserIds' not found."))
            }

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

            chatOutputPort.save(chat).bind()
            chat
        }

    override fun getAllChats(): Either<GenericException, List<Chat>> {
        return chatOutputPort.findAll()
    }

    override fun getChat(id: UUID): Either<GenericException, Chat> =
        either {
            chatOutputPort.findById(id).bind() ?: raise(ChatNotFoundException("Chat with id '$id' not found."))
        }

    override fun addMessageToChat(
        chatId: UUID,
        message: Message,
    ): Either<GenericException, Unit> =
        either {
            val chat = chatOutputPort.findById(chatId).bind()
            if (chat == null) {
                raise(ChatNotFoundException("Chat with id '$chatId' not found."))
            } else {
                chatOutputPort.save(
                    chat.addMessage(
                        message = message.message,
                        createdBy = message.createdBy,
                        createdAt = message.createdAt,
                    ),
                ).bind()
            }
        }

    override fun addUsersToChat(
        chatId: UUID,
        userIds: List<UUID>,
    ): Either<GenericException, Unit> =
        either {
            val chat = chatOutputPort.findById(chatId).bind()
                ?: raise(ChatNotFoundException("Chat with id '$chatId' not found."))

            val notExistingUserIds = userIds.filter { userId -> userInputPort.getUserBy(userId).bind() != null }
            if (notExistingUserIds.isNotEmpty()) {
                raise(UserNotFoundException("User(s) '$notExistingUserIds' not found."))
            }

            chatOutputPort.save(
                chat.addUser(userIds.first()),
            ).bind()
        }

    override fun removeUserFromChat(
        chatId: UUID,
        userId: UUID,
    ): Either<GenericException, Unit> =
        either {
            val chat = chatOutputPort.findById(chatId).bind()
                ?: raise(ChatNotFoundException("Chat with id '$chatId' not found."))
            chatOutputPort.save(
                chat.removeUser(userId),
            ).bind()
        }

    override fun deleteChat(chatId: UUID): Either<GenericException, Unit> {
        return chatOutputPort.deleteById(chatId)
    }
}
