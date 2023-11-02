package de.liruhg.lirucloud.api.global.configuration

import de.liruhg.lirucloud.api.global.runtime.RuntimeVars
import de.liruhg.lirucloud.library.configuration.Configuration
import de.liruhg.lirucloud.library.proxy.PluginConfigurationModel
import de.liruhg.lirucloud.library.util.FileUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

class DefaultPluginConfiguration(
    private val pluginConfigurationFile: File,
    private val runtimeVars: RuntimeVars
) : Configuration {

    private val logger: Logger = LoggerFactory.getLogger(DefaultPluginConfiguration::class.java)

    override fun execute() {
        if (!this.pluginConfigurationFile.exists()) {
            this.logger.info("Cloud configuration file does not exist... Process can not start properly.")
            return
        }

        val pluginConfiguration = FileUtils.readClassFromJson(
            this.pluginConfigurationFile,
            PluginConfigurationModel::class.java
        )

        this.runtimeVars.pluginConfiguration = pluginConfiguration
    }
}