package de.liruhg.lirucloud.master.process.server

import de.liruhg.lirucloud.library.network.util.NetworkUtil
import de.liruhg.lirucloud.library.process.CloudProcess
import de.liruhg.lirucloud.library.util.PortUtil
import de.liruhg.lirucloud.master.client.ClientRegistry
import de.liruhg.lirucloud.master.process.ProcessRegistry
import de.liruhg.lirucloud.master.process.ProcessRequestHandler
import de.liruhg.lirucloud.master.process.protocol.`in`.PacketInRequestProcessResult
import de.liruhg.lirucloud.master.process.protocol.out.PacketOutRequestProcess
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

class ServerProcessRequestHandler(
    private val networkUtil: NetworkUtil,
    private val clientRegistry: ClientRegistry,
    private val processRegistry: ProcessRegistry,
    private val portUtil: PortUtil
) : ProcessRequestHandler {

    private val logger: Logger = LoggerFactory.getLogger(ServerProcessRequestHandler::class.java)

    override fun requestProcesses(count: Int, process: CloudProcess) {
        (1..count).forEach { _ ->
            this.requestProcess(process)
        }
    }

    override fun requestProcess(process: CloudProcess) {
        val groupName = process.groupName
        val currentlyRunning = this.processRegistry.getRunningProcessCount(groupName)

        process.uuid = UUID.randomUUID().toString()

        process.port = this.portUtil.getNextFreePort(60000)
        this.portUtil.blockPort(process.port)

        process.name =
            "$groupName-${if ((currentlyRunning + 1) >= 10) "${currentlyRunning + 1}" else "0${currentlyRunning + 1}"}"

        val clientInfoModel = this.clientRegistry.getLeastUsedClient(groupName)

        if (clientInfoModel == null) {
            this.logger.error("No client found for group with name: [$groupName]")
            return
        }

        val clientName = "${clientInfoModel.name}${clientInfoModel.delimiter}${clientInfoModel.suffix}"
        val channel = clientInfoModel.channel

        if (channel == null) {
            this.logger.error("Channel of client is null. name: [${clientName}]")
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
            this.logger.info("Requested process with name: [${process.name}] on client with name: [${clientName}]")
        }
    }
}