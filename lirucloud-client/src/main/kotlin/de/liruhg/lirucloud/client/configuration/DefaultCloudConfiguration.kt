package de.liruhg.lirucloud.client.configuration

import de.liruhg.lirucloud.client.configuration.model.CloudConfiguration
import de.liruhg.lirucloud.client.store.Store
import de.liruhg.lirucloud.library.configuration.Configuration
import de.liruhg.lirucloud.library.configuration.model.CacheConnection
import de.liruhg.lirucloud.library.configuration.model.DatabaseConnection
import de.liruhg.lirucloud.library.directory.Directories
import de.liruhg.lirucloud.library.util.FileUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.util.*
import kotlin.system.exitProcess

class DefaultCloudConfiguration(
    private val store: Store
) : Configuration {

    private val logger: Logger = LoggerFactory.getLogger(DefaultCloudConfiguration::class.java)

    override fun execute() {
        val cloudConfigurationFile = File(Directories.CLIENT_CONFIGURATION, "config.json")

        if (!cloudConfigurationFile.exists()) {
            this.logger.info("Cloud configuration file does not exist. Creating default configuration file.")

            val cloudConfiguration = CloudConfiguration(
                masterAddress = "localhost",
                masterPort = 8080,
                name = "Client",
                delimiter = "-",
                suffix = "1",
                memory = 10240,
                uuid = UUID.randomUUID().toString(),
                responsibleGroups = emptySet(),
                database = DatabaseConnection(
                    connectionUrl = "mongodb://localhost:27017",
                    databaseName = "lirucloud",
                    bucketName = "templates",
                    collections = mutableMapOf(
                        "filesCollection" to "filesCollection"
                    )
                ),
                cache = CacheConnection(
                    host = "localhost",
                    port = 6379,
                    user = "",
                    password = "",
                    database = 0
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