package com.poisonedyouth

import com.poisonedyouth.chat.application.ChatService
import com.poisonedyouth.chat.domain.ChatInputPort
import com.poisonedyouth.chat.domain.ChatOutputPort
import com.poisonedyouth.chat.infrastructure.ExposedChatOutputPort
import com.poisonedyouth.configuration.DatabaseProperties
import com.poisonedyouth.configuration.ServerConfiguration
import com.poisonedyouth.event.application.EventService
import com.poisonedyouth.event.domain.EventInputPort
import com.poisonedyouth.event.domain.EventOutputPort
import com.poisonedyouth.event.infrastructure.ExposedEventRepository
import com.poisonedyouth.user.application.UserService
import com.poisonedyouth.user.domain.UserInputPort
import com.poisonedyouth.user.domain.UserOutputPort
import com.poisonedyouth.user.infrastructure.ExposedUserRepository
import org.http4k.server.Http4kServer
import org.http4k.server.KtorCIO
import org.http4k.server.asServer
import org.koin.core.context.stopKoin
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.wait.strategy.Wait

private class ChatPostgreSQLContainer(imageName: String) : PostgreSQLContainer<ChatPostgreSQLContainer>(imageName)

private val postgresqlContainer: ChatPostgreSQLContainer = initPostgresContainer()

private val testProperties =
    object : DatabaseProperties {
        override val driver: String
            get() = postgresqlContainer.driverClassName
        override val url: String
            get() = postgresqlContainer.jdbcUrl
        override val username: String
            get() = postgresqlContainer.username
        override val password: String
            get() = postgresqlContainer.password
    }

private val testModule: org.koin.core.module.Module =
    module {
        single {
            testProperties
        } bind DatabaseProperties::class
        singleOf(::ExposedUserRepository) { bind<UserOutputPort>() }
        singleOf(::UserService) { bind<UserInputPort>() }
        singleOf(::ExposedChatOutputPort) { bind<ChatOutputPort>() }
        singleOf(::ChatService) { bind<ChatInputPort>() }
        singleOf(::ExposedEventRepository) { bind<EventOutputPort>() }
        singleOf(::EventService) { bind<EventInputPort>() }
    }

fun startTestApplication(block: (Int) -> Unit) {
    postgresqlContainer.start()
    val server: Http4kServer = ServerConfiguration.initHttpHandlerWith(testModule).asServer(KtorCIO(9999))
    try {
        server.start()
        block(server.port())
    } finally {
        server.stop()
        stopKoin()
        postgresqlContainer.stop()
    }
}

private fun initPostgresContainer(): ChatPostgreSQLContainer {
    return ChatPostgreSQLContainer("postgres:15")
        .waitingFor(Wait.defaultWaitStrategy())
}
