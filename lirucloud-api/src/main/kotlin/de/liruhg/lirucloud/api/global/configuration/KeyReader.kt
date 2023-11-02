package de.liruhg.lirucloud.api.global.configuration

import de.liruhg.lirucloud.api.global.runtime.RuntimeVars
import de.liruhg.lirucloud.library.configuration.Configuration
import de.liruhg.lirucloud.library.util.FileUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

class KeyReader(
    private val clientKeyFile: File,
    private val runtimeVars: RuntimeVars
) : Configuration {

    private val logger: Logger = LoggerFactory.getLogger(KeyReader::class.java)

    override fun execute() {
        if (!this.clientKeyFile.exists()) {
            this.logger.error("Can't find the client key in \"${this.clientKeyFile.path}\"!")
            return
        }

        this.runtimeVars.clientKey = FileUtils.readStringFromFile(this.clientKeyFile)
    }
}