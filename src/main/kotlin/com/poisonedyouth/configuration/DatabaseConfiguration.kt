package com.poisonedyouth.configuration

import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.slf4j.LoggerFactory
import java.util.Properties

interface DatabaseProperties {
    val driver: String
    val url: String
    val username: String
    val password: String
}

class ApplicationPropertiesDatabaseProperties : DatabaseProperties {
    private val properties =
        Properties().apply {
            this.load(
                DatabaseConfiguration::class.java.classLoader.getResourceAsStream("application.properties"),
            )
        }
    override val driver: String = properties.getProperty("database.driver")
    override val username: String = properties.getProperty("database.username")
    override val url: String = properties.getProperty("database.url")
    override val password: String = properties.getProperty("database.password")
}

class DatabaseConfiguration(
    private val databaseProperties: DatabaseProperties,
) {
    private val logger = LoggerFactory.getLogger(DatabaseConfiguration::class.java)

    fun initialize() {
        logger.info("Connect to database '{}'", databaseProperties.url)
        Database.connect(
            url = databaseProperties.url,
            driver = databaseProperties.driver,
            user = databaseProperties.username,
            password = databaseProperties.password,
        )

        logger.info("Check for database migrations.")
        Flyway.configure()
            .dataSource(databaseProperties.url, databaseProperties.username, databaseProperties.password)
            .load().migrate()
    }
}
