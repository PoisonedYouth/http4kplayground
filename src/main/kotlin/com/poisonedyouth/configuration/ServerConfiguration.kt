package com.poisonedyouth.configuration

import org.http4k.core.HttpHandler
import org.http4k.core.then
import org.http4k.filter.DebuggingFilters.PrintRequest
import org.koin.core.module.Module
import org.koin.java.KoinJavaComponent.get
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object ServerConfiguration {
    fun initHttpHandlerWith(module: Module): HttpHandler {
        ComponentConfiguration.initKoin(module)
        DatabaseConfiguration(get(DatabaseProperties::class.java)).initialize()
        return PrintRequest().then(app)
    }
}
