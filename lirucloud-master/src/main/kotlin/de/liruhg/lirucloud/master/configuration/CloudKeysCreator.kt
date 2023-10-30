package de.liruhg.lirucloud.master.configuration

import de.liruhg.lirucloud.library.configuration.Configuration
import de.liruhg.lirucloud.library.directory.Directories
import de.liruhg.lirucloud.library.util.FileUtils
import de.liruhg.lirucloud.master.runtime.RuntimeVars
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.util.*

class CloudKeysCreator(
    private val runtimeVars: RuntimeVars
) : Configuration {

    private val logger: Logger = LoggerFactory.getLogger(CloudKeysCreator::class.java)

    override fun execute() {
        val clientKeyFile = File(Directories.MASTER_KEYS, "client.key")

        if (!clientKeyFile.exists()) {
            this.logger.info("Client key file does not exist. Creating default client key file")

            val clientKey = this.generateKey()
            FileUtils.writeStringToFile(clientKeyFile, clientKey)
        }

        val clientKey = FileUtils.readStringFromFile(clientKeyFile)
        this.runtimeVars.clientKey = clientKey

        val webKeyFile = File(Directories.MASTER_KEYS, "web.key")

        if (!webKeyFile.exists()) {
            this.logger.info("Web key file does not exist. Creating default web key file")

            val webKey = this.generateKey()
            FileUtils.writeStringToFile(webKeyFile, webKey)
        }

        val webKey = FileUtils.readStringFromFile(webKeyFile)
        this.runtimeVars.webKey = webKey
    }

    private fun generateKey(): String {
        val keyBuilder = StringBuilder()

        (0..10).forEach { _ ->
            keyBuilder.append(UUID.randomUUID().toString().replace("-", ""))
        }

        return keyBuilder.toString()
    }
}