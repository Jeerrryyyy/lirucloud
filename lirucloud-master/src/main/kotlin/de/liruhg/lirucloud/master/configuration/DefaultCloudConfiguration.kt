package de.liruhg.lirucloud.master.configuration

import de.liruhg.lirucloud.library.configuration.Configuration
import de.liruhg.lirucloud.library.configuration.model.DatabaseConnectionModel
import de.liruhg.lirucloud.library.directory.Directories
import de.liruhg.lirucloud.library.util.FileUtils
import de.liruhg.lirucloud.master.configuration.model.CloudConfigurationModel
import de.liruhg.lirucloud.master.configuration.model.ValidClientModel
import de.liruhg.lirucloud.master.runtime.RuntimeVars
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

class DefaultCloudConfiguration(
    private val runtimeVars: RuntimeVars
) : Configuration {

    private val logger: Logger = LoggerFactory.getLogger(DefaultCloudConfiguration::class.java)

    override fun execute() {
        val cloudConfigurationFile = File(Directories.MASTER_CONFIGURATION, "config.json")

        if (!cloudConfigurationFile.exists()) {
            this.logger.info("Cloud configuration file does not exist. Creating default configuration file.")

            val cloudConfigurationModel = CloudConfigurationModel(
                masterServerPort = 8080,
                masterWebPort = 8081,
                database = DatabaseConnectionModel(
                    connectionUrl = "mongodb://localhost:27017",
                    databaseName = "lirucloud",
                    bucketName = "templates",
                    collections = mutableMapOf(
                        "filesCollection" to "filesCollection",
                        "webUserCollection" to "webUserCollection",
                        "proxyGroupsCollection" to "proxyGroupsCollection",
                        "serverGroupsCollection" to "serverGroupsCollection"
                    )
                ),
                validClients = setOf(
                    ValidClientModel("Client-1", setOf("localhost", "127.0.0.1", "0:0:0:0:0:0:0:1"))
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