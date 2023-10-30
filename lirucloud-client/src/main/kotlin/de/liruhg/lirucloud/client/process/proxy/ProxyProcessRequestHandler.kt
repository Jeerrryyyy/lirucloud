package de.liruhg.lirucloud.client.process.proxy

import de.liruhg.lirucloud.client.process.ProcessRegistry
import de.liruhg.lirucloud.client.process.ProcessRequestHandler
import de.liruhg.lirucloud.library.database.handler.SyncFileHandler
import de.liruhg.lirucloud.library.directory.Directories
import de.liruhg.lirucloud.library.process.ProcessStreamConsumer
import de.liruhg.lirucloud.library.process.model.ProxyProcess
import de.liruhg.lirucloud.library.thread.ThreadPool
import de.liruhg.lirucloud.library.util.FileUtils
import de.liruhg.lirucloud.library.util.HashUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

class ProxyProcessRequestHandler(
    private val processRegistry: ProcessRegistry<InternalProxyProcess>,
    private val fileHandler: SyncFileHandler,
    private val proxyConfigGenerator: ProxyConfigGenerator,
    private val threadPool: ThreadPool
) : ProcessRequestHandler<ProxyProcess> {

    private val logger: Logger = LoggerFactory.getLogger(ProxyProcessRequestHandler::class.java)

    override fun handle(request: ProxyProcess) {
        this.threadPool.execute({
            val hashedName = HashUtils.hashStringMD5(request.groupName)

            this.fileHandler.downloadFile(
                hashedName,
                File(Directories.CLIENT_CACHED_TEMPLATES_PROXY, "${request.groupName}.zip")
            )

            FileUtils.unzipFile(
                File(Directories.CLIENT_CACHED_TEMPLATES_PROXY, "${request.groupName}.zip"),
                "${Directories.CLIENT_CACHED_TEMPLATES_PROXY}/${request.groupName}"
            )

            val serverDirectory =
                File("${Directories.CLIENT_RUNNING_PROCESSES_PROXY}/${request.groupName}_${request.uuid}")

            FileUtils.copyAllFiles(
                File("${Directories.CLIENT_CACHED_TEMPLATES_PROXY}/${request.groupName}/default").toPath(),
                serverDirectory.path
            )
            FileUtils.copyFile(
                File(Directories.CLIENT_STATIC_PROXY, "proxy.jar"),
                File(serverDirectory, "proxy.jar")
            )

            this.proxyConfigGenerator.writeConfig(
                serverDirectory,
                request.port,
                request.maxPlayers,
                60,
                "§aLiruCloud §7- §ecreated by Jevzo"
            )

            val processBuilder = ProcessBuilder()
            processBuilder.command(
                "java",
                "-Xms${request.minMemory}m",
                "-Xmx${request.maxMemory}m",
                "-jar",
                "proxy.jar"
            )
            processBuilder.directory(serverDirectory)

            val process = processBuilder.start()
            val processStreamConsumer = ProcessStreamConsumer(process.inputStream)

            val internalProxyProcess = InternalProxyProcess(
                request.groupName,
                request.name,
                request.uuid,
                request.ip,
                request.type,
                request.stage,
                request.minMemory,
                request.maxMemory,
                request.port,
                request.maxPlayers,
                request.joinPower,
                request.maintenance,
                serverDirectory.toPath(),
                process,
                processStreamConsumer
            )

            this.threadPool.execute(processStreamConsumer)

            this.processRegistry.registerProcess(internalProxyProcess)
            this.logger.info("Successfully started process with Name: [${request.name}]")
        }, false)
    }
}
