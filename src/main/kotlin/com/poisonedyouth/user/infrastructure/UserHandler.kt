package com.poisonedyouth.user.infrastructure

import com.poisonedyouth.common.GenericException
import com.poisonedyouth.user.domain.User
import com.poisonedyouth.user.domain.UserAlreadyExistException
import com.poisonedyouth.user.domain.UserInputPort
import com.poisonedyouth.user.domain.UserNotFoundException
import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.format.Jackson
import java.util.UUID

fun addUserHandler(userInputPort: UserInputPort): HttpHandler =
    { request ->
        val userDto = Jackson.asA<NewUserDto>(request.bodyString())
        val result =
            userInputPort.createNewUser(
                User(
                    id = UUID.randomUUID(),
                    username = userDto.username,
                ),
            )

        result.fold(
            ifLeft = { handleFailure(it) },
            ifRight = {
                Response(status = Status.CREATED).body(it.id.toString())
            },
        )
    }

private fun handleFailure(e: GenericException) =
    when (e) {
        is UserNotFoundException ->
            Response(status = Status.NOT_FOUND)
                .body(e.message)

        is UserAlreadyExistException ->
            Response(status = Status.CONFLICT).body(e.message)

        else ->
            Response(status = Status.INTERNAL_SERVER_ERROR)
                .body(e.message ?: "Unknown error occurred.")
    }

data class NewUserDto(
    val username: String,
)
