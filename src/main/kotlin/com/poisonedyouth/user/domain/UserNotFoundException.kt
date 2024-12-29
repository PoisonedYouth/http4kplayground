package com.poisonedyouth.user.domain

class UserNotFoundException(override val message: String): RuntimeException(message)