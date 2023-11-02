package de.liruhg.lirucloud.api.global.network

import de.liruhg.lirucloud.api.global.GlobalPluginDi
import de.liruhg.lirucloud.api.global.network.protocol.out.PacketOutProcessRequestHandshake
import de.liruhg.lirucloud.api.global.runtime.RuntimeVars
import de.liruhg.lirucloud.library.network.protocol.Packet
import de.liruhg.lirucloud.library.network.util.NetworkUtil
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import org.kodein.di.instance
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class NetworkHandler : SimpleChannelInboundHandler<Packet>() {

    private val logger: Logger = LoggerFactory.getLogger(NetworkHandler::class.java)

    private val networkUtil: NetworkUtil by GlobalPluginDi.KODEIN.instance()
    private val runtimeVars: RuntimeVars by GlobalPluginDi.KODEIN.instance()

    override fun channelRead0(channelHandlerContext: ChannelHandlerContext, packet: Packet) {
        packet.handle(channelHandlerContext)
    }

    override fun channelActive(channelHandlerContext: ChannelHandlerContext) {
        this.logger.info("Channel activated, trying to handshake...")

        this.networkUtil.sendPacket(
            PacketOutProcessRequestHandshake(
                this.runtimeVars.pluginConfiguration.processInformation.uuid,
                this.runtimeVars.clientKey
            ),
            channelHandlerContext.channel()
        )

        this.runtimeVars.masterChannel = channelHandlerContext.channel()
    }

    override fun channelInactive(channelHandlerContext: ChannelHandlerContext) {
        this.logger.error("Channel disconnected, doing nothing but server will not be able to respond to packets...")
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