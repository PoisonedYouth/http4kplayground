package com.poisonedyouth.user.domain

import arrow.core.Either
import com.poisonedyouth.common.GenericException
import java.util.UUID

interface UserOutputPort {
    fun save(user: User): Either<GenericException, User>

    fun findById(id: UUID): Either<GenericException, User?>

    fun findByUsername(username: String): Either<GenericException, User?>

    fun deleteById(id: UUID): Either<GenericException, Unit>

    fun findAll(): Either<GenericException, List<User>>
}
