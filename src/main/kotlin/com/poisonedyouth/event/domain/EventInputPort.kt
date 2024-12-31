package com.poisonedyouth.event.domain

interface EventInputPort {
    fun publish(event: Event)
}
