package com.poisonedyouth.user.application

import com.poisonedyouth.user.domain.User
import com.poisonedyouth.user.domain.UserInputPort
import com.poisonedyouth.user.domain.UserOutputPort
import java.util.UUID

class UserService(
    private val userOutputPort: UserOutputPort
) : UserInputPort {
    override fun getUserBy(username: String): User? {
        return userOutputPort.findByUsername(username)
    }

    override fun getUserBy(userId: UUID): User? {
        return userOutputPort.findById(userId)
    }

    override fun createNewUser(user: User): User {
        val existingUser = userOutputPort.findByUsername(user.username)
        require(existingUser == null) {
            "User already exists"
        }
        return userOutputPort.save(user)
    }
}