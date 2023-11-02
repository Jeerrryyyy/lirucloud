package de.liruhg.lirucloud.client.process.protocol.`in`

import de.liruhg.lirucloud.client.LiruCloudClient
import de.liruhg.lirucloud.client.process.ProcessRegistry
import de.liruhg.lirucloud.client.process.proxy.model.InternalProxyProcess
import de.liruhg.lirucloud.library.network.protocol.Packet
import de.liruhg.lirucloud.library.process.ProcessStage
import io.netty.channel.ChannelHandlerContext
import org.kodein.di.instance
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class PacketInProxyUpdateStatus : Packet {

    private val logger: Logger = LoggerFactory.getLogger(PacketInProxyUpdateStatus::class.java)

    private val proxyProcessRegistry: ProcessRegistry<InternalProxyProcess> by LiruCloudClient.KODEIN.instance()

    private lateinit var uuid: String
    private lateinit var stage: ProcessStage

    override fun handle(channelHandlerContext: ChannelHandlerContext) {
        val internalProxyProcess = this.proxyProcessRegistry.getProcess(this.uuid)

        if (internalProxyProcess == null) {
            this.logger.warn("Received status update for unknown process with UUID: [${this.uuid}]")
            return
        }

        internalProxyProcess.stage = this.stage

        this.proxyProcessRegistry.updateProcess(internalProxyProcess)

        this.logger.info("Updated process with UUID: [${this.uuid}] - ProcessStage: [${this.stage}]")
    }
}