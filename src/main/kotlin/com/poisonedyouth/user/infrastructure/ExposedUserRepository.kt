package com.poisonedyouth.user.infrastructure

import arrow.core.Either
import com.poisonedyouth.common.GenericException
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
    override fun save(user: User): Either<GenericException, User> =
        Either.catch {
            transaction {
                UserTable.insert {
                    it[UserTable.id] = user.id
                    it[username] = user.username
                }
                user
            }
        }.mapLeft {
            GenericException("Failed to save user.", it)
        }

    override fun findById(id: UUID): Either<GenericException, User?> =
        Either.catch {
            transaction {
                UserTable.selectAll().where(UserTable.id eq id).singleOrNull()?.let {
                    User(
                        id = it[UserTable.id].value,
                        username = it[UserTable.username],
                    )
                }
            }
        }.mapLeft {
            GenericException("Failed to load user.", it)
        }

    override fun deleteById(id: UUID): Either<GenericException, Unit> =
        Either.catch {
            transaction {
                UserTable.deleteWhere {
                    UserTable.id eq id
                }
            }
            Unit
        }.mapLeft {
            GenericException("Failed to delete user.", it)
        }

    override fun findAll(): Either<GenericException, List<User>> =
        Either.catch {
            transaction {
                UserTable.selectAll().map {
                    User(
                        id = it[UserTable.id].value,
                        username = it[UserTable.username],
                    )
                }
            }
        }.mapLeft {
            GenericException("Failed to load users.", it)
        }

    override fun findByUsername(username: String): Either<GenericException, User?> =
        Either.catch {
            transaction {
                UserTable.selectAll().where(UserTable.username eq username).singleOrNull()?.let {
                    User(
                        id = it[UserTable.id].value,
                        username = it[UserTable.username],
                    )
                }
            }
        }.mapLeft {
            GenericException("Failed to load user.", it)
        }
}

object UserTable : UUIDTable("app_user") {
    val username = varchar("username", 255).uniqueIndex()
}
