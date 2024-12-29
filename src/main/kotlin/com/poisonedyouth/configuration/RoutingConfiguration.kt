package com.poisonedyouth.configuration

import com.poisonedyouth.chat.domain.ChatInputPort
import com.poisonedyouth.chat.infrastructure.addChatHandler
import com.poisonedyouth.chat.infrastructure.addMessageToChatHandler
import com.poisonedyouth.chat.infrastructure.addUserToChatHandler
import com.poisonedyouth.chat.infrastructure.getAllChatsHandler
import com.poisonedyouth.chat.infrastructure.getChatHandler
import com.poisonedyouth.user.domain.UserInputPort
import com.poisonedyouth.user.infrastructure.addUserHandler
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.koin.java.KoinJavaComponent.inject

private val chatInputPort by inject<ChatInputPort>(ChatInputPort::class.java)
private val userInputPort by inject<UserInputPort>(UserInputPort::class.java)

val app: HttpHandler =
    routes(
        "chat" bind GET to
            getAllChatsHandler(
                chatInputPort = chatInputPort,
            ),
        "chat" bind POST to
            addChatHandler(
                chatInputPort = chatInputPort,
                userInputPort = userInputPort,
            ),
        "chat/message" bind POST to
            addMessageToChatHandler(
                chatInputPort = chatInputPort,
            ),
        "chat/{id}" bind GET to
            getChatHandler(
                chatInputPort = chatInputPort,
            ),
        "chat/user" bind POST to
            addUserToChatHandler(
                chatInputPort = chatInputPort,
                userInputPort = userInputPort,
            ),
        "user" bind POST to
            addUserHandler(
                userInputPort = userInputPort,
            ),
    )
