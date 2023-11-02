package de.liruhg.lirucloud.master.process.server.handler

import de.liruhg.lirucloud.library.network.client.model.ClientInfoModel
import de.liruhg.lirucloud.library.network.util.NetworkUtil
import de.liruhg.lirucloud.library.process.ProcessStage
import de.liruhg.lirucloud.library.process.ProcessType
import de.liruhg.lirucloud.library.process.model.ServerProcess
import de.liruhg.lirucloud.library.util.PortUtils
import de.liruhg.lirucloud.master.client.ClientRegistry
import de.liruhg.lirucloud.master.group.server.ServerGroupHandler
import de.liruhg.lirucloud.master.process.ProcessRequestHandler
import de.liruhg.lirucloud.master.process.protocol.out.PacketOutRequestServerProcess
import de.liruhg.lirucloud.master.process.server.registry.ServerProcessRegistry
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

class ServerProcessRequestHandler(
    private val networkUtil: NetworkUtil,
    private val serverProcessRegistry: ServerProcessRegistry,
    private val clientRegistry: ClientRegistry,
    private val serverGroupHandler: ServerGroupHandler
) : ProcessRequestHandler<ServerProcess> {

    private val logger: Logger = LoggerFactory.getLogger(ServerProcessRequestHandler::class.java)

    override fun requestProcessesOnConnect(clientInfoModel: ClientInfoModel) {
        val responsibleGroups = clientInfoModel.responsibleGroups
            .filter { this.serverGroupHandler.getGroup(it) != null }
            .map { this.serverGroupHandler.getGroup(it)!! }
            .toSet()

        for (group in responsibleGroups) {
            val currentlyRunning = this.serverProcessRegistry.getRunningProcessCount(group.name)
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
                    count, ServerProcess(
                        groupName = group.name,
                        name = null,
                        uuid = null,
                        ip = ip,
                        type = ProcessType.SERVER,
                        stage = ProcessStage.STARTING,
                        minMemory = group.minMemory,
                        maxMemory = group.maxMemory,
                        port = -1,
                        maxPlayers = group.maxPlayers,
                        mode = group.mode,
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
                    ServerProcess(
                        groupName = group.name,
                        name = null,
                        uuid = null,
                        ip = ip,
                        type = ProcessType.SERVER,
                        stage = ProcessStage.STARTING,
                        minMemory = group.minMemory,
                        maxMemory = group.maxMemory,
                        port = -1,
                        maxPlayers = group.maxPlayers,
                        mode = group.mode,
                    )
                )
            }
        }
    }

    override fun requestProcesses(count: Int, process: ServerProcess) {
        (1..count).forEach { _ ->
            this.requestProcess(process)
        }
    }

    override fun requestProcess(process: ServerProcess) {
        val groupName = process.groupName
        val currentlyRunning = this.serverProcessRegistry.getRunningProcessCount(groupName)

        process.uuid = UUID.randomUUID().toString()
        process.port = PortUtils.getNextFreePort(60000)
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

        this.networkUtil.sendPacket(PacketOutRequestServerProcess(process), channel)

        clientInfoModel.runningProcesses.add(process.uuid!!)

        this.clientRegistry.updateClient(clientInfoModel)
        this.serverProcessRegistry.registerDanglingProcess(process)
        this.logger.info("Requested process with name: [${process.name}] on client with Name: [${clientName}]")
    }
}