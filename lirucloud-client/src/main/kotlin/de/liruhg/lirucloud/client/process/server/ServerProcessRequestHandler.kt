package de.liruhg.lirucloud.client.process.server

import de.liruhg.lirucloud.client.process.ProcessRegistry
import de.liruhg.lirucloud.client.process.ProcessRequestHandler
import de.liruhg.lirucloud.library.database.handler.SyncFileHandler
import de.liruhg.lirucloud.library.directory.Directories
import de.liruhg.lirucloud.library.process.ProcessStreamConsumer
import de.liruhg.lirucloud.library.process.model.ServerProcess
import de.liruhg.lirucloud.library.thread.ThreadPool
import de.liruhg.lirucloud.library.util.FileUtils
import de.liruhg.lirucloud.library.util.HashUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

class ServerProcessRequestHandler(
    private val processRegistry: ProcessRegistry<InternalServerProcess>,
    private val fileHandler: SyncFileHandler,
    private val threadPool: ThreadPool
) : ProcessRequestHandler<ServerProcess> {

    private val logger: Logger = LoggerFactory.getLogger(ServerProcessRequestHandler::class.java)

    override fun handle(request: ServerProcess) {
        this.threadPool.execute({
            val hashedName = HashUtils.hashStringMD5(request.groupName)

            this.fileHandler.downloadFile(
                hashedName,
                File(Directories.CLIENT_CACHED_TEMPLATES_SERVER, "${request.groupName}.zip")
            )

            FileUtils.unzipFile(
                File(Directories.CLIENT_CACHED_TEMPLATES_SERVER, "${request.groupName}.zip"),
                "${Directories.CLIENT_CACHED_TEMPLATES_SERVER}/${request.groupName}"
            )

            val serverDirectory =
                File("${Directories.CLIENT_RUNNING_PROCESSES_SERVER}/${request.groupName}_${request.uuid}")

            FileUtils.copyAllFiles(
                File("${Directories.CLIENT_CACHED_TEMPLATES_SERVER}/${request.groupName}/default").toPath(),
                serverDirectory.path
            )
            FileUtils.copyFile(
                File(Directories.CLIENT_STATIC_SERVER, "server.jar"),
                File(serverDirectory, "server.jar")
            )

            FileUtils.writeStringToFile(File(serverDirectory, "eula.txt"), "eula=true")
            FileUtils.writeStringToFile(
                File(serverDirectory, "server.properties"),
                ServerProperties.getProperties(serverPort = request.port, maxPlayers = request.maxPlayers)
            )

            val processBuilder = ProcessBuilder()
            processBuilder.command(
                "java",
                "-Xms${request.minMemory}m",
                "-Xmx${request.maxMemory}m",
                "-jar",
                "server.jar"
            )
            processBuilder.directory(serverDirectory)

            val process = processBuilder.start()
            val processStreamConsumer = ProcessStreamConsumer(process.inputStream)

            val internalServerProcess = InternalServerProcess(
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
                processStreamConsumer,
                request.mode
            )

            this.threadPool.execute(processStreamConsumer)

            this.processRegistry.registerProcess(internalServerProcess)
            this.logger.info("Successfully started process with Name: [${request.name}]")
        }, false)
    }
}