package de.liruhg.lirucloud.client.network.protocol.`in`

import de.liruhg.lirucloud.client.LiruCloudClient
import de.liruhg.lirucloud.client.network.protocol.out.PacketOutClientRequestServers
import de.liruhg.lirucloud.client.runtime.RuntimeVars
import de.liruhg.lirucloud.library.network.protocol.Packet
import de.liruhg.lirucloud.library.network.util.NetworkUtil
import io.netty.channel.ChannelHandlerContext
import org.kodein.di.instance
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class PacketInClientHandshakeResult : Packet {

    private val logger: Logger = LoggerFactory.getLogger(PacketInClientHandshakeResult::class.java)

    private val networkUtil: NetworkUtil by LiruCloudClient.KODEIN.instance()
    private val runtimeVars: RuntimeVars by LiruCloudClient.KODEIN.instance()

    private lateinit var message: String
    private var success: Boolean = false

    override fun handle(channelHandlerContext: ChannelHandlerContext) {
        if (!this.success) {
            this.logger.error("Unfortunately the handshake was not successful. Reason: [$message]")
            return
        }

        this.logger.info("Handshake result received with Message: [$message] - Success: [$success]")

        this.networkUtil.sendPacket(PacketOutClientRequestServers(), this.runtimeVars.masterChannel)
    }
}