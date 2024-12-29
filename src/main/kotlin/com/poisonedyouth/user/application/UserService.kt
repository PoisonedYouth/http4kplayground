package com.poisonedyouth.user.application

import com.poisonedyouth.user.domain.User
import com.poisonedyouth.user.domain.UserAlreadyExistException
import com.poisonedyouth.user.domain.UserInputPort
import com.poisonedyouth.user.domain.UserNotFoundException
import com.poisonedyouth.user.domain.UserOutputPort
import java.util.UUID

class UserService(
    private val userOutputPort: UserOutputPort,
) : UserInputPort {
    override fun getUserBy(username: String): Result<User> = Result.runCatching {
        userOutputPort.findByUsername(username).getOrThrow() ?: throw UserNotFoundException("User with name '$username' not found.")
    }

    override fun getUserBy(userId: UUID): Result<User> = Result.runCatching {
        userOutputPort.findById(userId).getOrThrow() ?: throw UserNotFoundException("User with id '$userId' not found.")
    }

    override fun createNewUser(user: User): Result<User> = Result.runCatching {
        val existingUser = userOutputPort.findByUsername(user.username).getOrThrow()
        if (existingUser != null) {
            throw UserAlreadyExistException("User with name '${user.username}' already exists.")
        }
        userOutputPort.save(user).getOrThrow()
    }
}
