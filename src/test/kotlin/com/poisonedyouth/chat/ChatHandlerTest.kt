package com.poisonedyouth.chat

import com.poisonedyouth.chat.domain.ChatInputPort
import com.poisonedyouth.chat.infrastructure.addChatHandler
import com.poisonedyouth.user.domain.User
import com.poisonedyouth.user.domain.UserInputPort
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.junit.jupiter.api.Test
import java.util.UUID

class ChatHandlerTest {
    @Test
    fun `addUserToChatHandler should fail when owner not exists`() {
        // given
        val chatInputPort = mockk<ChatInputPort>(relaxed = true)
        val userInputPort = mockk<UserInputPort>(relaxed = true)

        val userId = UUID.randomUUID()

        every {
            userInputPort.getUserBy(userId = any())
        } returns null

        // when
        val response =
            addChatHandler(
                chatInputPort = chatInputPort,
                userInputPort = userInputPort,
            )(
                Request(
                    method = Method.POST,
                    uri = "chat",
                ).body(
                    """
                    {
                      "owner": "$userId",
                      "messages" : [
                        "Hello World!"
                      ],
                      "userIds" : [
                        "$userId"
                      ]
                    } 
                    """.trimIndent(),
                ),
            )

        // then
        response.status shouldBe Status.NOT_FOUND
    }

    @Test
    fun `addUserToChatHandler should add new chat for existing owner`() {
        // given
        val chatInputPort = mockk<ChatInputPort>(relaxed = true)
        val userInputPort = mockk<UserInputPort>(relaxed = true)

        val userId = UUID.randomUUID()

        every {
            userInputPort.getUserBy(userId = any())
        } returns
            User(
                id = userId,
                username = "username",
            )

        // when
        val response =
            addChatHandler(
                chatInputPort = chatInputPort,
                userInputPort = userInputPort,
            )(
                Request(
                    method = Method.POST,
                    uri = "chat",
                ).body(
                    """
                    {
                      "owner": "$userId",
                      "messages" : [
                        "Hello World!"
                      ],
                      "userIds" : [
                        "$userId"
                      ]
                    } 
                    """.trimIndent(),
                ),
            )

        // then
        response.status shouldBe Status.CREATED
    }
}
