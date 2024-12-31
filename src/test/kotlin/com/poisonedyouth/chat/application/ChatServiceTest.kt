package com.poisonedyouth.chat.application

import arrow.core.Either
import com.poisonedyouth.chat.domain.Chat
import com.poisonedyouth.chat.domain.ChatNotFoundException
import com.poisonedyouth.chat.domain.ChatOutputPort
import com.poisonedyouth.chat.domain.Message
import com.poisonedyouth.event.domain.EventInputPort
import com.poisonedyouth.user.domain.User
import com.poisonedyouth.user.domain.UserInputPort
import com.poisonedyouth.user.domain.UserNotFoundException
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.matchers.types.shouldBeTypeOf
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.UUID

class ChatServiceTest {
    private val chatOutputPort = mockk<ChatOutputPort>(relaxed = true)
    private val userInputPort = mockk<UserInputPort>(relaxed = true)
    private val eventInputPort = mockk<EventInputPort>(relaxed = true)
    private val chatService =
        ChatService(
            chatOutputPort = chatOutputPort,
            userInputPort = userInputPort,
            eventInputPort = eventInputPort,
        )

    @Test
    fun `createNewChat should fail when owner does not exist`() {
        // given
        val owner = UUID.randomUUID()
        val messages = listOf("Hello World!")
        val userIds = listOf<UUID>(UUID.randomUUID())

        every {
            userInputPort.getUserBy(owner)
        } returns Either.Right(null)

        // when
        val actual = chatService.createNewChat(owner = owner, messages = messages, userIds = userIds)

        actual.shouldBeLeft().shouldBeTypeOf<UserNotFoundException>()
    }

    @Test
    fun `createNewChat should fail when one of the userIds does not exist`() {
        // given
        val owner = UUID.randomUUID()
        val messages = listOf("Hello World!")
        val unknownUserId = UUID.randomUUID()
        val userIds = listOf<UUID>(owner, unknownUserId)

        every {
            userInputPort.getUserBy(owner)
        } returns Either.Right(User(id = owner, username = "test-user"))

        every {
            userInputPort.getUserBy(unknownUserId)
        } returns Either.Right(null)

        // when
        val actual = chatService.createNewChat(owner = owner, messages = messages, userIds = userIds)

        actual.shouldBeLeft().shouldBeTypeOf<UserNotFoundException>()
    }

    @Test
    fun `createNewChat should save new chat`() {
        // given
        val owner = UUID.randomUUID()
        val messages = listOf("Hello World!")
        val otherUserId = UUID.randomUUID()
        val userIds = listOf<UUID>(owner, otherUserId)

        every {
            userInputPort.getUserBy(owner)
        } returns Either.Right(User(id = owner, username = "test-user"))

        every {
            userInputPort.getUserBy(otherUserId)
        } returns Either.Right(User(id = otherUserId, username = "other-user"))

        every {
            chatOutputPort.save(any())
        } returns
                Either.Right(
                    Chat(
                        id = UUID.randomUUID(),
                        createdAt = Instant.now(),
                        owner = owner,
                    ),
                )

        // when
        val actual = chatService.createNewChat(owner = owner, messages = messages, userIds = userIds)

        actual.shouldBeRight()
    }

    @Test
    fun `getChat should return chat when available`() {
        // given
        val chatId = UUID.randomUUID()

        every {
            chatOutputPort.findById(chatId)
        } returns Either.Right(Chat(id = chatId, createdAt = Instant.now(), owner = UUID.randomUUID()))

        // when
        val actual = chatService.getChat(chatId)

        actual.shouldBeRight()
    }

    @Test
    fun `getChat should fail when chat is not available`() {
        // given
        val chatId = UUID.randomUUID()

        every {
            chatOutputPort.findById(chatId)
        } returns Either.Right(null)

        // when
        val actual = chatService.getChat(chatId)

        actual.shouldBeLeft().shouldBeTypeOf<ChatNotFoundException>()
    }

    @Test
    fun `addMessageToChat should fail when chat is not available`() {
        // given
        val chatId = UUID.randomUUID()

        every {
            chatOutputPort.findById(chatId)
        } returns Either.Right(null)

        val message =
            Message(
                id = UUID.randomUUID(),
                message = "Hello World!",
                createdBy = UUID.randomUUID(),
                createdAt = Instant.now(),
            )

        // when
        val actual = chatService.addMessageToChat(chatId = chatId, message = message)

        actual.shouldBeLeft().shouldBeTypeOf<ChatNotFoundException>()
    }

    @Test
    fun `addMessageToChat should add message to existing chat`() {
        // given
        val chatId = UUID.randomUUID()

        every {
            chatOutputPort.findById(chatId)
        } returns
                Either.Right(
                    Chat(id = chatId, createdAt = Instant.now(), owner = UUID.randomUUID()),
                )

        val message =
            Message(
                id = UUID.randomUUID(),
                message = "Hello World!",
                createdBy = UUID.randomUUID(),
                createdAt = Instant.now(),
            )

        every {
            chatOutputPort.save(any())
        } returns
                Either.Right(
                    Chat(id = chatId, createdAt = Instant.now(), owner = UUID.randomUUID()).addMessage(message),
                )

        // when
        val actual = chatService.addMessageToChat(chatId = chatId, message = message)

        actual.shouldBeRight()

        verify(exactly = 1) {
            chatOutputPort.save(any())
        }
    }

    @Test
    fun `addUsersToChat should fail when chat is not available`() {
        // given
        val chatId = UUID.randomUUID()

        every {
            chatOutputPort.findById(chatId)
        } returns Either.Right(null)

        val userIds = listOf<UUID>(UUID.randomUUID())

        // when
        val actual = chatService.addUsersToChat(chatId = chatId, userIds = userIds)

        actual.shouldBeLeft().shouldBeTypeOf<ChatNotFoundException>()
    }

    @Test
    fun `addUsersToChat should fail when one of the userIds does not exist`() {
        // given
        val chatId = UUID.randomUUID()

        every {
            chatOutputPort.findById(chatId)
        } returns
                Either.Right(
                    Chat(id = chatId, createdAt = Instant.now(), owner = UUID.randomUUID()),
                )

        val userIds = listOf<UUID>(UUID.randomUUID())

        every {
            userInputPort.getUserBy(userIds[0])
        } returns Either.Right(null)

        // when
        val actual = chatService.addUsersToChat(chatId = chatId, userIds = userIds)

        actual.shouldBeLeft().shouldBeTypeOf<UserNotFoundException>()
    }

    @Test
    fun `addUsersToChat should add multiple users to existing chat`() {
        // given
        val chatId = UUID.randomUUID()

        every {
            chatOutputPort.findById(chatId)
        } returns
                Either.Right(
                    Chat(id = chatId, createdAt = Instant.now(), owner = UUID.randomUUID()),
                )

        val userId1 = UUID.randomUUID()
        val userId2 = UUID.randomUUID()
        val userIds = listOf<UUID>(userId1, userId2)

        every {
            userInputPort.getUserBy(userId1)
        } returns Either.Right(User(id = userId1, username = "test-user-1"))
        every {
            userInputPort.getUserBy(userId2)
        } returns Either.Right(User(id = userId2, username = "test-user-2"))

        val expectedChat = Chat(
            id = chatId, createdAt = Instant.now(), owner = UUID.randomUUID()
        ).addUser(userId1).addUser(userId2)
        every {
            chatOutputPort.save(any())
        } returns
                Either.Right(
                    expectedChat,
                )

        // when
        val actual = chatService.addUsersToChat(chatId = chatId, userIds = userIds)

        actual.shouldBeRight()

        verify(exactly = 1) {
            chatOutputPort.save(expectedChat)
        }
    }

    @Test
    fun `removeUserFromChat should fail when chat is not available`() {
        // given
        val chatId = UUID.randomUUID()

        every {
            chatOutputPort.findById(chatId)
        } returns Either.Right(null)

        val userId = UUID.randomUUID()

        // when
        val actual = chatService.removeUserFromChat(chatId = chatId, userId = userId)

        actual.shouldBeLeft().shouldBeTypeOf<ChatNotFoundException>()
    }

    @Test
    fun `removeUserFromChat should remove existing user from existing chat`() {
        // given
        val chatId = UUID.randomUUID()

        val userId = UUID.randomUUID()

        every {
            chatOutputPort.findById(chatId)
        } returns
                Either.Right(
                    Chat(id = chatId, createdAt = Instant.now(), owner = UUID.randomUUID()).addUser(userId),
                )

        every {
            chatOutputPort.save(any())
        } returns
                Either.Right(
                    Chat(id = chatId, createdAt = Instant.now(), owner = UUID.randomUUID()),
                )

        // when
        val actual = chatService.removeUserFromChat(chatId = chatId, userId = userId)

        actual.shouldBeRight()

        verify(exactly = 1) {
            chatOutputPort.save(Chat(id = chatId, createdAt = Instant.now(), owner = UUID.randomUUID()))
        }
    }
}
