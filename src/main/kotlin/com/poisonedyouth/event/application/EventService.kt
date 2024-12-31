package com.poisonedyouth.event.application

import com.poisonedyouth.event.domain.Event
import com.poisonedyouth.event.domain.EventInputPort
import com.poisonedyouth.event.domain.EventOutputPort

class EventService(
    private val eventOutputPort: EventOutputPort,
) : EventInputPort {
    override fun publish(event: Event) {
        eventOutputPort.saveEvent(event)
    }
}
