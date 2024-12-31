package com.poisonedyouth.chat.domain

import com.poisonedyouth.user.domain.User
import io.kotest.matchers.collections.shouldHaveSize
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.UUID

class ChatTest {
    @Test
    fun `addMessage not adds duplicates`() {
        // given
        val chat =
            Chat(
                id = UUID.randomUUID(),
                createdAt = Instant.now(),
                owner = UUID.randomUUID(),
            )

        val message =
            Message(
                id = UUID.randomUUID(),
                createdAt = Instant.now(),
                createdBy = UUID.randomUUID(),
                message = "Hello World",
            )

        // when
        chat.addMessage(message)
        chat.addMessage(message)

        // then
        chat.getMessages() shouldHaveSize 1
    }

    @Test
    fun `addUser not adds duplicates`() {
        // given
        val chat =
            Chat(
                id = UUID.randomUUID(),
                createdAt = Instant.now(),
                owner = UUID.randomUUID(),
            )

        val user =
            User(
                id = UUID.randomUUID(),
                username = "test-user",
            )

        // when
        chat.addUser(user.id)
        chat.addUser(user.id)

        // then
        chat.getUsers() shouldHaveSize 1
    }
}
