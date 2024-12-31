package com.poisonedyouth.user.application

import arrow.core.Either
import arrow.core.raise.either
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.poisonedyouth.common.GenericException
import com.poisonedyouth.event.domain.Event
import com.poisonedyouth.event.domain.EventInputPort
import com.poisonedyouth.event.domain.EventType
import com.poisonedyouth.user.domain.User
import com.poisonedyouth.user.domain.UserAlreadyExistException
import com.poisonedyouth.user.domain.UserInputPort
import com.poisonedyouth.user.domain.UserNotFoundException
import com.poisonedyouth.user.domain.UserOutputPort
import java.time.Instant
import java.util.UUID

class UserService(
    private val userOutputPort: UserOutputPort,
    private val eventInputPort: EventInputPort,
) : UserInputPort {
    override fun getUserBy(username: String): Either<GenericException, User> =
        either {
            userOutputPort.findByUsername(username).bind()
                ?: raise(UserNotFoundException("User with name '$username' not found."))
        }

    override fun getUserBy(userId: UUID): Either<GenericException, User> =
        either {
            userOutputPort.findById(userId).bind()
                ?: raise(UserNotFoundException("User with id '$userId' not found."))
        }

    override fun createNewUser(user: User): Either<GenericException, User> =
        either {
            val existingUser = userOutputPort.findByUsername(user.username).bind()
            if (existingUser != null) {
                raise(UserAlreadyExistException("User with name '${user.username}' already exists."))
            }
            userOutputPort.save(user).bind().also {
                eventInputPort.publish(
                    Event(
                        id = UUID.randomUUID(),
                        createdAt = Instant.now(),
                        payload = jacksonObjectMapper().writeValueAsString(it),
                        type = EventType.CREATE_USER,
                    ),
                )
            }
        }
}
