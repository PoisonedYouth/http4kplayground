package com.poisonedyouth.event.infrastructure

import arrow.core.Either
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.poisonedyouth.common.GenericException
import com.poisonedyouth.event.domain.Event
import com.poisonedyouth.event.domain.EventOutputPort
import com.poisonedyouth.event.domain.EventType
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class ExposedEventRepository : EventOutputPort {
    override fun saveEvent(event: Event): Either<GenericException, Unit> =
        Either.catch {
            transaction {
                EventTable.insert {
                    it[EventTable.id] = event.id
                    it[createdAt] = event.createdAt
                    it[type] = event.type
                    it[payload] = jacksonObjectMapper().writeValueAsString(event.payload)
                }
            }
            Unit
        }.mapLeft {
            GenericException("Failed to save event.", it)
        }

    override fun allEvents(): Either<GenericException, List<Event>> =
        Either.catch {
            transaction {
                EventTable.selectAll().map {
                    Event(
                        id = it[EventTable.id].value,
                        createdAt = it[EventTable.createdAt],
                        type = it[EventTable.type],
                        payload = jacksonObjectMapper().readValue(it[EventTable.payload], it[EventTable.type].type),
                    )
                }
            }
        }.mapLeft {
            GenericException("Failed to load events.", it)
        }
}

object EventTable : UUIDTable("event") {
    val createdAt = timestamp("created_at")
    val payload = text("payload")
    val type = enumeration("event_type", EventType::class)
}
