package de.liruhg.lirucloud.master

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
import de.liruhg.lirucloud.library.router.Router
import de.liruhg.lirucloud.library.thread.ThreadPool
import de.liruhg.lirucloud.master.client.ClientRegistry
import de.liruhg.lirucloud.master.client.protocol.`in`.PacketInClientRequestHandshake
import de.liruhg.lirucloud.master.client.protocol.`in`.PacketInClientRequestServers
import de.liruhg.lirucloud.master.client.protocol.`in`.PacketInClientUpdateLoadStatus
import de.liruhg.lirucloud.master.client.protocol.out.PacketOutClientHandshakeResult
import de.liruhg.lirucloud.master.command.ListGroupsCommand
import de.liruhg.lirucloud.master.command.UpdateGroupFiles
import de.liruhg.lirucloud.master.configuration.CloudKeysCreator
import de.liruhg.lirucloud.master.configuration.DefaultCloudConfiguration
import de.liruhg.lirucloud.master.configuration.DefaultFolderCreator
import de.liruhg.lirucloud.master.configuration.proxy.ProxyDownloadConfiguration
import de.liruhg.lirucloud.master.configuration.proxy.ProxyGroupLoader
import de.liruhg.lirucloud.master.configuration.server.ServerDownloadConfiguration
import de.liruhg.lirucloud.master.configuration.server.ServerGroupLoader
import de.liruhg.lirucloud.master.group.proxy.ProxyGroupHandler
import de.liruhg.lirucloud.master.group.server.ServerGroupHandler
import de.liruhg.lirucloud.master.network.NetworkConnectionRegistry
import de.liruhg.lirucloud.master.network.NetworkServer
import de.liruhg.lirucloud.master.process.protocol.out.PacketOutRequestProxyProcess
import de.liruhg.lirucloud.master.process.protocol.out.PacketOutRequestServerProcess
import de.liruhg.lirucloud.master.process.proxy.handler.ProxyProcessRequestHandler
import de.liruhg.lirucloud.master.process.proxy.registry.ProxyProcessRegistry
import de.liruhg.lirucloud.master.process.server.handler.ServerProcessRequestHandler
import de.liruhg.lirucloud.master.process.server.registry.ServerProcessRegistry
import de.liruhg.lirucloud.master.runtime.RuntimeVars
import de.liruhg.lirucloud.master.task.CheckDanglingConnectionsTask
import de.liruhg.lirucloud.master.web.WebServer
import de.liruhg.lirucloud.master.web.repository.CloudWebUserRepository
import de.liruhg.lirucloud.master.web.route.CreateUserRoute
import de.liruhg.lirucloud.master.web.route.StatusRoute
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
            DefaultCloudConfiguration(KODEIN.direct.instance<RuntimeVars>())
        )

        val runtimeVars = KODEIN.direct.instance<RuntimeVars>()

        KODEIN.direct.instance<DatabaseConnectionFactory>().connectDatabase(runtimeVars.cloudConfiguration.database)

        KODEIN.direct.instance<ConfigurationExecutor>().executeConfigurations()
        KODEIN.direct.instance<CommandManager>().start()
        KODEIN.direct.instance<NetworkServer>().startServer(runtimeVars.cloudConfiguration.masterServerPort)
        KODEIN.direct.instance<WebServer>().startServer(runtimeVars.cloudConfiguration.masterWebPort)

        this.startTasks()
    }

    fun shutdownGracefully() {
        KODEIN.direct.instance<CommandManager>().stop()
        KODEIN.direct.instance<NetworkServer>().shutdownGracefully()
        KODEIN.direct.instance<WebServer>().shutdownGracefully()
        KODEIN.direct.instance<ThreadPool>().shutdown()

        this.logger.info("Thank you for using LiruCloud!")
    }

    private fun initializeDI() {
        KODEIN = DI {
            bindSingleton { this@LiruCloudMaster }

            bindSingleton { ThreadPool() }

            bindSingleton { RuntimeVars() }
            bindSingleton { NettyHelper() }
            bindSingleton { NetworkUtil() }

            bindSingleton { DatabaseConnectionFactory() }

            bindSingleton { SyncFileHandler(instance()) }

            bindSingleton { ServerGroupHandler(instance()) }
            bindSingleton { ProxyGroupHandler(instance()) }

            bindSingleton {
                val configurationExecutor = ConfigurationExecutor()

                configurationExecutor.registerConfiguration(CloudKeysCreator(instance()))

                configurationExecutor.registerConfiguration(ProxyDownloadConfiguration(instance()))
                configurationExecutor.registerConfiguration(ProxyGroupLoader(instance()))

                configurationExecutor.registerConfiguration(ServerDownloadConfiguration(instance()))
                configurationExecutor.registerConfiguration(ServerGroupLoader(instance()))

                configurationExecutor
            }

            bindSingleton { ClientRegistry() }
            bindSingleton { NetworkConnectionRegistry() }

            bindSingleton {
                val commandManager = CommandManager(instance())

                commandManager.registerCommand(UpdateGroupFiles(instance(), instance(), instance()))
                commandManager.registerCommand(CloudExitCommand())
                commandManager.registerCommand(ListGroupsCommand(instance(), instance()))
                commandManager.registerCommand(CloudHelpCommand(commandManager))

                commandManager
            }

            bindSingleton {
                val packetRegistry = PacketRegistry()

                packetRegistry.registerIncomingPacket(
                    PacketId.PACKET_REQUEST_HANDSHAKE,
                    PacketInClientRequestHandshake::class.java
                )
                packetRegistry.registerIncomingPacket(
                    PacketId.PACKET_REQUEST_SERVERS,
                    PacketInClientRequestServers::class.java
                )
                packetRegistry.registerIncomingPacket(
                    PacketId.PACKET_UPDATE_LOAD_STATUS,
                    PacketInClientUpdateLoadStatus::class.java
                )

                packetRegistry.registerOutgoingPacket(
                    PacketId.PACKET_HANDSHAKE_RESULT,
                    PacketOutClientHandshakeResult::class.java
                )
                packetRegistry.registerOutgoingPacket(
                    PacketId.PACKET_REQUEST_PROXY_PROCESS,
                    PacketOutRequestProxyProcess::class.java
                )
                packetRegistry.registerOutgoingPacket(
                    PacketId.PACKET_REQUEST_SERVER_PROCESS,
                    PacketOutRequestServerProcess::class.java
                )

                packetRegistry
            }

            bindSingleton { CloudWebUserRepository(instance()) }

            bindSingleton {
                val router = Router()

                router.registerRoute("/status", StatusRoute(instance(), instance()))
                router.registerRoute("/user/create", CreateUserRoute(instance(), instance()))

                router
            }

            bindSingleton { ProxyProcessRegistry() }
            bindSingleton { ProxyProcessRequestHandler(instance(), instance(), instance(), instance()) }

            bindSingleton { ServerProcessRegistry() }
            bindSingleton { ServerProcessRequestHandler(instance(), instance(), instance(), instance()) }

            bindSingleton { NetworkServer(instance(), instance(), instance()) }
            bindSingleton { WebServer(instance(), instance(), instance()) }
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
        KODEIN.direct.instance<RuntimeVars>().debug = args.contains("--debug")
    }

    private fun startTasks() {
        val timer = Timer("lirucloud-timer")
        timer.scheduleAtFixedRate(
            CheckDanglingConnectionsTask(),
            TimeUnit.SECONDS.toMillis(5),
            TimeUnit.SECONDS.toMillis(5)
        )
    }
}