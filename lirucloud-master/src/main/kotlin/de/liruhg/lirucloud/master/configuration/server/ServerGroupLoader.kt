package de.liruhg.lirucloud.master.configuration.server

import de.liruhg.lirucloud.library.configuration.Configuration
import de.liruhg.lirucloud.library.directory.Directories
import de.liruhg.lirucloud.library.process.ServerMode
import de.liruhg.lirucloud.library.util.FileUtils
import de.liruhg.lirucloud.master.group.server.ServerGroupHandler
import de.liruhg.lirucloud.master.group.server.model.ServerGroupModel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

class ServerGroupLoader(
    private val serverGroupHandler: ServerGroupHandler
) : Configuration {

    private val logger: Logger = LoggerFactory.getLogger(ServerGroupLoader::class.java)

    override fun execute() {
        val serverGroupFiles = File(Directories.MASTER_GROUPS_SERVER).listFiles()

        if (serverGroupFiles == null || serverGroupFiles.isEmpty()) {
            this.logger.warn("No server groups found. Continuing with creating default lobby group..")

            val serverGroupModel = ServerGroupModel(
                name = "Lobby",
                maxServersOnline = 1,
                minServersOnline = 1,
                maxMemory = 256,
                minMemory = 64,
                maxPlayers = 64,
                joinPower = 0,
                maintenance = false,
                template = "default",
                newServerPercentage = 100,
                mode = ServerMode.LOBBY,
                randomTemplateMode = false,
                templateModes = mutableSetOf()
            )

            this.serverGroupHandler.createGroup(serverGroupModel)
            return
        }

        for (serverGroupFile in serverGroupFiles) {
            if (!serverGroupFile.name.endsWith(".json")) {
                this.logger.warn("Found invalid server group file Name: [${serverGroupFile.name}]")
                continue
            }

            val serverGroupModel = FileUtils.readClassFromJson(serverGroupFile, ServerGroupModel::class.java)
            this.serverGroupHandler.registerGroup(serverGroupModel)
        }
    }
}