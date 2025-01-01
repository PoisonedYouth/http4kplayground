package com.poisonedyouth.configuration

import org.http4k.core.HttpHandler
import org.http4k.core.then
import org.http4k.filter.DebuggingFilters.PrintRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object ServerConfiguration {
    private val logger: Logger = LoggerFactory.getLogger(ServerConfiguration::class.java)

    fun initHttpHandler(): HttpHandler {
        logger.info("Starting http4k playground...")
        logger.info("Starting Koin...")
        ComponentConfiguration.initKoin()
        logger.info("Initialize database configuration...")
        DatabaseConfiguration.initialize()
        return PrintRequest().then(app)
    }
}
