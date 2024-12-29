package com.poisonedyouth.user.infrastructure

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
        val result = userInputPort.createNewUser(
            User(
                id = UUID.randomUUID(),
                username = userDto.username,
            ),
        )

        when (result.isSuccess) {
            true -> Response(status = Status.CREATED).body(result.getOrNull()!!.id.toString())
            false -> handleFailure(result)
        }
    }

private fun handleFailure(result: Result<Any>) = when (val exception = result.exceptionOrNull()!!) {
    is UserNotFoundException ->
        Response(status = Status.NOT_FOUND)
            .body(exception.message)

    is UserAlreadyExistException ->
        Response(status = Status.CONFLICT).body(exception.message)

    else ->
        Response(status = Status.INTERNAL_SERVER_ERROR)
            .body(exception.message ?: "Unknown error occurred.")
}


data class NewUserDto(
    val username: String,
)
