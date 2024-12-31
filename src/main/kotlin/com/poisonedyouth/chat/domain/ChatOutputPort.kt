package com.poisonedyouth.chat.domain

import arrow.core.Either
import com.poisonedyouth.common.GenericException
import java.util.UUID

interface ChatOutputPort {
    fun save(chat: Chat): Either<GenericException, Chat>

    fun findById(id: UUID): Either<GenericException, Chat?>

    fun deleteById(id: UUID): Either<GenericException, Unit>

    fun findAll(): Either<GenericException, List<Chat>>
}
