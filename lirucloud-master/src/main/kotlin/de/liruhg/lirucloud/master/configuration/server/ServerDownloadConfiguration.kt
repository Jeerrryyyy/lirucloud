package de.liruhg.lirucloud.master.configuration.server

import de.liruhg.lirucloud.library.configuration.Configuration
import de.liruhg.lirucloud.library.database.handler.SyncFileHandler
import de.liruhg.lirucloud.library.directory.Directories
import de.liruhg.lirucloud.library.util.DownloadUtils
import de.liruhg.lirucloud.library.util.FileUtils
import de.liruhg.lirucloud.library.util.HashUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import kotlin.system.exitProcess

class ServerDownloadConfiguration(
    private val fileHandler: SyncFileHandler
) : Configuration {

    private val logger: Logger = LoggerFactory.getLogger(ServerDownloadConfiguration::class.java)

    override fun execute() {
        val serverJar = File(Directories.MASTER_SOFTWARE_SERVER, "server.jar")

        if (serverJar.exists()) return

        val serverJarUrl =
            "https://api.papermc.io/v2/projects/paper/versions/1.8.8/builds/445/downloads/paper-1.8.8-445.jar"
        this.logger.info("Downloading Server Jar from URL: [$serverJarUrl]")

        try {
            DownloadUtils.downloadFile(serverJarUrl, serverJar.path, 20000, 20000, false)

            FileUtils.zipMultipleFiles(File(Directories.MASTER_SOFTWARE_SERVER, "server.zip"), setOf(serverJar))

            this.logger.info("Uploading server jar to database bucket...")
            this.fileHandler.uploadFile(
                File(Directories.MASTER_SOFTWARE_SERVER, "server.zip"),
                HashUtils.hashStringMD5("serverSoftware")
            )
        } catch (e: Exception) {
            this.logger.error("Failed to download Server Jar from URL: [$serverJarUrl] - Reason: [${e.message}]")
            exitProcess(0)
        }
    }
}