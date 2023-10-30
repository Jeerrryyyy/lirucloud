package de.liruhg.lirucloud.client.network

import de.liruhg.lirucloud.client.LiruCloudClient
import de.liruhg.lirucloud.client.network.protocol.out.PacketOutClientRequestHandshake
import de.liruhg.lirucloud.client.runtime.RuntimeVars
import de.liruhg.lirucloud.library.network.client.model.ClientInfoModel
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

    private val runtimeVars: RuntimeVars by LiruCloudClient.KODEIN.instance()
    private val networkUtil: NetworkUtil by LiruCloudClient.KODEIN.instance()

    override fun channelRead0(channelHandlerContext: ChannelHandlerContext, packet: Packet) {
        packet.handle(channelHandlerContext)
    }

    override fun channelActive(channelHandlerContext: ChannelHandlerContext) {
        this.logger.info("Channel activated, trying to handshake...")

        this.networkUtil.sendPacket(
            PacketOutClientRequestHandshake(
                this.runtimeVars.clientKey,
                ClientInfoModel(
                    uuid = this.runtimeVars.cloudConfiguration.uuid,
                    name = this.runtimeVars.cloudConfiguration.name,
                    delimiter = this.runtimeVars.cloudConfiguration.delimiter,
                    suffix = this.runtimeVars.cloudConfiguration.suffix,
                    currentOnlineServers = 0,
                    memory = this.runtimeVars.cloudConfiguration.memory,
                    currentMemoryUsage = 0,
                    currentCpuUsage = 0.0,
                    responsibleGroups = this.runtimeVars.cloudConfiguration.responsibleGroups,
                    channel = null
                )
            ), channelHandlerContext.channel()
        )

        runtimeVars.masterChannel = channelHandlerContext.channel()
    }

    override fun channelInactive(channelHandlerContext: ChannelHandlerContext) {
        this.logger.error("Master unfortunately disconnected, trying to shut down gracefully...")
        exitProcess(0)
    }

    @Deprecated("Deprecated in Java")
    override fun exceptionCaught(channelHandlerContext: ChannelHandlerContext, cause: Throwable) {
        this.logger.error(
            "Exception caught in channel with Id: [${
                channelHandlerContext.channel().id()
            }] - Reason: [${cause.message}]"
        )

        cause.printStackTrace()
    }
}