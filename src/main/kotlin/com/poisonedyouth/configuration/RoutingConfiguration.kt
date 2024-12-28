package com.poisonedyouth.configuration

import com.poisonedyouth.chat.infrastructure.addChatHandler
import com.poisonedyouth.chat.infrastructure.addMessageToChatHandler
import com.poisonedyouth.chat.infrastructure.addUserToChatHandler
import com.poisonedyouth.chat.infrastructure.getAllChatsHandler
import com.poisonedyouth.chat.infrastructure.getChatHandler
import com.poisonedyouth.user.infrastructure.addUserHandler
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.routing.bind
import org.http4k.routing.routes

val app: HttpHandler = routes(
    "chat" bind GET to getAllChatsHandler(
        chatInputPort = ComponentConfiguration.chatInputPort
    ),
    "chat" bind POST to addChatHandler(
        chatInputPort = ComponentConfiguration.chatInputPort,
        userInputPort = ComponentConfiguration.userInputPort
    ),
    "chat/message" bind POST to addMessageToChatHandler(
        chatInputPort = ComponentConfiguration.chatInputPort
    ),
    "chat/{id}" bind GET to getChatHandler(
        chatInputPort = ComponentConfiguration.chatInputPort
    ),
    "chat/user" bind POST to addUserToChatHandler(
        chatInputPort = ComponentConfiguration.chatInputPort,
        userInputPort = ComponentConfiguration.userInputPort
    ),
    "user" bind POST to addUserHandler(
        userInputPort = ComponentConfiguration.userInputPort,
    )
)