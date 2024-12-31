package com.poisonedyouth.user.domain

import com.poisonedyouth.common.GenericException

class UserNotFoundException(override val message: String) : GenericException(message)
