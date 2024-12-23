package com.poisonedyouth.configuration

import com.poisonedyouth.chat.ChatInputPort
import com.poisonedyouth.chat.ChatRepository
import com.poisonedyouth.chat.ChatService
import com.poisonedyouth.chat.ExposedChatRepository
import com.poisonedyouth.user.ExposedUserRepository
import com.poisonedyouth.user.UserInputPort
import com.poisonedyouth.user.UserRepository
import com.poisonedyouth.user.UserService

data object ComponentConfiguration {
    private val userRepository: UserRepository = ExposedUserRepository()
    val userInputPort: UserInputPort = UserService(userRepository)
    private val chatRepository: ChatRepository = ExposedChatRepository()
    val chatInputPort: ChatInputPort = ChatService(
        chatRepository = chatRepository,
    )
}