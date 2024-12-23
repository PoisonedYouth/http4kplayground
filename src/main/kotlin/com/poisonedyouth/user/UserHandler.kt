package com.poisonedyouth.user

import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.format.Jackson
import java.util.UUID

fun addUserHandler(userInputPort: UserInputPort): HttpHandler = { request ->
    val userDto = Jackson.asA<NewUserDto>(request.bodyString())
    val existingUser = userInputPort.getUserBy(userDto.username)
    if (existingUser != null) {
        Response(status = Status.CONFLICT).body("User '${userDto.username}' already exists.")
    } else {
        val user = userInputPort.createNewUser(
            User(
                id = UUID.randomUUID(),
                username = userDto.username,
            )
        )
        Response(status = Status.CREATED).body(user.id.toString())
    }
}

data class NewUserDto(
    val username: String,
)