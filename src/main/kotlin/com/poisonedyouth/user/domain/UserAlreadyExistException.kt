package com.poisonedyouth.user.domain

import com.poisonedyouth.common.GenericException

class UserAlreadyExistException(override val message: String) : GenericException(message)
