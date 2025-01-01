package com.poisonedyouth.chat.infrastructure

import com.poisonedyouth.chat.domain.ChatOutputPort
import com.poisonedyouth.configuration.objectMapper
import com.poisonedyouth.startTestApplication
import com.poisonedyouth.user.domain.User
import com.poisonedyouth.user.domain.UserOutputPort
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.http4k.client.ApacheClient
import org.http4k.core.Method
import org.http4k.core.Request
import org.junit.jupiter.api.Test
import org.koin.java.KoinJavaComponent.inject
import java.util.UUID

class ChatHandlerKtIT {
    private val client = ApacheClient()

    @Test
    fun `addChatHandler should add new chat`() {
        startTestApplication { port ->
            val userOutputPort by inject<UserOutputPort>(UserOutputPort::class.java)
            val chatOutputPort by inject<ChatOutputPort>(ChatOutputPort::class.java)

            val userId = UUID.randomUUID()
            userOutputPort.save(
                User(
                    id = userId,
                    username = "test-user",
                ),
            )

            val request =
                Request(
                    method = Method.POST,
                    uri = "http://localhost:$port/chat",
                ).body(
                    objectMapper.writeValueAsString(
                        NewChatDto(
                            owner = userId.toString(),
                            messages = listOf("Hello world"),
                            userIds = listOf(userId.toString()),
                        ),
                    ),
                )

            // when
            val actual = client(request)

            // then
            actual.status.code shouldBe 201

            chatOutputPort.findById(UUID.fromString(actual.bodyString())).shouldBeRight() shouldNotBe null
        }
    }
}
