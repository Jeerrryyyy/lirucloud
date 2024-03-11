package de.liruhg.lirucloud.master

import de.liruhg.lirucloud.library.cache.CacheConnectionFactory
import de.liruhg.lirucloud.library.command.CommandManager
import de.liruhg.lirucloud.library.command.commands.CloudExitCommand
import de.liruhg.lirucloud.library.command.commands.CloudHelpCommand
import de.liruhg.lirucloud.library.configuration.ConfigurationExecutor
import de.liruhg.lirucloud.library.database.DatabaseConnectionFactory
import de.liruhg.lirucloud.library.database.handler.SyncFileHandler
import de.liruhg.lirucloud.library.network.helper.NettyHelper
import de.liruhg.lirucloud.library.network.protocol.PacketId
import de.liruhg.lirucloud.library.network.protocol.PacketRegistry
import de.liruhg.lirucloud.library.network.util.NetworkUtil
import de.liruhg.lirucloud.library.thread.ThreadPool
import de.liruhg.lirucloud.library.util.PortUtil
import de.liruhg.lirucloud.master.client.ClientRegistry
import de.liruhg.lirucloud.master.client.protocol.`in`.PacketInClientRequestHandshake
import de.liruhg.lirucloud.master.client.protocol.out.PacketOutClientHandshakeResult
import de.liruhg.lirucloud.master.configuration.CloudKeysCreator
import de.liruhg.lirucloud.master.configuration.DefaultCloudConfiguration
import de.liruhg.lirucloud.master.configuration.DefaultFolderCreator
import de.liruhg.lirucloud.master.configuration.proxy.ProxyGroupLoader
import de.liruhg.lirucloud.master.configuration.proxy.ProxySoftwareDownloadConfiguration
import de.liruhg.lirucloud.master.configuration.server.ServerGroupLoader
import de.liruhg.lirucloud.master.configuration.server.ServerSoftwareDownloadConfiguration
import de.liruhg.lirucloud.master.group.proxy.ProxyGroupHandler
import de.liruhg.lirucloud.master.group.server.ServerGroupHandler
import de.liruhg.lirucloud.master.network.NetworkConnectionRegistry
import de.liruhg.lirucloud.master.network.NetworkServer
import de.liruhg.lirucloud.master.process.ProcessRegistry
import de.liruhg.lirucloud.master.process.protocol.`in`.PacketInRequestProcessResult
import de.liruhg.lirucloud.master.process.protocol.out.PacketOutRequestProcess
import de.liruhg.lirucloud.master.process.proxy.ProxyProcessRequestHandler
import de.liruhg.lirucloud.master.process.server.ServerProcessRequestHandler
import de.liruhg.lirucloud.master.store.Store
import de.liruhg.lirucloud.master.task.CheckDanglingConnectionsTask
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.direct
import org.kodein.di.instance
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.TimeUnit
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

        KODEIN.direct.instance<DatabaseConnectionFactory>().connectDatabase(store.cloudConfiguration.database)
        KODEIN.direct.instance<CacheConnectionFactory>().connectCache(store.cloudConfiguration.cache)

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

            bindSingleton { ServerGroupHandler(instance(), instance(), instance()) }
            bindSingleton { ProxyGroupHandler(instance(), instance(), instance()) }

            bindSingleton {
                val configurationExecutor = ConfigurationExecutor()

                configurationExecutor.registerConfiguration(CloudKeysCreator(instance()))

                configurationExecutor.registerConfiguration(ProxySoftwareDownloadConfiguration(instance()))
                configurationExecutor.registerConfiguration(ProxyGroupLoader(instance()))

                configurationExecutor.registerConfiguration(ServerSoftwareDownloadConfiguration(instance()))
                configurationExecutor.registerConfiguration(ServerGroupLoader(instance()))

                configurationExecutor
            }

            bindSingleton { ClientRegistry() }
            bindSingleton { NetworkConnectionRegistry() }

            bindSingleton {
                val commandManager = CommandManager(instance())

                commandManager.registerCommand(CloudExitCommand())
                commandManager.registerCommand(CloudHelpCommand(commandManager))

                commandManager
            }

            bindSingleton {
                val packetRegistry = PacketRegistry()

                packetRegistry.registerIncomingPacket(
                    PacketId.PACKET_CLIENT_REQUEST_HANDSHAKE,
                    PacketInClientRequestHandshake::class.java
                )
                packetRegistry.registerIncomingPacket(
                    PacketId.PACKET_REQUEST_PROCESS_RESULT,
                    PacketInRequestProcessResult::class.java
                )

                packetRegistry.registerOutgoingPacket(
                    PacketId.PACKET_CLIENT_HANDSHAKE_RESULT,
                    PacketOutClientHandshakeResult::class.java
                )
                packetRegistry.registerOutgoingPacket(
                    PacketId.PACKET_REQUEST_PROCESS,
                    PacketOutRequestProcess::class.java
                )

                packetRegistry
            }

            bindSingleton { ProcessRegistry(instance()) }

            bindSingleton { ProxyProcessRequestHandler(instance(), instance(), instance(), instance()) }
            bindSingleton { ServerProcessRequestHandler(instance(), instance(), instance(), instance()) }

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
        val timer = Timer("lirucloud-timer")
        timer.scheduleAtFixedRate(
            CheckDanglingConnectionsTask(),
            TimeUnit.SECONDS.toMillis(15),
            TimeUnit.SECONDS.toMillis(15)
        )
    }
}