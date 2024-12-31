package com.poisonedyouth.event.domain

import arrow.core.Either
import com.poisonedyouth.common.GenericException

interface EventOutputPort {
    fun saveEvent(event: Event): Either<GenericException, Unit>

    fun allEvents(): Either<GenericException, List<Event>>
}
