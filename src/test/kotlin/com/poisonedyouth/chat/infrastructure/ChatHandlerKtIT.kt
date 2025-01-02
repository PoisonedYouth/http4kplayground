package com.poisonedyouth.chat.infrastructure

import com.fasterxml.jackson.core.type.TypeReference
import com.poisonedyouth.chat.domain.Chat
import com.poisonedyouth.chat.domain.ChatOutputPort
import com.poisonedyouth.chat.domain.Message
import com.poisonedyouth.configuration.objectMapper
import com.poisonedyouth.startTestApplication
import com.poisonedyouth.user.domain.User
import com.poisonedyouth.user.domain.UserOutputPort
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.http4k.client.ApacheClient
import org.http4k.core.Method
import org.http4k.core.Request
import org.junit.jupiter.api.Test
import org.koin.java.KoinJavaComponent.inject
import java.time.Instant
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

    @Test
    fun `getAllChats should return all available chats`() {
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
            val chatId2 = UUID.randomUUID()
            val chat2 =
                Chat(
                    id = chatId2,
                    createdAt = Instant.now(),
                    owner = userId,
                ).addMessage(
                    Message(
                        id = UUID.randomUUID(),
                        message = "Hello world",
                        createdBy = userId,
                        createdAt = Instant.now(),
                    ),
                )
            chatOutputPort.save(chat2).shouldBeRight()

            val chatId1 = UUID.randomUUID()
            val chat1 =
                Chat(
                    id = chatId1,
                    createdAt = Instant.now(),
                    owner = userId,
                ).addMessage(
                    Message(
                        id = UUID.randomUUID(),
                        message = "Hello world",
                        createdBy = userId,
                        createdAt = Instant.now(),
                    ),
                )
            chatOutputPort.save(chat1).shouldBeRight()

            val request =
                Request(
                    method = Method.GET,
                    uri = "http://localhost:$port/chat",
                )

            // when
            val actual = client(request)

            // then
            actual.status.code shouldBe 200
            val result = objectMapper.readValue(actual.bodyString(), object : TypeReference<List<ChatDto>>() {})
            result.map { it.id } shouldContainExactlyInAnyOrder listOf(chatId1.toString(), chatId2.toString())
        }
    }

    @Test
    fun `getChat should return matching chat when available`() {
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
            val chatId2 = UUID.randomUUID()
            val chat2 =
                Chat(
                    id = chatId2,
                    createdAt = Instant.now(),
                    owner = userId,
                ).addMessage(
                    Message(
                        id = UUID.randomUUID(),
                        message = "Hello world",
                        createdBy = userId,
                        createdAt = Instant.now(),
                    ),
                )
            chatOutputPort.save(chat2).shouldBeRight()

            val chatId1 = UUID.randomUUID()
            val chat1 =
                Chat(
                    id = chatId1,
                    createdAt = Instant.now(),
                    owner = userId,
                ).addMessage(
                    Message(
                        id = UUID.randomUUID(),
                        message = "Hello world",
                        createdBy = userId,
                        createdAt = Instant.now(),
                    ),
                )
            chatOutputPort.save(chat1).shouldBeRight()

            val request =
                Request(
                    method = Method.GET,
                    uri = "http://localhost:$port/chat/$chatId1",
                )

            // when
            val actual = client(request)

            // then
            actual.status.code shouldBe 200
            val result = objectMapper.readValue(actual.bodyString(), object : TypeReference<ChatDto>() {})
            result.id shouldBe chatId1.toString()
        }
    }
}
