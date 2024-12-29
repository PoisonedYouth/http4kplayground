package com.poisonedyouth.chat

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.architecture.KoArchitectureCreator.assertArchitecture
import com.lemonappdev.konsist.api.architecture.Layer
import org.junit.jupiter.api.Test

class KonsistTest {
    @Test
    fun `verify hexagonal architecture`() {
        Konsist.scopeFromProduction()
            .assertArchitecture {
                val domain = Layer("domain", "..domain..")
                val application = Layer("application", "..application..")
                val infrastructure = Layer("infrastructure", "..infrastructure..")

                domain.dependsOnNothing()
                application.dependsOn(domain)
                infrastructure.dependsOn(domain, application)
            }
    }
}
