package de.liruhg.lirucloud.master.process.proxy.handler

import de.liruhg.lirucloud.library.network.client.model.ClientInfoModel
import de.liruhg.lirucloud.library.network.util.NetworkUtil
import de.liruhg.lirucloud.library.process.ProcessStage
import de.liruhg.lirucloud.library.process.ProcessType
import de.liruhg.lirucloud.library.process.model.ProxyProcess
import de.liruhg.lirucloud.library.util.PortUtils
import de.liruhg.lirucloud.master.client.ClientRegistry
import de.liruhg.lirucloud.master.group.proxy.ProxyGroupHandler
import de.liruhg.lirucloud.master.process.ProcessRequestHandler
import de.liruhg.lirucloud.master.process.proxy.protocol.out.PacketOutRequestProxyProcess
import de.liruhg.lirucloud.master.process.proxy.registry.ProxyProcessRegistry
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

class ProxyProcessRequestHandler(
    private val networkUtil: NetworkUtil,
    private val proxyProcessRegistry: ProxyProcessRegistry,
    private val clientRegistry: ClientRegistry,
    private val proxyGroupHandler: ProxyGroupHandler
) : ProcessRequestHandler<ProxyProcess> {

    private val logger: Logger = LoggerFactory.getLogger(ProxyProcessRequestHandler::class.java)

    override fun requestProcessesOnConnect(clientInfoModel: ClientInfoModel) {
        val responsibleGroups = clientInfoModel.responsibleGroups
            .filter { this.proxyGroupHandler.getGroup(it) != null }
            .map { this.proxyGroupHandler.getGroup(it)!! }
            .toSet()

        for (group in responsibleGroups) {
            val currentlyRunning = this.proxyProcessRegistry.getRunningProcessCount(group.name)
            val clientName = "${clientInfoModel.name}${clientInfoModel.delimiter}${clientInfoModel.suffix}"
            val channel = clientInfoModel.channel

            if (channel == null) {
                this.logger.error("Channel of client is null. Name: [${clientName}]")
                continue
            }

            val ip = channel.remoteAddress().toString().replace("/", "").split(":")[0]

            if (group.minServersOnline != 1) {
                if (currentlyRunning >= group.minServersOnline) continue

                val count = group.minServersOnline - currentlyRunning
                val memoryUsageAfterRequest = clientInfoModel.currentMemoryUsage + (group.maxMemory * count)

                if (memoryUsageAfterRequest > clientInfoModel.memory) {
                    this.logger.warn(
                        "Blocked request of process on client with Name: [$clientName] - Reason: [Not enough memory]"
                    )
                    continue
                }

                this.requestProcesses(
                    count, ProxyProcess(
                        groupName = group.name,
                        name = null,
                        uuid = null,
                        ip = ip,
                        type = ProcessType.PROXY,
                        stage = ProcessStage.STARTING,
                        minMemory = group.minMemory,
                        maxMemory = group.maxMemory,
                        port = -1,
                        maxPlayers = group.maxPlayers
                    )
                )
            } else {
                if (currentlyRunning >= group.minServersOnline) {
                    continue
                }

                val memoryUsageAfterRequest = clientInfoModel.currentMemoryUsage + group.maxMemory

                if (memoryUsageAfterRequest > clientInfoModel.memory) {
                    this.logger.warn(
                        "Blocked request of process on client with Name: [$clientName] - Reason: [Not enough memory]"
                    )
                    continue
                }

                this.requestProcess(
                    ProxyProcess(
                        groupName = group.name,
                        name = null,
                        uuid = null,
                        ip = ip,
                        type = ProcessType.PROXY,
                        stage = ProcessStage.STARTING,
                        minMemory = group.minMemory,
                        maxMemory = group.maxMemory,
                        port = -1,
                        maxPlayers = group.maxPlayers
                    )
                )
            }
        }
    }

    override fun requestProcesses(count: Int, process: ProxyProcess) {
        (1..count).forEach { _ ->
            this.requestProcess(process)
        }
    }

    override fun requestProcess(process: ProxyProcess) {
        val groupName = process.groupName
        val currentlyRunning = this.proxyProcessRegistry.getRunningProcessCount(groupName)

        process.uuid = UUID.randomUUID().toString()
        process.port = PortUtils.getNextFreePort(25565)
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

        this.networkUtil.sendPacket(PacketOutRequestProxyProcess(process), channel)

        clientInfoModel.runningProcesses.add(process.uuid!!)

        this.clientRegistry.updateClient(clientInfoModel)
        this.proxyProcessRegistry.registerDanglingProcess(process)
        this.logger.info("Requested process with name: [${process.name}] on client with Name: [${clientName}]")
    }
}