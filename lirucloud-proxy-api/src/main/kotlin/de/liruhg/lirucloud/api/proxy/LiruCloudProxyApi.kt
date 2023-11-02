package de.liruhg.lirucloud.api.proxy

import de.liruhg.lirucloud.api.proxy.configuration.DefaultFolderCreator
import de.liruhg.lirucloud.api.proxy.configuration.DefaultProxyConfiguration
import de.liruhg.lirucloud.api.proxy.configuration.KeyReader
import de.liruhg.lirucloud.api.proxy.network.NetworkClient
import de.liruhg.lirucloud.api.proxy.network.protocol.`in`.PacketInProxyHandshakeResult
import de.liruhg.lirucloud.api.proxy.network.protocol.out.PacketOutProxyProcessRequestHandshake
import de.liruhg.lirucloud.api.proxy.runtime.RuntimeVars
import de.liruhg.lirucloud.library.configuration.ConfigurationExecutor
import de.liruhg.lirucloud.library.database.DatabaseConnectionFactory
import de.liruhg.lirucloud.library.database.handler.SyncFileHandler
import de.liruhg.lirucloud.library.network.helper.NettyHelper
import de.liruhg.lirucloud.library.network.protocol.PacketId
import de.liruhg.lirucloud.library.network.protocol.PacketRegistry
import de.liruhg.lirucloud.library.network.util.NetworkUtil
import de.liruhg.lirucloud.library.thread.ThreadPool
import net.md_5.bungee.api.plugin.Plugin
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.direct
import org.kodein.di.instance
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Suppress("unused") // This class is used by BungeeCord.
class LiruCloudProxyApi : Plugin() {

    private val logger: Logger = LoggerFactory.getLogger(LiruCloudProxyApi::class.java)

    companion object {
        lateinit var KODEIN: DI
    }

    override fun onEnable() {
        this.logger.info("Welcome to LiruCloud! Please wait while we initialize components and start systems...")

        this.initializeDI()

        KODEIN.direct.instance<ConfigurationExecutor>().executeConfigurations(
            DefaultFolderCreator(),
            DefaultProxyConfiguration(KODEIN.direct.instance<RuntimeVars>())
        )

        val runtimeVars = KODEIN.direct.instance<RuntimeVars>()

        KODEIN.direct.instance<DatabaseConnectionFactory>()
            .connectDatabase(runtimeVars.proxyPluginConfigurationModel.database)
        KODEIN.direct.instance<ConfigurationExecutor>().executeConfigurations()
        KODEIN.direct.instance<NetworkClient>().startClient(
            runtimeVars.proxyPluginConfigurationModel.masterAddress,
            runtimeVars.proxyPluginConfigurationModel.masterPort
        )
    }

    override fun onDisable() {
        KODEIN.direct.instance<NetworkClient>().shutdownGracefully()
        KODEIN.direct.instance<ThreadPool>().shutdown()

        this.logger.info("Thank you for using LiruCloud!")
    }

    private fun initializeDI() {
        KODEIN = DI {
            bindSingleton { this@LiruCloudProxyApi }

            bindSingleton { ThreadPool() }

            bindSingleton { RuntimeVars() }
            bindSingleton { NettyHelper() }
            bindSingleton { NetworkUtil() }

            bindSingleton { DatabaseConnectionFactory() }

            bindSingleton { SyncFileHandler(instance()) }

            bindSingleton {
                val configurationExecutor = ConfigurationExecutor()

                configurationExecutor.registerConfiguration(KeyReader(instance()))

                configurationExecutor
            }

            bindSingleton {
                val packetRegistry = PacketRegistry()

                packetRegistry.registerOutgoingPacket(
                    PacketId.PACKET_PROXY_REQUEST_HANDSHAKE,
                    PacketOutProxyProcessRequestHandshake::class.java
                )

                packetRegistry.registerIncomingPacket(
                    PacketId.PACKET_PROXY_HANDSHAKE_RESULT,
                    PacketInProxyHandshakeResult::class.java
                )

                packetRegistry
            }

            bindSingleton { NetworkClient(instance(), instance(), instance()) }
        }
    }
}