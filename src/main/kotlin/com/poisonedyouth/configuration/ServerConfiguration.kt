package com.poisonedyouth.configuration

import org.http4k.core.HttpHandler
import org.http4k.core.then
import org.http4k.filter.DebuggingFilters.PrintRequest
import org.koin.core.module.Module
import org.koin.java.KoinJavaComponent.get

object ServerConfiguration {
    fun initHttpHandlerWith(module: Module): HttpHandler {
        ComponentConfiguration.initKoin(module)
        DatabaseConfiguration(get(DatabaseProperties::class.java)).initialize()
        return PrintRequest().then(app)
    }
}
