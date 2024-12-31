package com.poisonedyouth.event.domain

import com.poisonedyouth.chat.domain.Chat
import com.poisonedyouth.user.domain.User
import java.time.Instant
import java.util.UUID

data class Event(
    val id: UUID,
    val createdAt: Instant,
    val payload: Any,
    val type: EventType,
)

enum class EventType(val type: Class<*>) {
    CREATE_USER(User::class.java),
    DELETE_USER(User::class.java),
    CREATE_CHAT(Chat::class.java),
    ADD_MESSAGE_TO_CHAT(Chat::class.java),
    ADD_USER_TO_CHAT(Chat::class.java),
    REMOVE_USER_FROM_CHAT(Chat::class.java),
}
