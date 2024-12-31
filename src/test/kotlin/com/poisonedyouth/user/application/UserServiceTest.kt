package com.poisonedyouth.user.application

import arrow.core.Either
import com.poisonedyouth.event.domain.EventInputPort
import com.poisonedyouth.user.domain.User
import com.poisonedyouth.user.domain.UserAlreadyExistException
import com.poisonedyouth.user.domain.UserNotFoundException
import com.poisonedyouth.user.domain.UserOutputPort
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import java.util.UUID

class UserServiceTest {

    private val eventInputPort = mockk<EventInputPort>(relaxed = true)
    private val userOutputPort = mockk<UserOutputPort>(relaxed = true)
    private val userService =
        UserService(
            userOutputPort = userOutputPort,
            eventInputPort = eventInputPort,
        )

    @Test
    fun `getUserBy using username should fail when user does not exist`()  {
        // given
        val username = "test-user"

        every {
            userOutputPort.findByUsername(username)
        } returns Either.Right(null)

        // when
        val actual = userService.getUserBy(username)

        // then
        actual.shouldBeLeft().shouldBeTypeOf<UserNotFoundException>()
    }

    @Test
    fun `getUserBy using username should return existing user`()  {
        // given
        val username = "test-user"

        val expectedUser =
            User(
                id = UUID.randomUUID(),
                username = username,
            )
        every {
            userOutputPort.findByUsername(username)
        } returns
            Either.Right(
                expectedUser,
            )

        // when
        val actual = userService.getUserBy(username)

        // then
        actual.shouldBeRight().shouldBe(expectedUser)
    }

    @Test
    fun `getUserBy using userId should fail when user does not exist`()  {
        // given
        val userId = UUID.randomUUID()

        every {
            userOutputPort.findById(userId)
        } returns Either.Right(null)

        // when
        val actual = userService.getUserBy(userId)

        // then
        actual.shouldBeLeft().shouldBeTypeOf<UserNotFoundException>()
    }

    @Test
    fun `getUserBy should return existing user`()  {
        // given
        val userId = UUID.randomUUID()

        val expectedUser =
            User(
                id = userId,
                username = "test-user",
            )
        every {
            userOutputPort.findById(userId)
        } returns
            Either.Right(
                expectedUser,
            )

        // when
        val actual = userService.getUserBy(userId)

        // then
        actual.shouldBeRight().shouldBe(expectedUser)
    }

    @Test
    fun `createNewUser should save new user`()  {
        // given
        val username = "test-user"

        val existingUser =
            User(
                id = UUID.randomUUID(),
                username = username,
            )
        every {
            userOutputPort.findByUsername(username)
        } returns Either.Right(existingUser)

        // when
        val actual = userService.createNewUser(existingUser)

        // then
        actual.shouldBeLeft().shouldBeTypeOf<UserAlreadyExistException>()
    }

    @Test
    fun `createNewUser should fail when user already exists`()  {
        // given
        val username = "test-user"

        val existingUser =
            User(
                id = UUID.randomUUID(),
                username = username,
            )
        every {
            userOutputPort.findByUsername(username)
        } returns Either.Right(null)

        every {
            userOutputPort.save(existingUser)
        } returns Either.Right(existingUser)

        // when
        val actual = userService.createNewUser(existingUser)

        // then
        actual.shouldBeRight()
    }
}
