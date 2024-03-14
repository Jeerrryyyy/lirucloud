package de.liruhg.lirucloud.master.task

import de.liruhg.lirucloud.master.LiruCloudMaster
import de.liruhg.lirucloud.master.client.ClientRegistry
import de.liruhg.lirucloud.master.process.proxy.ProxyProcessRequestHandler
import de.liruhg.lirucloud.master.store.Store
import org.kodein.di.instance
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

class CheckProxiesTask : TimerTask() {

    private val logger: Logger = LoggerFactory.getLogger(CheckProxiesTask::class.java)

    private val clientRegistry: ClientRegistry by LiruCloudMaster.KODEIN.instance()
    private val proxyProcessRequestHandler: ProxyProcessRequestHandler by LiruCloudMaster.KODEIN.instance()
    private val store: Store by LiruCloudMaster.KODEIN.instance()

    override fun run() {
        if (this.clientRegistry.getClients().isEmpty()) {
            return
        }

        val clientInfo = this.clientRegistry.getLeastUsedClient(this.store.cloudConfiguration.defaultProxyGroupName)

        if (clientInfo == null) {
            this.logger.error("Could not find any clients to handle request for default proxy group with name: [${this.store.cloudConfiguration.defaultProxyGroupName}]")
            return
        }

        this.proxyProcessRequestHandler.checkDefaultProcesses(clientInfo)
    }
}