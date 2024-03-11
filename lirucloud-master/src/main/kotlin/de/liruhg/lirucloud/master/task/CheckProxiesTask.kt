package de.liruhg.lirucloud.master.task

import de.liruhg.lirucloud.library.process.CloudProcess
import de.liruhg.lirucloud.library.process.ProcessMode
import de.liruhg.lirucloud.library.process.ProcessStage
import de.liruhg.lirucloud.library.process.ProcessType
import de.liruhg.lirucloud.master.LiruCloudMaster
import de.liruhg.lirucloud.master.client.ClientRegistry
import de.liruhg.lirucloud.master.group.proxy.ProxyGroupHandler
import de.liruhg.lirucloud.master.process.ProcessRegistry
import de.liruhg.lirucloud.master.process.proxy.ProxyProcessRequestHandler
import de.liruhg.lirucloud.master.store.Store
import org.kodein.di.instance
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

class CheckProxiesTask : TimerTask() {

    private val logger: Logger = LoggerFactory.getLogger(CheckDanglingConnectionsTask::class.java)

    private val proxyGroupHandler: ProxyGroupHandler by LiruCloudMaster.KODEIN.instance()
    private val processRegistry: ProcessRegistry by LiruCloudMaster.KODEIN.instance()
    private val proxyProcessRequestHandler: ProxyProcessRequestHandler by LiruCloudMaster.KODEIN.instance()
    private val store: Store by LiruCloudMaster.KODEIN.instance()
    private val clientRegistry: ClientRegistry by LiruCloudMaster.KODEIN.instance()

    override fun run() {
        if (this.clientRegistry.getClients().isEmpty()) {
            return
        }

        val processes = this.processRegistry.getProcesses(this.store.cloudConfiguration.defaultProxyGroupName)
        val proxyGroup = this.proxyGroupHandler.getGroup(this.store.cloudConfiguration.defaultProxyGroupName)

        if (proxyGroup == null) {
            this.logger.error("The default proxy group with name: [${this.store.cloudConfiguration.defaultProxyGroupName}] configured in cloud config is not present.")
            return
        }

        if (processes.isNotEmpty() && processes.size >= proxyGroup.minServersOnline) {
            return
        }

        this.logger.warn("Found not enough instances of default proxy group with name: [${this.store.cloudConfiguration.defaultProxyGroupName}] were running!")

        val proxiesToStart = proxyGroup.minServersOnline - processes.size

        if (proxiesToStart < 0) {
            return
        }

        val clientInfo = this.clientRegistry.getLeastUsedClient(this.store.cloudConfiguration.defaultProxyGroupName)

        if (clientInfo == null) {
            this.logger.error("Could not find any clients to handle request for default proxy group with name: [${this.store.cloudConfiguration.defaultProxyGroupName}]")
            return
        }

        val clientName = "${clientInfo.name}${clientInfo.delimiter}${clientInfo.suffix}"
        val channel = clientInfo.channel

        if (channel == null) {
            this.logger.error("Channel of client is null. Name: [${clientName}]")
            return
        }

        val count = proxyGroup.minServersOnline - processes.size
        val memoryUsageAfterRequest = clientInfo.currentMemoryUsage + (proxyGroup.maxMemory * count)

        if (memoryUsageAfterRequest > clientInfo.memory) {
            this.logger.warn(
                "Blocked request of process on client with Name: [$clientName] - Reason: [Not enough memory]"
            )
            return
        }

        val ip = channel.remoteAddress().toString().replace("/", "").split(":")[0]

        this.proxyProcessRequestHandler.requestProcesses(
            proxiesToStart, CloudProcess(
                groupName = proxyGroup.name,
                name = null,
                uuid = null,
                ip = ip,
                type = ProcessType.PROXY,
                stage = ProcessStage.STARTING,
                mode = ProcessMode.NONE,
                minMemory = proxyGroup.minMemory,
                maxMemory = proxyGroup.maxMemory,
                port = -1,
                maxPlayers = proxyGroup.maxPlayers
            )
        )
    }
}