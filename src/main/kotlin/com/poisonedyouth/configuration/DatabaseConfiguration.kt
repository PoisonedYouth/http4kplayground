package com.poisonedyouth.configuration

import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
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