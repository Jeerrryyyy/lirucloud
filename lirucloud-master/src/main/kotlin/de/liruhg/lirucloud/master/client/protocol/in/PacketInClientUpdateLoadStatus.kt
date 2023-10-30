package de.liruhg.lirucloud.master.client.protocol.`in`

import de.liruhg.lirucloud.library.network.protocol.Packet
import de.liruhg.lirucloud.master.LiruCloudMaster
import de.liruhg.lirucloud.master.client.ClientRegistry
import io.netty.channel.ChannelHandlerContext
import org.kodein.di.instance
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class PacketInClientUpdateLoadStatus : Packet {

    private val logger: Logger = LoggerFactory.getLogger(PacketInClientUpdateLoadStatus::class.java)

    private val clientRegistry: ClientRegistry by LiruCloudMaster.KODEIN.instance()

    private var currentOnlineServers: Int = 0
    private var currentMemoryUsage: Long = 0
    private var currentCpuUsage: Double = 0.0

    override fun handle(channelHandlerContext: ChannelHandlerContext) {
        val channel = channelHandlerContext.channel()
        val client = this.clientRegistry.getClientByChannel(channel)

        if (client == null) {
            this.logger.warn("Client requested servers but was not registered. Closing connection...")

            channel.close()
            return
        }

        client.currentOnlineServers = this.currentOnlineServers
        client.currentMemoryUsage = this.currentMemoryUsage
        client.currentCpuUsage = this.currentCpuUsage

        this.clientRegistry.updateClient(client)
    }
}