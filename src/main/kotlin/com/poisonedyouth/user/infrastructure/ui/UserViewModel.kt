package com.poisonedyouth.user.infrastructure.ui

import org.http4k.template.ViewModel
import java.util.UUID

data class UserViewModel(
    val users: List<UserDto>,
) : ViewModel {

    override fun template(): String {
        return "UserViewModel.html"
    }
}

data class UserDto(
    val id: UUID,
    val username: String,
)
