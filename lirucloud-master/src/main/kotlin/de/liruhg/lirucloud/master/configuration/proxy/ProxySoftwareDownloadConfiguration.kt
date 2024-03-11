package de.liruhg.lirucloud.master.configuration.proxy

import de.liruhg.lirucloud.library.configuration.Configuration
import de.liruhg.lirucloud.library.database.handler.FileHandler
import de.liruhg.lirucloud.library.directory.Directories
import de.liruhg.lirucloud.library.util.DownloadUtils
import de.liruhg.lirucloud.library.util.FileUtils
import de.liruhg.lirucloud.library.util.HashUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import kotlin.system.exitProcess

class ProxySoftwareDownloadConfiguration(
    private val fileHandler: FileHandler
) : Configuration {

    private val logger: Logger = LoggerFactory.getLogger(ProxySoftwareDownloadConfiguration::class.java)

    override fun execute() {
        val proxyJar = File(Directories.MASTER_SOFTWARE_PROXY, "proxy.jar")

        if (proxyJar.exists()) return

        val proxyJarUrl =
            "https://api.papermc.io/v2/projects/waterfall/versions/1.20/builds/548/downloads/waterfall-1.20-548.jar"
        this.logger.info("Downloading Proxy Jar from URL: [$proxyJarUrl]")

        try {
            DownloadUtils.downloadFile(proxyJarUrl, proxyJar.path, 200000, 200000, false)

            FileUtils.zipMultipleFiles(File(Directories.MASTER_SOFTWARE_PROXY, "proxy-software.zip"), setOf(proxyJar))

            this.logger.info("Uploading proxy jar to database bucket...")
            this.fileHandler.uploadFile(
                File(Directories.MASTER_SOFTWARE_PROXY, "proxy-software.zip"),
                HashUtils.hashStringMD5("proxySoftware")
            )
        } catch (e: Exception) {
            this.logger.error("Failed to download Proxy Jar from URL: [$proxyJarUrl] - Reason: [${e.message}]")
            exitProcess(0)
        }
    }
}