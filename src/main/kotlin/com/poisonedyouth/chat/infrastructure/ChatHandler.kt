package com.poisonedyouth.chat.infrastructure

import com.poisonedyouth.chat.domain.ChatInputPort
import com.poisonedyouth.chat.domain.Message
import com.poisonedyouth.user.domain.UserInputPort
import org.http4k.core.ContentType
import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.format.Jackson
import org.http4k.routing.path
import java.time.Instant
import java.util.UUID

fun addUserToChatHandler(
    chatInputPort: ChatInputPort,
    userInputPort: UserInputPort,
): HttpHandler =
    {
        val newUserDto = Jackson.asA<NewUserDto>(it.bodyString())

        val chat = chatInputPort.getChat(UUID.fromString(newUserDto.chatId))
        if (chat == null) {
            Response(status = Status.NOT_FOUND).body("Chat '${newUserDto.chatId}' not found")
        }

        newUserDto.userIds.forEach { userId ->
            if (userInputPort.getUserBy(UUID.fromString(userId)) == null) {
                Response(status = Status.NOT_FOUND).body("User '$userId not found")
                return@forEach
            }
        }
        chatInputPort.addUsersToChat(UUID.fromString(newUserDto.chatId), newUserDto.userIds.map { UUID.fromString(it) })
        Response(status = Status.ACCEPTED).body("Users '${newUserDto.userIds}' added to chat '${newUserDto.chatId}'")
    }

fun getAllChatsHandler(chatInputPort: ChatInputPort): HttpHandler =
    {
        Response(status = Status.OK)
            .body(
                Jackson.asFormatString(
                    chatInputPort.getAllChats().map {
                        ChatDto(
                            id = it.toString(),
                            createdAt = it.createdAt.toString(),
                            messages =
                                it.getMessages().map { message ->
                                    MessageDto(
                                        id = message.id.toString(),
                                        message = message.message,
                                        createdAt = message.createdAt.toString(),
                                        createdBy = message.createdBy.toString(),
                                    )
                                },
                        )
                    },
                ),
            )
            .header("content-type", ContentType.APPLICATION_JSON.toHeaderValue())
    }

fun addChatHandler(
    chatInputPort: ChatInputPort,
    userInputPort: UserInputPort,
): HttpHandler =
    { request ->
        val chatDto = Jackson.asA<NewChatDto>(request.bodyString())
        val owner = userInputPort.getUserBy(UUID.fromString(chatDto.owner))
        if (owner == null) {
            Response(status = Status.NOT_FOUND).body("User '${chatDto.owner}' not found")
        } else {
            val chat =
                chatInputPort.createNewChat(
                    owner = owner.id,
                    messages = chatDto.messages,
                    userIds = chatDto.userIds.map { UUID.fromString(it) },
                )
            Response(status = Status.CREATED).body(chat.id.toString())
        }
    }

fun addMessageToChatHandler(chatInputPort: ChatInputPort): HttpHandler =
    {
        val newChatMessageDto = Jackson.asA<NewChatMessageDto>(it.bodyString())
        val chat = chatInputPort.getChat(UUID.fromString(newChatMessageDto.chatId))
        if (chat == null) {
            Response(status = Status.NOT_FOUND).body("Chat '${newChatMessageDto.chatId}' not found")
        } else {
            chatInputPort.addMessageToChat(
                chatId = UUID.fromString(newChatMessageDto.chatId),
                message =
                    Message(
                        id = UUID.randomUUID(),
                        message = newChatMessageDto.message,
                        createdAt = Instant.now(),
                        createdBy = newChatMessageDto.owner.let { UUID.fromString(it) },
                    ),
            )
            Response(status = Status.ACCEPTED)
                .body("Message '${newChatMessageDto.message}' added to chat '${newChatMessageDto.chatId}'")
        }
    }

fun getChatHandler(chatInputPort: ChatInputPort): HttpHandler =
    {
        val chat = chatInputPort.getChat(UUID.fromString(it.path("id")))
        if (chat == null) {
            Response(status = Status.NOT_FOUND).body("Chat '${it.path("id")}' not found")
        } else {
            Response(status = Status.OK)
                .body(
                    Jackson.asFormatString(
                        ChatDto(
                            id = chat.id.toString(),
                            createdAt = chat.createdAt.toString(),
                            messages =
                                chat.getMessages().map {
                                    MessageDto(
                                        id = it.id.toString(),
                                        message = it.message,
                                        createdAt = it.createdAt.toString(),
                                        createdBy = it.createdBy.toString(),
                                    )
                                },
                        ),
                    ),
                )
        }
    }

data class ChatDto(
    val id: String,
    val createdAt: String,
    val messages: List<MessageDto>,
)

data class MessageDto(
    val id: String,
    val message: String,
    val createdAt: String,
    val createdBy: String,
)

data class NewChatDto(
    val owner: String,
    val messages: List<String>,
    val userIds: List<String>,
)

data class NewChatMessageDto(
    val chatId: String,
    val message: String,
    val owner: String,
)

data class NewUserDto(
    val chatId: String,
    val userIds: List<String>,
)
