package de.liruhg.lirucloud.api.global

import org.kodein.di.DI

class GlobalPluginDi {

    companion object {
        lateinit var KODEIN: DI
    }

    fun initializeDi() {
        KODEIN = DI {

        }
    }
}