package com.poisonedyouth.user.domain

import arrow.core.Either
import com.poisonedyouth.common.GenericException
import java.util.UUID

interface UserInputPort {
    fun getUserBy(username: String): Either<GenericException, User?>

    fun getUserBy(userId: UUID): Either<GenericException, User?>

    fun createNewUser(user: User): Either<GenericException, User>
}
