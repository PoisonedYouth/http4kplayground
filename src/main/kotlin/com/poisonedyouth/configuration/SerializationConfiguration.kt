package com.poisonedyouth.configuration

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

val objectMapper =
    jacksonObjectMapper()
        .registerModules(KotlinModule.Builder().build())
        .registerModule(JavaTimeModule())
