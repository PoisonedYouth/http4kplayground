package com.poisonedyouth.user

import java.util.UUID

class UserService(
    private val userRepository: UserRepository
) : UserInputPort {
    override fun getUserBy(username: String): User? {
        return userRepository.findByUsername(username)
    }

    override fun getUserBy(userId: UUID): User? {
        return userRepository.findById(userId)
    }

    override fun createNewUser(user: User): User {
        val existingUser = userRepository.findByUsername(user.username)
        require(existingUser == null) {
            "User already exists"
        }
        return userRepository.save(user)
    }
}