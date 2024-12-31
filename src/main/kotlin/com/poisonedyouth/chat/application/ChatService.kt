package com.poisonedyouth.chat.application

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensureNotNull
import com.poisonedyouth.chat.domain.Chat
import com.poisonedyouth.chat.domain.ChatInputPort
import com.poisonedyouth.chat.domain.ChatNotFoundException
import com.poisonedyouth.chat.domain.ChatOutputPort
import com.poisonedyouth.chat.domain.Message
import com.poisonedyouth.common.GenericException
import com.poisonedyouth.configuration.objectMapper
import com.poisonedyouth.event.domain.Event
import com.poisonedyouth.event.domain.EventInputPort
import com.poisonedyouth.event.domain.EventType
import com.poisonedyouth.user.domain.UserInputPort
import com.poisonedyouth.user.domain.UserNotFoundException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Instant
import java.util.UUID

class ChatService(
    private val chatOutputPort: ChatOutputPort,
    private val userInputPort: UserInputPort,
    private val eventInputPort: EventInputPort,
) : ChatInputPort {
    private val logger: Logger = LoggerFactory.getLogger(ChatService::class.java)

    override fun createNewChat(
        owner: UUID,
        messages: List<String>,
        userIds: List<UUID>,
    ): Either<GenericException, Chat> =
        either {
            ensureNotNull(userInputPort.getUserBy(owner).bind()) {
                raise(UserNotFoundException("User with id '$owner' not found."))
            }

            val notExistingUserIds = userIds.filter { userId -> userInputPort.getUserBy(userId).bind() == null }
            if (notExistingUserIds.isNotEmpty()) {
                raise(UserNotFoundException("User(s) '$notExistingUserIds' not found."))
            }

            val id = UUID.randomUUID()
            logger.info("Creating new chat with id '{}'", id)
            val chat =
                Chat(
                    id = id,
                    owner = owner,
                    createdAt = Instant.now(),
                )
            messages.forEach {
                chat.addMessage(
                    Message(
                        id = UUID.randomUUID(),
                        message = it,
                        createdBy = owner,
                        createdAt = Instant.now(),
                    ),
                )
            }
            userIds.forEach {
                chat.addUser(it)
            }

            chatOutputPort.save(chat).bind().also {
                eventInputPort.publish(
                    Event(
                        id = UUID.randomUUID(),
                        createdAt = it.createdAt,
                        payload = objectMapper.writeValueAsString(it),
                        type = EventType.CREATE_CHAT,
                    ),
                )
            }
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
                        Message(
                            id = UUID.randomUUID(),
                            message = message.message,
                            createdBy = message.createdBy,
                            createdAt = message.createdAt,
                        ),
                    ),
                ).bind().also {
                    eventInputPort.publish(
                        Event(
                            id = UUID.randomUUID(),
                            createdAt = Instant.now(),
                            payload = objectMapper.writeValueAsString(it),
                            type = EventType.ADD_MESSAGE_TO_CHAT,
                        ),
                    )
                }
            }
        }

    override fun addUsersToChat(
        chatId: UUID,
        userIds: List<UUID>,
    ): Either<GenericException, Unit> =
        either {
            val chat =
                chatOutputPort.findById(chatId).bind()
                    ?: raise(ChatNotFoundException("Chat with id '$chatId' not found."))

            val notExistingUserIds = userIds.filter { userId -> userInputPort.getUserBy(userId).bind() == null }
            if (notExistingUserIds.isNotEmpty()) {
                raise(UserNotFoundException("User(s) '$notExistingUserIds' not found."))
            }

            chatOutputPort.save(
                userIds.fold(chat) { acc, userId -> acc.addUser(userId) },
            ).bind().also {
                eventInputPort.publish(
                    Event(
                        id = UUID.randomUUID(),
                        createdAt = Instant.now(),
                        payload = objectMapper.writeValueAsString(it),
                        type = EventType.ADD_USER_TO_CHAT,
                    ),
                )
            }
        }

    override fun removeUserFromChat(
        chatId: UUID,
        userId: UUID,
    ): Either<GenericException, Unit> =
        either {
            val chat =
                chatOutputPort.findById(chatId).bind()
                    ?: raise(ChatNotFoundException("Chat with id '$chatId' not found."))
            chatOutputPort.save(
                chat.removeUser(userId),
            ).bind().also {
                eventInputPort.publish(
                    Event(
                        id = UUID.randomUUID(),
                        createdAt = Instant.now(),
                        payload = objectMapper.writeValueAsString(it),
                        type = EventType.REMOVE_USER_FROM_CHAT,
                    ),
                )
            }
        }

    override fun deleteChat(chatId: UUID): Either<GenericException, Unit> {
        return chatOutputPort.deleteById(chatId)
    }
}
