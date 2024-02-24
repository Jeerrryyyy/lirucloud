package de.liruhg.lirucloud.client.network

import de.liruhg.lirucloud.client.LiruCloudClient
import de.liruhg.lirucloud.client.protocol.`in`.PacketInHandshakeResult
import de.liruhg.lirucloud.client.protocol.out.PacketOutHandshake
import de.liruhg.lirucloud.library.network.protocol.Packet
import de.liruhg.lirucloud.library.network.util.NetworkUtil
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import org.kodein.di.instance
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.system.exitProcess

class NetworkHandler : SimpleChannelInboundHandler<Packet>() {

    private val logger: Logger = LoggerFactory.getLogger(NetworkHandler::class.java)

    private val networkUtil: NetworkUtil by LiruCloudClient.KODEIN.instance()

    override fun channelRead0(channelHandlerContext: ChannelHandlerContext, packet: Packet) {
        packet.handle(channelHandlerContext)
    }

    override fun channelActive(channelHandlerContext: ChannelHandlerContext) {
        this.logger.info("Channel activated, trying to handshake...")

        this.networkUtil.sendPacket<PacketInHandshakeResult>(PacketOutHandshake(), channelHandlerContext.channel()) {
            println(it.message)
        }
    }

    override fun channelInactive(channelHandlerContext: ChannelHandlerContext) {
        this.logger.error("Channel disconnected, trying to shut down gracefully...")
        exitProcess(0)
    }

    @Deprecated("Deprecated in Java")
    override fun exceptionCaught(channelHandlerContext: ChannelHandlerContext, cause: Throwable) {
        this.logger.error(
            "Exception caught in channel with Id: [${
                channelHandlerContext.channel().id()
            }] - Reason: [${cause.message}]"
        )
    }
}