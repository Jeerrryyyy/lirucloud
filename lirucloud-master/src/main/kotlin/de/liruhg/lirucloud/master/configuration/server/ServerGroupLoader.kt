package de.liruhg.lirucloud.master.configuration.server

import de.liruhg.lirucloud.library.configuration.Configuration
import de.liruhg.lirucloud.library.process.ProcessMode
import de.liruhg.lirucloud.library.server.ServerInformationModel
import de.liruhg.lirucloud.master.group.server.ServerGroupHandler
import de.liruhg.lirucloud.master.group.server.model.ServerGroupModel
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ServerGroupLoader(
    private val serverGroupHandler: ServerGroupHandler
) : Configuration {

    private val logger: Logger = LoggerFactory.getLogger(ServerGroupLoader::class.java)

    override fun execute() {
        val shouldCreateGroup = this.serverGroupHandler.shouldCreateGroup()

        if (shouldCreateGroup) {
            this.logger.warn("No server groups found. Continuing with creating default lobby group..")

            val serverGroupModel = ServerGroupModel(
                name = "Lobby",
                minServersOnline = 1,
                maxMemory = 256,
                minMemory = 64,
                maxPlayers = 64,
                serverInformation = ServerInformationModel(
                    joinPower = 0,
                    maintenance = false,
                ),
                template = "default",
                newServerPercentage = 100,
                mode = ProcessMode.LOBBY,
                randomTemplateMode = false,
                templateModes = mutableSetOf()
            )

            this.serverGroupHandler.createGroup(serverGroupModel)
            return
        }

        this.serverGroupHandler.fetchGroups().forEach {
            this.serverGroupHandler.registerGroup(it)

            this.logger.info("Registered server group with Name: [${it.name}]")
        }
    }
}