package de.liruhg.lirucloud.client.process.server

import de.liruhg.lirucloud.client.process.InternalCloudProcess
import de.liruhg.lirucloud.client.process.ProcessRegistry
import de.liruhg.lirucloud.client.process.ProcessRequestHandler
import de.liruhg.lirucloud.client.store.Store
import de.liruhg.lirucloud.library.database.handler.FileHandler
import de.liruhg.lirucloud.library.directory.Directories
import de.liruhg.lirucloud.library.process.CloudProcess
import de.liruhg.lirucloud.library.process.ProcessStreamConsumer
import de.liruhg.lirucloud.library.process.model.PluginConfiguration
import de.liruhg.lirucloud.library.process.model.ProcessInformation
import de.liruhg.lirucloud.library.thread.ThreadPool
import de.liruhg.lirucloud.library.util.FileUtils
import de.liruhg.lirucloud.library.util.HashUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Path

class ServerProcessRequestHandler(
    private val processRegistry: ProcessRegistry,
    private val fileHandler: FileHandler,
    private val threadPool: ThreadPool,
    private val store: Store,
    private val serverConfigGenerator: ServerConfigGenerator
) : ProcessRequestHandler {

    private val logger: Logger = LoggerFactory.getLogger(ServerProcessRequestHandler::class.java)

    override fun handle(process: CloudProcess): Pair<String, Boolean> {
        val hashedName = HashUtils.hashStringMD5(process.groupName)

        this.fileHandler.downloadFile(
            hashedName,
            File(Directories.CLIENT_CACHED_TEMPLATES_SERVER, "${process.groupName}.zip")
        )

        FileUtils.unzipFile(
            File(Directories.CLIENT_CACHED_TEMPLATES_SERVER, "${process.groupName}.zip"),
            "${Directories.CLIENT_CACHED_TEMPLATES_SERVER}/${process.groupName}"
        )

        val serverDirectory =
            File("${Directories.CLIENT_RUNNING_PROCESSES_SERVER}/${process.groupName}_${process.uuid}")

        FileUtils.copyAllFiles(
            File("${Directories.CLIENT_CACHED_TEMPLATES_SERVER}/${process.groupName}/default").toPath(),
            serverDirectory.path
        )
        FileUtils.copyFile(
            File(Directories.CLIENT_STATIC_SERVER, "server.jar"),
            File(serverDirectory, "server.jar")
        )

        FileUtils.writeStringToFile(File(serverDirectory, "eula.txt"), "eula=true")
        FileUtils.writeStringToFile(
            File(serverDirectory, "server.properties"),
            ServerProperties.getProperties(serverPort = process.port, maxPlayers = process.maxPlayers)
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
            PluginConfiguration(
                store.cloudConfiguration.masterAddress,
                store.cloudConfiguration.masterPort,
                store.cloudConfiguration.database,
                store.cloudConfiguration.cache,
                ProcessInformation(
                    process.uuid.orEmpty(),
                    process.name.orEmpty()
                )
            )
        )

        val processBuilder = ProcessBuilder()
        processBuilder.command(
            "java",
            "-Xms${process.minMemory}m",
            "-Xmx${process.maxMemory}m",
            "-jar",
            "server.jar"
        )
        processBuilder.directory(serverDirectory)

        val startedProcess = processBuilder.start()
        val processStreamConsumer = ProcessStreamConsumer(startedProcess.inputStream)

        val internalServerProcess = InternalCloudProcess(
            process.groupName,
            process.name,
            process.uuid,
            process.ip,
            process.type,
            process.stage,
            process.mode,
            process.minMemory,
            process.maxMemory,
            process.port,
            process.maxPlayers,
            serverDirectory.toPath(),
            startedProcess,
            processStreamConsumer
        )

        this.threadPool.execute(processStreamConsumer)

        this.processRegistry.registerProcess(internalServerProcess)

        val message =
            "Successfully started process with Name: [${internalServerProcess.name}] - UUID: [${internalServerProcess.uuid}]"
        this.logger.info(message)

        return Pair(message, true)
    }
}