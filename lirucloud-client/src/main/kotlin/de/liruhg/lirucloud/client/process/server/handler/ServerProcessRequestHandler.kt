package de.liruhg.lirucloud.client.process.server.handler

import de.liruhg.lirucloud.client.process.InternalCloudProcess
import de.liruhg.lirucloud.client.process.ProcessRegistry
import de.liruhg.lirucloud.client.process.ProcessRequestHandler
import de.liruhg.lirucloud.client.process.server.config.ServerConfigGenerator
import de.liruhg.lirucloud.client.process.server.config.ServerProperties
import de.liruhg.lirucloud.client.runtime.RuntimeVars
import de.liruhg.lirucloud.library.database.handler.SyncFileHandler
import de.liruhg.lirucloud.library.directory.Directories
import de.liruhg.lirucloud.library.process.CloudProcess
import de.liruhg.lirucloud.library.process.ProcessStreamConsumer
import de.liruhg.lirucloud.library.process.model.PluginConfigurationModel
import de.liruhg.lirucloud.library.process.model.ProcessInformationModel
import de.liruhg.lirucloud.library.thread.ThreadPool
import de.liruhg.lirucloud.library.util.FileUtils
import de.liruhg.lirucloud.library.util.HashUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Path

class ServerProcessRequestHandler(
    private val processRegistry: ProcessRegistry,
    private val fileHandler: SyncFileHandler,
    private val threadPool: ThreadPool,
    private val runtimeVars: RuntimeVars,
    private val serverConfigGenerator: ServerConfigGenerator
) : ProcessRequestHandler {

    private val logger: Logger = LoggerFactory.getLogger(ServerProcessRequestHandler::class.java)

    override fun handle(request: CloudProcess) {
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

            this.serverConfigGenerator.generateBukkitYaml(serverDirectory)
            this.serverConfigGenerator.generateSpigotYaml(serverDirectory)

            FileUtils.createDirectory(Path.of(serverDirectory.path, Directories.SERVER_PLUGINS_API))

            FileUtils.copyFile(
                File(Directories.CLIENT_KEYS, "client.key"),
                File(serverDirectory, "${Directories.SERVER_PLUGINS_API}/client.key")
            )

            FileUtils.writeClassToJsonFile(
                File(serverDirectory, "${Directories.SERVER_PLUGINS_API}/config.json"),
                PluginConfigurationModel(
                    runtimeVars.cloudConfiguration.masterAddress,
                    runtimeVars.cloudConfiguration.masterPort,
                    runtimeVars.cloudConfiguration.database,
                    ProcessInformationModel(
                        request.uuid.orEmpty(),
                        request.name.orEmpty()
                    )
                )
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

            val internalServerProcess = InternalCloudProcess(
                request.groupName,
                request.name,
                request.uuid,
                request.ip,
                request.type,
                request.stage,
                request.mode,
                request.minMemory,
                request.maxMemory,
                request.port,
                request.maxPlayers,
                serverDirectory.toPath(),
                process,
                processStreamConsumer
            )

            this.threadPool.execute(processStreamConsumer)

            this.processRegistry.registerProcess(internalServerProcess)
            this.logger.info("Successfully started process with Name: [${internalServerProcess.name}] - UUID: [${internalServerProcess.uuid}]")
        }, false)
    }
}