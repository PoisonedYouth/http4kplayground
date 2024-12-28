package com.poisonedyouth.user.infrastructure

import com.poisonedyouth.user.domain.User
import com.poisonedyouth.user.domain.UserOutputPort
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

class ExposedUserRepository : UserOutputPort {

    override fun save(user: User): User = transaction {
        UserTable.insert {
            it[UserTable.id] = user.id
            it[username] = user.username
        }
        user
    }

    override fun findById(id: UUID): User? = transaction {
        UserTable.selectAll().where(UserTable.id eq id).singleOrNull()?.let {
            User(
                id = it[UserTable.id].value,
                username = it[UserTable.username]
            )
        }
    }

    override fun deleteById(id: UUID): Unit = transaction {
        UserTable.deleteWhere {
            UserTable.id eq id
        }
    }

    override fun findAll(): List<User> = transaction {
        UserTable.selectAll().map {
            User(
                id = it[UserTable.id].value,
                username = it[UserTable.username]
            )
        }
    }

    override fun findByUsername(username: String): User? = transaction {
        UserTable.selectAll().where(UserTable.username eq username).singleOrNull()?.let {
            User(
                id = it[UserTable.id].value,
                username = it[UserTable.username]
            )
        }
    }
}
 object UserTable : UUIDTable("app_user") {
    val username = varchar("username", 255).uniqueIndex()
}
