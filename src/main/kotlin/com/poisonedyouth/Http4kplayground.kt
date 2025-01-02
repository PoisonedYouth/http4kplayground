package com.poisonedyouth

import com.poisonedyouth.configuration.ServerConfiguration
import com.poisonedyouth.configuration.defaultModule
import org.http4k.server.KtorCIO
import org.http4k.server.asServer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private const val SERVER_PORT = 9000
private val logger: Logger = LoggerFactory.getLogger("com.poisonedyouth.Http4kplayground")

fun main() {
    logger.info("Starting Http4kPlayground...")
    val server =
        ServerConfiguration
            .initHttpHandlerWith(defaultModule)
            .asServer(KtorCIO(SERVER_PORT))
            .start()
    logger.info("Server started on " + server.port())
    server.block()
}
