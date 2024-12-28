package com.poisonedyouth.configuration

import com.poisonedyouth.chat.domain.ChatInputPort
import com.poisonedyouth.chat.domain.ChatOutputPort
import com.poisonedyouth.chat.application.ChatService
import com.poisonedyouth.chat.infrastructure.ExposedChatOutputport
import com.poisonedyouth.user.infrastructure.ExposedUserRepository
import com.poisonedyouth.user.domain.UserInputPort
import com.poisonedyouth.user.domain.UserOutputPort
import com.poisonedyouth.user.application.UserService

data object ComponentConfiguration {
    private val userOutputPort: UserOutputPort = ExposedUserRepository()
    val userInputPort: UserInputPort = UserService(userOutputPort)
    private val chatOutputport: ChatOutputPort = ExposedChatOutputport()
    val chatInputPort: ChatInputPort = ChatService(
        chatOutputPort = chatOutputport,
    )
}