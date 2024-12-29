package com.poisonedyouth.user.domain

class UserAlreadyExistException(override val message: String): RuntimeException(message)