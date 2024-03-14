package de.liruhg.lirucloud.master.configuration

import de.liruhg.lirucloud.library.configuration.Configuration
import de.liruhg.lirucloud.library.configuration.model.CacheConnection
import de.liruhg.lirucloud.library.configuration.model.DatabaseConnection
import de.liruhg.lirucloud.library.directory.Directories
import de.liruhg.lirucloud.library.util.FileUtils
import de.liruhg.lirucloud.master.configuration.model.CloudConfiguration
import de.liruhg.lirucloud.master.configuration.model.ValidClient
import de.liruhg.lirucloud.master.store.Store
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import kotlin.system.exitProcess

class DefaultCloudConfiguration(
    private val store: Store
) : Configuration {

    private val logger: Logger = LoggerFactory.getLogger(DefaultCloudConfiguration::class.java)

    override fun execute() {
        val cloudConfigurationFile = File(Directories.MASTER_CONFIGURATION, "config.json")

        if (!cloudConfigurationFile.exists()) {
            this.logger.info("Cloud configuration file does not exist. Creating default configuration file.")

            val cloudConfiguration = CloudConfiguration(
                serverPort = 8080,
                defaultProxyGroupName = "Proxy",
                defaultLobbyGroupName = "Lobby",
                database = DatabaseConnection(
                    connectionUrl = "mongodb://localhost:27017",
                    databaseName = "lirucloud",
                    bucketName = "templates",
                    collections = mutableMapOf(
                        "filesCollection" to "filesCollection",
                        "proxyGroupsCollection" to "proxyGroupsCollection",
                        "serverGroupsCollection" to "serverGroupsCollection"
                    )
                ),
                cache = CacheConnection(
                    host = "localhost",
                    port = 6379,
                    user = "",
                    password = "",
                    database = 0
                ),
                validClients = setOf(
                    ValidClient("Client-1", setOf("localhost", "127.0.0.1", "0:0:0:0:0:0:0:1"))
                )
            )

            FileUtils.writeClassToJsonFile(cloudConfigurationFile, cloudConfiguration)
            exitProcess(0)
        }

        val cloudConfiguration = FileUtils.readClassFromJson(
            cloudConfigurationFile,
            CloudConfiguration::class.java
        )

        this.store.cloudConfiguration = cloudConfiguration
    }
}