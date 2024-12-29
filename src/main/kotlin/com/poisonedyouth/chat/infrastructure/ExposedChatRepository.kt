package com.poisonedyouth.chat.infrastructure

import com.poisonedyouth.chat.domain.Chat
import com.poisonedyouth.chat.domain.ChatOutputPort
import com.poisonedyouth.chat.domain.Message
import com.poisonedyouth.user.infrastructure.UserTable
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.util.UUID

class ExposedChatOutputport : ChatOutputPort {
    override fun findAll(): List<Chat> =
        transaction {
            ChatTable.selectAll().map {
                mapResultRowToChat(it)
            }
        }

    private fun mapResultRowToChat(it: ResultRow): Chat {
        val chatId = it[ChatTable.id].value
        val chat =
            Chat(
                id = chatId,
                createdAt = it[ChatTable.createdAt],
                owner = it[ChatTable.owner].value,
            )
        ChatUserTable.selectAll().where({ ChatUserTable.chatId eq chatId }).forEach { user ->
            chat.addUser(UUID.fromString(user[ChatUserTable.userId].toString()))
        }
        ChatMessageTable.selectAll().where({ ChatMessageTable.chatId eq chatId }).forEach { message ->
            chat.addMessage(
                message = message[ChatMessageTable.message],
                createdBy = message[ChatMessageTable.createdBy],
                createdAt = message[ChatMessageTable.createdAt],
            )
        }
        return chat
    }

    override fun save(chat: Chat): Chat =
        transaction {
            if (findById(chat.id) != null) {
                ChatTable.update({ ChatTable.id eq chat.id }) {
                    it[createdAt] = chat.createdAt
                    it[owner] = chat.owner
                }
            } else {
                ChatTable.insert {
                    it[ChatTable.id] = chat.id
                    it[createdAt] = chat.createdAt
                    it[owner] = chat.owner
                }
            }
            chat.getMessages().forEach {
                addOrUpdateChatMessage(it, chat.id)
            }
            chat.getUsers().forEach {
                addOrUpdateChatUser(it, chat.id)
            }
            chat
        }

    override fun findById(id: UUID): Chat? =
        transaction {
            ChatTable.selectAll().where(ChatTable.id eq id).singleOrNull()?.let {
                mapResultRowToChat(it)
            }
        }

    override fun deleteById(id: UUID): Unit =
        transaction {
            ChatTable.deleteWhere {
                ChatTable.id eq id
            }
        }

    private fun addOrUpdateChatMessage(
        message: Message,
        chatId: UUID,
    ) = transaction {
        val existingMessage =
            ChatMessageTable.selectAll().where {
                ChatMessageTable.id eq message.id
            }.singleOrNull()
        if (existingMessage != null) {
            ChatMessageTable.update({ ChatMessageTable.id eq message.id }) {
                it[ChatMessageTable.chatId] = chatId
                it[ChatMessageTable.message] = message.message
                it[createdBy] = message.createdBy
                it[createdAt] = message.createdAt
            }
        } else {
            ChatMessageTable.insert {
                it[id] = message.id
                it[ChatMessageTable.chatId] = chatId
                it[ChatMessageTable.message] = message.message
                it[createdBy] = message.createdBy
                it[createdAt] = message.createdAt
            }
        }
    }

    private fun addOrUpdateChatUser(
        userId: UUID,
        chatId: UUID,
    ) = transaction {
        val existingUser =
            ChatUserTable.selectAll().where {
                ChatUserTable.userId eq userId
            }.singleOrNull()
        if (existingUser == null) {
            ChatUserTable.insert {
                it[ChatUserTable.userId] = userId
                it[ChatUserTable.chatId] = chatId
            }
        }
    }
}

object ChatTable : UUIDTable("chat") {
    val createdAt = timestamp("created_at")
    val owner = reference("owner", UserTable.id)
}

object ChatMessageTable : UUIDTable("chat_message") {
    val chatId = reference("chat_id", ChatTable.id)
    val message = text("message")
    val createdBy = uuid("created_by")
    val createdAt = timestamp("created_at")
}

object ChatUserTable : UUIDTable("chat_user") {
    val chatId = reference("chat_id", ChatTable.id)
    val userId = reference("user_id", UserTable.id)
}
