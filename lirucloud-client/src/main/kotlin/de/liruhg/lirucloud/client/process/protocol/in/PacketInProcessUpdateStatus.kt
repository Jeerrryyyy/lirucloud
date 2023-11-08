package de.liruhg.lirucloud.client.process.protocol.`in`

import de.liruhg.lirucloud.client.LiruCloudClient
import de.liruhg.lirucloud.client.process.InternalCloudProcess
import de.liruhg.lirucloud.client.process.ProcessRegistry
import de.liruhg.lirucloud.library.network.protocol.Packet
import de.liruhg.lirucloud.library.process.ProcessStage
import io.netty.channel.ChannelHandlerContext
import org.kodein.di.instance
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class PacketInProcessUpdateStatus : Packet {

    private val logger: Logger = LoggerFactory.getLogger(PacketInProcessUpdateStatus::class.java)

    private val processRegistry: ProcessRegistry by LiruCloudClient.KODEIN.instance()

    private lateinit var uuid: String
    private lateinit var stage: ProcessStage

    override fun handle(channelHandlerContext: ChannelHandlerContext) {
        val internalProxyProcess = this.processRegistry.getProcess(this.uuid)

        if (internalProxyProcess != null) {
            this.handleProcess(internalProxyProcess)
            return
        }

        this.logger.warn("Received status update for unknown process with UUID: [${this.uuid}]")
    }

    private fun handleProcess(internalCloudProcess: InternalCloudProcess) {
        internalCloudProcess.stage = this.stage

        this.processRegistry.updateProcess(internalCloudProcess)

        this.logger.info("Updated process with Name: [${internalCloudProcess.name}] - UUID: [${this.uuid}] - ProcessStage: [${this.stage}]")
    }
}