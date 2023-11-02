package de.liruhg.lirucloud.api.proxy

import de.liruhg.lirucloud.api.global.GlobalPluginDi
import de.liruhg.lirucloud.api.global.configuration.DefaultFolderCreator
import de.liruhg.lirucloud.api.global.configuration.DefaultPluginConfiguration
import de.liruhg.lirucloud.api.global.configuration.KeyReader
import de.liruhg.lirucloud.api.global.network.NetworkClient
import de.liruhg.lirucloud.api.global.network.protocol.`in`.PacketInProcessHandshakeResult
import de.liruhg.lirucloud.api.global.network.protocol.out.PacketOutProcessRequestHandshake
import de.liruhg.lirucloud.api.global.runtime.RuntimeVars
import de.liruhg.lirucloud.library.configuration.ConfigurationExecutor
import de.liruhg.lirucloud.library.database.DatabaseConnectionFactory
import de.liruhg.lirucloud.library.directory.Directories
import de.liruhg.lirucloud.library.network.protocol.PacketId
import de.liruhg.lirucloud.library.network.protocol.PacketRegistry
import de.liruhg.lirucloud.library.thread.ThreadPool
import net.md_5.bungee.api.plugin.Plugin
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.direct
import org.kodein.di.instance
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Path

@Suppress("unused") // This class is used by BungeeCord.
class LiruCloudProxyApi : Plugin() {

    private val logger: Logger = LoggerFactory.getLogger(LiruCloudProxyApi::class.java)

    companion object {
        lateinit var KODEIN: DI
    }

    override fun onEnable() {
        this.logger.info("Welcome to LiruCloud! Please wait while we initialize components and start systems...")

        GlobalPluginDi().initializeDi()
        this.initializeDI()

        KODEIN.direct.instance<ConfigurationExecutor>().executeConfigurations(
            DefaultFolderCreator(
                setOf(
                    Path.of(Directories.PROXY_PLUGINS),
                    Path.of(Directories.PROXY_PLUGINS_API),
                )
            ),
            DefaultPluginConfiguration(
                File(Directories.PROXY_PLUGINS_API, "config.json"),
                KODEIN.direct.instance<RuntimeVars>()
            )
        )

        val runtimeVars = KODEIN.direct.instance<RuntimeVars>()

        KODEIN.direct.instance<DatabaseConnectionFactory>()
            .connectDatabase(runtimeVars.pluginConfiguration.database)
        KODEIN.direct.instance<ConfigurationExecutor>().executeConfigurations()
        KODEIN.direct.instance<NetworkClient>().startClient(
            runtimeVars.pluginConfiguration.masterAddress,
            runtimeVars.pluginConfiguration.masterPort
        )
    }

    override fun onDisable() {
        KODEIN.direct.instance<NetworkClient>().shutdownGracefully()
        KODEIN.direct.instance<ThreadPool>().shutdown()

        this.logger.info("Thank you for using LiruCloud!")
    }

    private fun initializeDI() {
        KODEIN = DI {
            extend(GlobalPluginDi.KODEIN)

            bindSingleton {
                val configurationExecutor = ConfigurationExecutor()

                configurationExecutor.registerConfiguration(
                    KeyReader(
                        File("${Directories.PROXY_PLUGINS_API}/client.key"),
                        instance()
                    )
                )

                configurationExecutor
            }

            bindSingleton {
                val packetRegistry = PacketRegistry()

                packetRegistry.registerOutgoingPacket(
                    PacketId.PACKET_PROCESS_REQUEST_HANDSHAKE,
                    PacketOutProcessRequestHandshake::class.java
                )

                packetRegistry.registerIncomingPacket(
                    PacketId.PACKET_PROCESS_HANDSHAKE_RESULT,
                    PacketInProcessHandshakeResult::class.java
                )

                packetRegistry
            }

            bindSingleton { NetworkClient(instance(), instance(), instance()) }
        }
    }
}