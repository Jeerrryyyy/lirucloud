package de.liruhg.lirucloud.client.configuration.server

import de.liruhg.lirucloud.library.configuration.Configuration
import de.liruhg.lirucloud.library.database.handler.SyncFileHandler
import de.liruhg.lirucloud.library.directory.Directories
import de.liruhg.lirucloud.library.util.FileUtils
import de.liruhg.lirucloud.library.util.HashUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

class ServerSoftwareDownloader(
    private val syncFileHandler: SyncFileHandler
) : Configuration {

    private val logger: Logger = LoggerFactory.getLogger(ServerSoftwareDownloader::class.java)

    override fun execute() {
        val staticServerJar = File("${Directories.CLIENT_STATIC_SERVER}/server.jar")

        if (staticServerJar.exists()) {
            return
        }

        this.logger.info("Downloading server.jar because it was not found...")

        val serverJarNameHash = HashUtils.hashStringMD5("serverSoftware")

        val downloadedFile = File("${Directories.CLIENT_STATIC_SERVER}/server.zip")
        this.syncFileHandler.downloadFile(serverJarNameHash, downloadedFile)

        FileUtils.unzipFile(
            downloadedFile,
            Directories.CLIENT_STATIC_SERVER
        )

        this.logger.info("Finished downloading server.jar!")
    }
}