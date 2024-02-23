package de.liruhg.lirucloud.master.network

import de.liruhg.lirucloud.library.network.protocol.Packet
import de.liruhg.lirucloud.master.LiruCloudMaster
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import org.kodein.di.instance
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class NetworkHandler : SimpleChannelInboundHandler<Packet>() {

    private val logger: Logger = LoggerFactory.getLogger(NetworkHandler::class.java)

    private val networkConnectionRegistry: NetworkConnectionRegistry by LiruCloudMaster.KODEIN.instance()

    override fun channelRead0(channelHandlerContext: ChannelHandlerContext, packet: Packet) {
        packet.handle(channelHandlerContext)
    }

    override fun channelActive(channelHandlerContext: ChannelHandlerContext) {
        this.logger.info(
            "New channel with Id: [${
                channelHandlerContext.channel().id()
            }] connected. Awaiting handshake."
        )
        this.networkConnectionRegistry.registerDanglingConnection(channelHandlerContext.channel().id(), channelHandlerContext.channel())
    }

    override fun channelInactive(channelHandlerContext: ChannelHandlerContext) {
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