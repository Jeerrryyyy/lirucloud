package de.liruhg.lirucloud.client.configuration

import de.liruhg.lirucloud.client.store.Store
import de.liruhg.lirucloud.library.configuration.Configuration
import de.liruhg.lirucloud.library.directory.Directories
import de.liruhg.lirucloud.library.util.FileUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import kotlin.system.exitProcess

class CloudKeysCreator(
    private val store: Store
) : Configuration {

    private val logger: Logger = LoggerFactory.getLogger(CloudKeysCreator::class.java)

    override fun execute() {
        val clientKeyFile = File(Directories.CLIENT_KEYS, "client.key")

        if (!clientKeyFile.exists()) {
            this.logger.info("Client key file does not exist. Did you copy it?")
            exitProcess(0)
        }

        val clientKey = FileUtils.readStringFromFile(clientKeyFile)
        this.store.clientKey = clientKey
    }
}