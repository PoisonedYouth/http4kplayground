package com.poisonedyouth.configuration

import com.poisonedyouth.chat.application.ChatService
import com.poisonedyouth.chat.domain.ChatInputPort
import com.poisonedyouth.chat.domain.ChatOutputPort
import com.poisonedyouth.chat.infrastructure.ExposedChatOutputPort
import com.poisonedyouth.event.application.EventService
import com.poisonedyouth.event.domain.EventInputPort
import com.poisonedyouth.event.domain.EventOutputPort
import com.poisonedyouth.event.infrastructure.ExposedEventRepository
import com.poisonedyouth.user.application.UserService
import com.poisonedyouth.user.domain.UserInputPort
import com.poisonedyouth.user.domain.UserOutputPort
import com.poisonedyouth.user.infrastructure.ExposedUserRepository
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.slf4j.LoggerFactory

val defaultModule: org.koin.core.module.Module =
    module {
        singleOf(::ApplicationPropertiesDatabaseProperties) { bind<DatabaseProperties>() }
        singleOf(::ExposedUserRepository) { bind<UserOutputPort>() }
        singleOf(::UserService) { bind<UserInputPort>() }
        singleOf(::ExposedChatOutputPort) { bind<ChatOutputPort>() }
        singleOf(::ChatService) { bind<ChatInputPort>() }
        singleOf(::ExposedEventRepository) { bind<EventOutputPort>() }
        singleOf(::EventService) { bind<EventInputPort>() }
    }

data object ComponentConfiguration {
    private val logger = LoggerFactory.getLogger(ComponentConfiguration::class.java)

    fun initKoin(module: org.koin.core.module.Module): KoinApplication {
        logger.info("Initialize Koin...")
        return startKoin {
            modules(module)
        }
    }
}
