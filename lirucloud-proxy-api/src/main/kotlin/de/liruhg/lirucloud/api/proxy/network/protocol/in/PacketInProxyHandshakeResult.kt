package de.liruhg.lirucloud.api.proxy.network.protocol.`in`

import de.liruhg.lirucloud.library.network.protocol.Packet
import io.netty.channel.ChannelHandlerContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class PacketInProxyHandshakeResult : Packet {

    private val logger: Logger = LoggerFactory.getLogger(PacketInProxyHandshakeResult::class.java)

    private lateinit var message: String
    private var success: Boolean = false

    override fun handle(channelHandlerContext: ChannelHandlerContext) {
        if (!this.success) {
            this.logger.error("Unfortunately the handshake was not successful. Reason: [$message]")
            return
        }

        this.logger.info("Handshake result received with Message: [$message] - Success: [$success]")
    }
}