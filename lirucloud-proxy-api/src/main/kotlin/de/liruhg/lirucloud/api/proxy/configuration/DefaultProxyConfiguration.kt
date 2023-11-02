package de.liruhg.lirucloud.api.proxy.configuration

import de.liruhg.lirucloud.api.proxy.runtime.RuntimeVars
import de.liruhg.lirucloud.library.configuration.Configuration
import de.liruhg.lirucloud.library.directory.Directories
import de.liruhg.lirucloud.library.proxy.ProxyPluginConfigurationModel
import de.liruhg.lirucloud.library.util.FileUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

class DefaultProxyConfiguration(
    private val runtimeVars: RuntimeVars
) : Configuration {

    private val logger: Logger = LoggerFactory.getLogger(DefaultProxyConfiguration::class.java)

    override fun execute() {
        val proxyConfigurationFile = File(Directories.PROXY_PLUGINS_API, "config.json")

        if (!proxyConfigurationFile.exists()) {
            this.logger.info("Cloud configuration file does not exist... Process can not start properly.")
            return
        }

        val proxyPluginConfigurationModel = FileUtils.readClassFromJson(
            proxyConfigurationFile,
            ProxyPluginConfigurationModel::class.java
        )

        this.runtimeVars.proxyPluginConfigurationModel = proxyPluginConfigurationModel
    }
}