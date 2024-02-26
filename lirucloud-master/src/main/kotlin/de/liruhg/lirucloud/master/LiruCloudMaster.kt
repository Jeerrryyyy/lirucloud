package de.liruhg.lirucloud.master

import de.liruhg.lirucloud.library.cache.CacheConnectionFactory
import de.liruhg.lirucloud.library.command.CommandManager
import de.liruhg.lirucloud.library.command.commands.CloudExitCommand
import de.liruhg.lirucloud.library.command.commands.CloudHelpCommand
import de.liruhg.lirucloud.library.configuration.ConfigurationExecutor
import de.liruhg.lirucloud.library.database.DatabaseConnectionFactory
import de.liruhg.lirucloud.library.database.handler.SyncFileHandler
import de.liruhg.lirucloud.library.network.helper.NettyHelper
import de.liruhg.lirucloud.library.network.protocol.PacketRegistry
import de.liruhg.lirucloud.library.network.util.NetworkUtil
import de.liruhg.lirucloud.library.thread.ThreadPool
import de.liruhg.lirucloud.library.util.PortUtil
import de.liruhg.lirucloud.master.client.protocol.`in`.PacketInHandshake
import de.liruhg.lirucloud.master.client.protocol.out.PacketOutHandshakeResult
import de.liruhg.lirucloud.master.configuration.CloudKeysCreator
import de.liruhg.lirucloud.master.configuration.DefaultCloudConfiguration
import de.liruhg.lirucloud.master.configuration.DefaultFolderCreator
import de.liruhg.lirucloud.master.network.NetworkServer
import de.liruhg.lirucloud.master.store.Store
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.direct
import org.kodein.di.instance
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.system.exitProcess

class LiruCloudMaster {

    private val logger: Logger = LoggerFactory.getLogger(LiruCloudMaster::class.java)

    companion object {
        lateinit var KODEIN: DI
    }

    fun start(args: Array<String>) {
        this.logger.info("Welcome to LiruCloud! Please wait while we initialize components and start systems...")

        this.initializeDI()
        this.checkForRoot(args)
        this.checkForDebug(args)

        KODEIN.direct.instance<ConfigurationExecutor>().executeConfigurations(
            DefaultFolderCreator(),
            DefaultCloudConfiguration(KODEIN.direct.instance<Store>())
        )

        val store = KODEIN.direct.instance<Store>()

        //KODEIN.direct.instance<DatabaseConnectionFactory>().connectDatabase(store.cloudConfiguration.database)
        //KODEIN.direct.instance<CacheConnectionFactory>().connectCache(store.cloudConfiguration.cache)

        KODEIN.direct.instance<ConfigurationExecutor>().executeConfigurations()
        KODEIN.direct.instance<CommandManager>().start()
        KODEIN.direct.instance<NetworkServer>().startServer(store.cloudConfiguration.serverPort)

        this.startTasks()
    }

    fun shutdownGracefully() {
        KODEIN.direct.instance<CommandManager>().stop()
        KODEIN.direct.instance<NetworkServer>().shutdownGracefully()
        KODEIN.direct.instance<ThreadPool>().shutdown()

        this.logger.info("Thank you for using LiruCloud!")
    }

    private fun initializeDI() {
        KODEIN = DI {
            bindSingleton { ThreadPool() }

            bindSingleton { Store() }
            bindSingleton { NettyHelper() }
            bindSingleton { NetworkUtil() }

            bindSingleton { DatabaseConnectionFactory() }
            bindSingleton { CacheConnectionFactory() }

            bindSingleton { PortUtil(instance()) }

            bindSingleton { SyncFileHandler(instance()) }

            bindSingleton {
                val configurationExecutor = ConfigurationExecutor()

                configurationExecutor.registerConfiguration(CloudKeysCreator(instance()))

                configurationExecutor
            }

            bindSingleton {
                val commandManager = CommandManager(instance())

                commandManager.registerCommand(CloudExitCommand())
                commandManager.registerCommand(CloudHelpCommand(commandManager))

                commandManager
            }

            bindSingleton {
                val packetRegistry = PacketRegistry()

                packetRegistry.registerIncomingPacket(1, PacketInHandshake::class.java)
                packetRegistry.registerOutgoingPacket(2, PacketOutHandshakeResult::class.java)

                packetRegistry
            }

            bindSingleton { NetworkServer(instance(), instance(), instance(), instance()) }
        }
    }

    private fun checkForRoot(args: Array<String>) {
        if (System.getProperty("user.name") != "root" || args.contains("--enable-root")) {
            return
        }

        this.logger.warn("Please consider not to use the \"root\" user for security reasons!")
        this.logger.warn("If you want to use it anyway, at your own risk, add \"--enable-root\" to the start arguments.")
        exitProcess(1)
    }

    private fun checkForDebug(args: Array<String>) {
        KODEIN.direct.instance<Store>().debug = args.contains("--debug")
    }

    private fun startTasks() {
    }
}