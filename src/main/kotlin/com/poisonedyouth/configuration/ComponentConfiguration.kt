package com.poisonedyouth.configuration

import com.poisonedyouth.chat.domain.ChatInputPort
import com.poisonedyouth.chat.domain.ChatOutputPort
import com.poisonedyouth.chat.application.ChatService
import com.poisonedyouth.chat.infrastructure.ExposedChatOutputport
import com.poisonedyouth.user.infrastructure.ExposedUserRepository
import com.poisonedyouth.user.domain.UserInputPort
import com.poisonedyouth.user.domain.UserOutputPort
import com.poisonedyouth.user.application.UserService
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

data object ComponentConfiguration {
    fun initKoin() {
        startKoin {
            modules(module)
        }
    }

    private val module = module {
        singleOf(::ExposedUserRepository) { bind<UserOutputPort>() }
        singleOf(::UserService) { bind<UserInputPort>() }
        singleOf(::ExposedChatOutputport) { bind<ChatOutputPort>() }
        singleOf(::ChatService) { bind<ChatInputPort>() }
    }
}