package com.poisonedyouth

import com.poisonedyouth.configuration.ServerConfiguration
import org.http4k.server.KtorCIO
import org.http4k.server.asServer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private const val SERVER_PORT = 9000
private val logger: Logger = LoggerFactory.getLogger("com.poisonedyouth.Http4kplayground")

fun main() {
    val server = ServerConfiguration.initHttpHandler().asServer(KtorCIO(SERVER_PORT)).start()
    logger.info("Server started on " + server.port())
    server.block()
}
