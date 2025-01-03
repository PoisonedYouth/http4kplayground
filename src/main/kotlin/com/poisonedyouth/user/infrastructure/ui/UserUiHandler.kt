package com.poisonedyouth.user.infrastructure.ui

import com.poisonedyouth.common.GenericException
import com.poisonedyouth.user.domain.UserAlreadyExistException
import com.poisonedyouth.user.domain.UserInputPort
import com.poisonedyouth.user.domain.UserNotFoundException
import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.template.ThymeleafTemplates
import org.http4k.template.renderToResponse

private val renderer = ThymeleafTemplates().HotReload("src/main/resources/templates")

fun getUiUserHandler(userInputPort: UserInputPort): HttpHandler =
    {
        val result = userInputPort.getAllUsers()
        result.fold(
            ifLeft = { handleFailure(it) },
            ifRight = { userList ->
                renderer.renderToResponse(
                    UserViewModel(
                        users =
                            userList.map {
                                UserDto(
                                    id = it.id,
                                    username = it.username,
                                )
                            },
                    ),
                )
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
