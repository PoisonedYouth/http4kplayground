package com.poisonedyouth

import com.poisonedyouth.configuration.ServerConfiguration
import org.http4k.server.KtorCIO
import org.http4k.server.asServer

fun startTestApplication(block: (Int) -> Unit) {
    val config = KtorCIO(9999)
    val server = ServerConfiguration.initHttpHandler().asServer(config).start()
    block(config.port)

    server.stop()
}
