package de.liruhg.lirucloud.client.configuration

import de.liruhg.lirucloud.client.runtime.RuntimeVars
import de.liruhg.lirucloud.library.configuration.Configuration
import de.liruhg.lirucloud.library.directory.Directories
import de.liruhg.lirucloud.library.util.FileUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import kotlin.system.exitProcess

class KeyReader(
    private val runtimeVars: RuntimeVars
) : Configuration {

    private val logger: Logger = LoggerFactory.getLogger(KeyReader::class.java)

    override fun execute() {
        val clientKeyFile = File("${Directories.CLIENT_KEYS}/client.key")

        if (!clientKeyFile.exists()) {
            this.logger.error("Can't find the client key in \"${Directories.CLIENT_KEYS}\"! Did you copy it?")
            exitProcess(0)
        }

        this.runtimeVars.clientKey = FileUtils.readStringFromFile(clientKeyFile)
    }
}