package de.liruhg.lirucloud.client

import de.liruhg.lirucloud.client.configuration.CloudKeysCreator
import de.liruhg.lirucloud.client.configuration.DefaultCloudConfiguration
import de.liruhg.lirucloud.client.configuration.DefaultFolderCreator
import de.liruhg.lirucloud.client.network.NetworkClient
import de.liruhg.lirucloud.client.network.protocol.`in`.PacketInClientHandshakeResult
import de.liruhg.lirucloud.client.network.protocol.out.PacketOutClientRequestHandshake
import de.liruhg.lirucloud.client.store.Store
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
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.direct
import org.kodein.di.instance
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.system.exitProcess

class LiruCloudClient {

    private val logger: Logger = LoggerFactory.getLogger(LiruCloudClient::class.java)

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

        KODEIN.direct.instance<ConfigurationExecutor>().executeConfigurations()
        KODEIN.direct.instance<CommandManager>().start()
        KODEIN.direct.instance<NetworkClient>()
            .startClient(store.cloudConfiguration.masterAddress, store.cloudConfiguration.masterPort)

        this.startTasks()
    }

    fun shutdownGracefully() {
        this.logger.info("Shutting down LiruCloud gracefully... Please be patient.")

        KODEIN.direct.instance<CommandManager>().stop()
        KODEIN.direct.instance<NetworkClient>().shutdownGracefully()
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

                packetRegistry.registerIncomingPacket(
                    PacketId.PACKET_CLIENT_HANDSHAKE_RESULT,
                    PacketInClientHandshakeResult::class.java
                )
                packetRegistry.registerOutgoingPacket(
                    PacketId.PACKET_CLIENT_REQUEST_HANDSHAKE,
                    PacketOutClientRequestHandshake::class.java
                )

                packetRegistry
            }

            bindSingleton { NetworkClient(instance(), instance(), instance(), instance()) }
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