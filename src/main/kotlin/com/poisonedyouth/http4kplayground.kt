package com.poisonedyouth

import com.poisonedyouth.chat.addChatHandler
import com.poisonedyouth.chat.addMessageToChatHandler
import com.poisonedyouth.chat.addUserToChatHandler
import com.poisonedyouth.chat.getAllChatsHandler
import com.poisonedyouth.chat.getChatHandler
import com.poisonedyouth.configuration.ComponentConfiguration
import com.poisonedyouth.configuration.DatabaseConfiguration
import com.poisonedyouth.configuration.app
import com.poisonedyouth.user.addUserHandler
import org.http4k.core.HttpHandler
import org.http4k.core.Method.*
import org.http4k.core.then
import org.http4k.filter.DebuggingFilters.PrintRequest
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.KtorCIO
import org.http4k.server.asServer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger("com.poisonedyouth.http4kplayground")

fun main() {

    DatabaseConfiguration.initialize()
    val printingApp: HttpHandler = PrintRequest().then(app)

    val server = printingApp.asServer(KtorCIO(9000)).start()

    logger.info("Server started on " + server.port())

    server.block()
}
