package com.poisonedyouth

import com.poisonedyouth.configuration.ComponentConfiguration
import com.poisonedyouth.configuration.DatabaseConfiguration
import com.poisonedyouth.configuration.app
import org.http4k.core.HttpHandler
import org.http4k.core.then
import org.http4k.filter.DebuggingFilters.PrintRequest
import org.http4k.server.KtorCIO
import org.http4k.server.asServer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger("com.poisonedyouth.http4kplayground")

fun main() {

    logger.info("Starting http4k playground...")
    logger.info("Starting Koin...")
    ComponentConfiguration.initKoin()
    logger.info("Initialize database configuration...")
    DatabaseConfiguration.initialize()
    val printingApp: HttpHandler = PrintRequest().then(app)

    val server = printingApp.asServer(KtorCIO(9000)).start()

    logger.info("Server started on " + server.port())

    server.block()
}
