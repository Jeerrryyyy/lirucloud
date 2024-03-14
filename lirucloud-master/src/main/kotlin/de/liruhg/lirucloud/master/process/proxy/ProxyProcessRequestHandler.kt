package de.liruhg.lirucloud.master.process.proxy

import de.liruhg.lirucloud.library.client.ClientInfo
import de.liruhg.lirucloud.library.network.util.NetworkUtil
import de.liruhg.lirucloud.library.process.CloudProcess
import de.liruhg.lirucloud.library.process.ProcessMode
import de.liruhg.lirucloud.library.process.ProcessStage
import de.liruhg.lirucloud.library.process.ProcessType
import de.liruhg.lirucloud.library.util.PortUtil
import de.liruhg.lirucloud.master.client.ClientRegistry
import de.liruhg.lirucloud.master.group.proxy.ProxyGroupHandler
import de.liruhg.lirucloud.master.process.ProcessRegistry
import de.liruhg.lirucloud.master.process.ProcessRequestHandler
import de.liruhg.lirucloud.master.process.protocol.`in`.PacketInRequestProcessResult
import de.liruhg.lirucloud.master.process.protocol.out.PacketOutRequestProcess
import de.liruhg.lirucloud.master.store.Store
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

class ProxyProcessRequestHandler(
    private val networkUtil: NetworkUtil,
    private val clientRegistry: ClientRegistry,
    private val processRegistry: ProcessRegistry,
    private val portUtil: PortUtil,
    private val proxyGroupHandler: ProxyGroupHandler,
    private val store: Store
) : ProcessRequestHandler {

    private val logger: Logger = LoggerFactory.getLogger(ProxyProcessRequestHandler::class.java)

    override fun requestProcesses(count: Int, process: CloudProcess) {
        (1..count).forEach { _ ->
            this.requestProcess(process)
        }
    }

    override fun requestProcess(process: CloudProcess) {
        val groupName = process.groupName
        val currentlyRunning = this.processRegistry.getRunningProcessCount(groupName)

        process.uuid = UUID.randomUUID().toString()

        process.port = this.portUtil.getNextFreePort(25565)
        this.portUtil.blockPort(process.port)

        process.name =
            "$groupName-${if ((currentlyRunning + 1) >= 10) "${currentlyRunning + 1}" else "0${currentlyRunning + 1}"}"

        val clientInfoModel = this.clientRegistry.getLeastUsedClient(groupName)

        if (clientInfoModel == null) {
            this.logger.error("No client found for group with Name: [$groupName]")
            return
        }

        val clientName = "${clientInfoModel.name}${clientInfoModel.delimiter}${clientInfoModel.suffix}"
        val channel = clientInfoModel.channel

        if (channel == null) {
            this.logger.error("Channel of client is null. Name: [${clientName}]")
            return
        }

        this.networkUtil.sendPacket<PacketInRequestProcessResult>(PacketOutRequestProcess(process), channel) {
            if (!it.success) {
                this.logger.error("Client with name: [$clientName] rejected process with name: [${process.name}] - Reason: [${it.message}]")
                return@sendPacket
            }

            clientInfoModel.runningProcesses.add(process.uuid!!)

            this.clientRegistry.updateClient(clientInfoModel)
            this.processRegistry.addProcess(process)
            this.logger.info("Requested process with name: [${process.name}] on client with Name: [${clientName}]")
        }
    }

    override fun checkDefaultProcesses(clientInfo: ClientInfo) {
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

        this.requestProcesses(
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