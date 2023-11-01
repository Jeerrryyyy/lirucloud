package de.liruhg.lirucloud.api.proxy.configuration

import de.liruhg.lirucloud.api.proxy.runtime.RuntimeVars
import de.liruhg.lirucloud.library.configuration.Configuration
import de.liruhg.lirucloud.library.directory.Directories
import de.liruhg.lirucloud.library.util.FileUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

class KeyReader(
    private val runtimeVars: RuntimeVars
) : Configuration {

    private val logger: Logger = LoggerFactory.getLogger(KeyReader::class.java)

    override fun execute() {
        val clientKeyFile = File("${Directories.PROXY_PLUGINS_API}/client.key")

        if (!clientKeyFile.exists()) {
            this.logger.error("Can't find the slave key in \"${Directories.PROXY_PLUGINS_API}\"!")
            return
        }

        this.runtimeVars.clientKey = FileUtils.readStringFromFile(clientKeyFile)
    }
}