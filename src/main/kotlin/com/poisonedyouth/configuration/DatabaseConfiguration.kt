package com.poisonedyouth.configuration

import com.poisonedyouth.chat.ChatTable
import com.poisonedyouth.chat.ChatUserTable
import com.poisonedyouth.chat.ChatMessageTable
import com.poisonedyouth.user.UserTable
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.Properties

data object DatabaseConfiguration {

    private lateinit var instance: Database

    fun initialize() {
        val properties = Properties()
        properties.load(DatabaseConfiguration::class.java.classLoader.getResourceAsStream("application.properties"))

        val driver = properties.getProperty("database.driver")
        val user = properties.getProperty("database.username")
        val url = properties.getProperty("database.url")
        val password = properties.getProperty("database.password")
        instance = Database.connect(
            url = url,
            driver = driver,
            user = user,
            password = password
        )

        Flyway.configure()
            .dataSource(url, user, password)
            .load().migrate()
    }
}