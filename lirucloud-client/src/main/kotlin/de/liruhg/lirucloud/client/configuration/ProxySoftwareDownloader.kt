package de.liruhg.lirucloud.client.configuration

import de.liruhg.lirucloud.library.configuration.Configuration
import de.liruhg.lirucloud.library.database.handler.FileHandler
import de.liruhg.lirucloud.library.directory.Directories
import de.liruhg.lirucloud.library.util.FileUtils
import de.liruhg.lirucloud.library.util.HashUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

class ProxySoftwareDownloader(
    private val fileHandler: FileHandler
) : Configuration {

    private val logger: Logger = LoggerFactory.getLogger(ProxySoftwareDownloader::class.java)

    override fun execute() {
        val staticProxyJar = File("${Directories.CLIENT_STATIC_PROXY}/proxy.jar")

        if (staticProxyJar.exists()) {
            return
        }

        this.logger.info("Downloading proxy.jar because it was not found...")

        val proxyJarNameHash = HashUtils.hashStringMD5("proxySoftware")

        val downloadedFile = File("${Directories.CLIENT_STATIC_PROXY}/proxy.zip")
        this.fileHandler.downloadFile(proxyJarNameHash, downloadedFile)

        FileUtils.unzipFile(
            downloadedFile,
            Directories.CLIENT_STATIC_PROXY
        )

        this.logger.info("Finished downloading proxy.jar!")
    }
}