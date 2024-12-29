package com.poisonedyouth.chat.infrastructure

import com.poisonedyouth.chat.domain.Chat
import com.poisonedyouth.chat.domain.ChatInputPort
import com.poisonedyouth.chat.domain.Message
import com.poisonedyouth.user.domain.UserNotFoundException
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
): HttpHandler =
    {
        val newUserDto = Jackson.asA<NewUserDto>(it.bodyString())
        val result = chatInputPort.addUsersToChat(UUID.fromString(newUserDto.chatId), newUserDto.userIds.map { UUID.fromString(it) })

        when (result.isSuccess) {
            true -> Response(status = Status.ACCEPTED).body("Users '${newUserDto.userIds}' added to chat '${newUserDto.chatId}'")
            false -> handleFailure(result)
        }
    }

fun getAllChatsHandler(chatInputPort: ChatInputPort): HttpHandler =
    {
        val result = chatInputPort.getAllChats()

        when (result.isSuccess) {
            true -> {
                val chats = result.getOrNull()!!
                Response(status = Status.OK)
                    .body(Jackson.asFormatString(chats.map { it.toDto() }))
                    .header("content-type", ContentType.APPLICATION_JSON.toHeaderValue())
            }

            false -> handleFailure(result)
        }
    }

fun addChatHandler(
    chatInputPort: ChatInputPort,
): HttpHandler =
    { request ->
        val chatDto = Jackson.asA<NewChatDto>(request.bodyString())

        val result = chatInputPort.createNewChat(
            owner = UUID.fromString(chatDto.owner),
            messages = chatDto.messages,
            userIds = chatDto.userIds.map { UUID.fromString(it) },
        )

        when (result.isSuccess) {
            true -> Response(status = Status.CREATED).body(result.getOrNull()!!.id.toString())
            false -> handleFailure(result)
        }
    }

fun addMessageToChatHandler(chatInputPort: ChatInputPort): HttpHandler =
    {
        val newChatMessageDto = Jackson.asA<NewChatMessageDto>(it.bodyString())

        val result = chatInputPort.addMessageToChat(
            chatId = UUID.fromString(newChatMessageDto.chatId),
            message =
                Message(
                    id = UUID.randomUUID(),
                    message = newChatMessageDto.message,
                    createdAt = Instant.now(),
                    createdBy = newChatMessageDto.owner.let { UUID.fromString(it) },
                ),
        )

        when (result.isSuccess) {
            true -> Response(status = Status.ACCEPTED)
                .body("Message '${newChatMessageDto.message}' added to chat '${newChatMessageDto.chatId}'")

            false -> handleFailure(result)
        }
    }

fun getChatHandler(chatInputPort: ChatInputPort): HttpHandler =
    {
        val result = chatInputPort.getChat(UUID.fromString(it.path("id")))
        when (result.isSuccess) {
            true -> {
                val chat = result.getOrNull()!!
                Response(status = Status.OK).body(Jackson.asFormatString(chat.toDto()))
            }

            false -> handleFailure(result)
        }
    }

private fun handleFailure(result: Result<Any>) = when (val exception = result.exceptionOrNull()!!) {
    is UserNotFoundException ->
        Response(status = Status.NOT_FOUND)
            .body(exception.message)

    else ->
        Response(status = Status.INTERNAL_SERVER_ERROR)
            .body(exception.message ?: "Unknown error occurred.")
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

fun Chat.toDto() = ChatDto(
    id = this.id.toString(),
    createdAt = this.createdAt.toString(),
    messages =
        this.getMessages().map { message ->
            MessageDto(
                id = message.id.toString(),
                message = message.message,
                createdAt = message.createdAt.toString(),
                createdBy = message.createdBy.toString(),
            )
        },
)