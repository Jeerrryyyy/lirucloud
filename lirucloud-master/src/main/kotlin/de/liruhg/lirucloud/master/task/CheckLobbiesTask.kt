package de.liruhg.lirucloud.master.task

import de.liruhg.lirucloud.master.LiruCloudMaster
import de.liruhg.lirucloud.master.client.ClientRegistry
import de.liruhg.lirucloud.master.process.server.ServerProcessRequestHandler
import de.liruhg.lirucloud.master.store.Store
import org.kodein.di.instance
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

class CheckLobbiesTask : TimerTask() {

    private val logger: Logger = LoggerFactory.getLogger(CheckLobbiesTask::class.java)

    private val clientRegistry: ClientRegistry by LiruCloudMaster.KODEIN.instance()
    private val serverProcessRequestHandler: ServerProcessRequestHandler by LiruCloudMaster.KODEIN.instance()
    private val store: Store by LiruCloudMaster.KODEIN.instance()

    override fun run() {
        if (this.clientRegistry.getClients().isEmpty()) {
            return
        }

        val clientInfo = this.clientRegistry.getLeastUsedClient(this.store.cloudConfiguration.defaultLobbyGroupName)

        if (clientInfo == null) {
            this.logger.error("Could not find any clients to handle request for default lobby group with name: [${this.store.cloudConfiguration.defaultLobbyGroupName}]")
            return
        }

        this.serverProcessRequestHandler.checkDefaultProcesses(clientInfo)
    }
}