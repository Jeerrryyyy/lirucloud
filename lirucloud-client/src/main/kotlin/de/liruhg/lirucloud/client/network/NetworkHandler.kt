package de.liruhg.lirucloud.client.network

import de.liruhg.lirucloud.client.LiruCloudClient
import de.liruhg.lirucloud.client.network.protocol.`in`.PacketInClientHandshakeResult
import de.liruhg.lirucloud.client.network.protocol.out.PacketOutClientRequestHandshake
import de.liruhg.lirucloud.client.store.Store
import de.liruhg.lirucloud.library.client.ClientInfo
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
    private val store: Store by LiruCloudClient.KODEIN.instance()

    override fun channelRead0(channelHandlerContext: ChannelHandlerContext, packet: Packet) {
        packet.handle(channelHandlerContext)
    }

    override fun channelActive(channelHandlerContext: ChannelHandlerContext) {
        this.logger.info("Channel activated, trying to handshake...")

        this.networkUtil.sendPacket<PacketInClientHandshakeResult>(
            PacketOutClientRequestHandshake(
                this.store.clientKey,
                ClientInfo(
                    uuid = this.store.cloudConfiguration.uuid,
                    name = this.store.cloudConfiguration.name,
                    delimiter = this.store.cloudConfiguration.delimiter,
                    suffix = this.store.cloudConfiguration.suffix,
                    currentOnlineServers = 0,
                    memory = this.store.cloudConfiguration.memory,
                    currentMemoryUsage = 0,
                    currentCpuUsage = 0.0,
                    responsibleGroups = this.store.cloudConfiguration.responsibleGroups,
                    channel = null
                )
            ),
            channelHandlerContext.channel()
        ) {
            if (!it.success) {
                this.logger.error("Unfortunately the handshake was not successful. Reason: [${it.message}]")
                return@sendPacket
            }

            this.logger.info("Handshake result received with Message: [${it.message}] - Success: [${it.success}]")
        }

        this.store.masterChannel = channelHandlerContext.channel()
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