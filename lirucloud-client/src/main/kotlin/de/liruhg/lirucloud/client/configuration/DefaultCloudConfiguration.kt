package de.liruhg.lirucloud.client.configuration

import de.liruhg.lirucloud.client.configuration.model.CloudConfigurationModel
import de.liruhg.lirucloud.client.runtime.RuntimeVars
import de.liruhg.lirucloud.library.configuration.Configuration
import de.liruhg.lirucloud.library.configuration.model.DatabaseConnectionModel
import de.liruhg.lirucloud.library.directory.Directories
import de.liruhg.lirucloud.library.util.FileUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.util.*

class DefaultCloudConfiguration(
    private val runtimeVars: RuntimeVars
) : Configuration {

    private val logger: Logger = LoggerFactory.getLogger(DefaultCloudConfiguration::class.java)

    override fun execute() {
        val cloudConfigurationFile = File(Directories.CLIENT_CONFIGURATION, "config.json")

        if (!cloudConfigurationFile.exists()) {
            this.logger.info("Cloud configuration file does not exist. Creating default configuration file.")

            val cloudConfigurationModel = CloudConfigurationModel(
                masterAddress = "localhost",
                masterPort = 8080,
                webPort = 8081,
                name = "Client",
                delimiter = "-",
                suffix = "1",
                memory = 10240,
                uuid = UUID.randomUUID().toString(),
                responsibleGroups = emptySet(),
                database = DatabaseConnectionModel(
                    connectionUrl = "mongodb://localhost:27017",
                    databaseName = "lirucloud",
                    bucketName = "templates",
                    collections = mutableMapOf(
                        "filesCollection" to "filesCollection"
                    )
                )
            )

            FileUtils.writeClassToJsonFile(cloudConfigurationFile, cloudConfigurationModel)
        }

        val cloudConfigurationModel = FileUtils.readClassFromJson(
            cloudConfigurationFile,
            CloudConfigurationModel::class.java
        )

        this.runtimeVars.cloudConfiguration = cloudConfigurationModel
    }
}