package de.liruhg.lirucloud.master.process.proxy.handler

import de.liruhg.lirucloud.library.network.client.model.ClientInfoModel
import de.liruhg.lirucloud.library.network.util.NetworkUtil
import de.liruhg.lirucloud.library.process.CloudProcess
import de.liruhg.lirucloud.library.process.ProcessMode
import de.liruhg.lirucloud.library.process.ProcessStage
import de.liruhg.lirucloud.library.process.ProcessType
import de.liruhg.lirucloud.library.util.PortUtil
import de.liruhg.lirucloud.master.client.ClientRegistry
import de.liruhg.lirucloud.master.group.proxy.ProxyGroupHandler
import de.liruhg.lirucloud.master.process.handler.ProcessRequestHandler
import de.liruhg.lirucloud.master.process.protocol.out.PacketOutRequestProcess
import de.liruhg.lirucloud.master.process.registry.ProcessRegistry
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

class ProxyProcessRequestHandler(
    private val networkUtil: NetworkUtil,
    private val clientRegistry: ClientRegistry,
    private val proxyGroupHandler: ProxyGroupHandler,
    private val processRegistry: ProcessRegistry,
    private val portUtil: PortUtil
) : ProcessRequestHandler {

    private val logger: Logger = LoggerFactory.getLogger(ProxyProcessRequestHandler::class.java)

    override fun requestProcessesOnConnect(clientInfoModel: ClientInfoModel) {
        val responsibleGroups = clientInfoModel.responsibleGroups
            .filter { this.proxyGroupHandler.getGroup(it) != null }
            .map { this.proxyGroupHandler.getGroup(it)!! }
            .toSet()

        for (group in responsibleGroups) {
            val currentlyRunning = this.processRegistry.getRunningProcessCount(group.name)
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
                    count, CloudProcess(
                        groupName = group.name,
                        name = null,
                        uuid = null,
                        ip = ip,
                        type = ProcessType.PROXY,
                        stage = ProcessStage.STARTING,
                        mode = ProcessMode.NONE,
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
                    CloudProcess(
                        groupName = group.name,
                        name = null,
                        uuid = null,
                        ip = ip,
                        type = ProcessType.PROXY,
                        stage = ProcessStage.STARTING,
                        mode = ProcessMode.NONE,
                        minMemory = group.minMemory,
                        maxMemory = group.maxMemory,
                        port = -1,
                        maxPlayers = group.maxPlayers
                    )
                )
            }
        }
    }

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

        this.networkUtil.sendPacket(PacketOutRequestProcess(process), channel)

        clientInfoModel.runningProcesses.add(process.uuid!!)

        this.clientRegistry.updateClient(clientInfoModel)
        this.processRegistry.addProcess(process)
        this.logger.info("Requested process with name: [${process.name}] on client with Name: [${clientName}]")
    }
}