package de.liruhg.lirucloud.api.global

import de.liruhg.lirucloud.api.global.runtime.RuntimeVars
import de.liruhg.lirucloud.library.database.DatabaseConnectionFactory
import de.liruhg.lirucloud.library.database.handler.SyncFileHandler
import de.liruhg.lirucloud.library.network.helper.NettyHelper
import de.liruhg.lirucloud.library.network.util.NetworkUtil
import de.liruhg.lirucloud.library.thread.ThreadPool
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

class GlobalPluginDi {

    companion object {
        lateinit var KODEIN: DI
    }

    fun initializeDi() {
        KODEIN = DI {
            bindSingleton { ThreadPool() }

            bindSingleton { RuntimeVars() }
            bindSingleton { NettyHelper() }
            bindSingleton { NetworkUtil() }

            bindSingleton { DatabaseConnectionFactory() }

            bindSingleton { SyncFileHandler(instance()) }
        }
    }
}