package de.liruhg.lirucloud.master.client.protocol.`in`

import de.liruhg.lirucloud.library.network.protocol.Packet
import de.liruhg.lirucloud.master.LiruCloudMaster
import de.liruhg.lirucloud.master.client.ClientRegistry
import de.liruhg.lirucloud.master.process.proxy.handler.ProxyProcessRequestHandler
import de.liruhg.lirucloud.master.process.server.handler.ServerProcessRequestHandler
import io.netty.channel.ChannelHandlerContext
import org.kodein.di.instance
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class PacketInClientRequestProcesses : Packet {

    private val logger: Logger = LoggerFactory.getLogger(PacketInClientRequestProcesses::class.java)

    private val clientRegistry: ClientRegistry by LiruCloudMaster.KODEIN.instance()

    private val proxyProcessRequestHandler: ProxyProcessRequestHandler by LiruCloudMaster.KODEIN.instance()
    private val serverProcessRequestHandler: ServerProcessRequestHandler by LiruCloudMaster.KODEIN.instance()

    override fun handle(channelHandlerContext: ChannelHandlerContext) {
        val channel = channelHandlerContext.channel()
        val client = this.clientRegistry.getClientByChannel(channel)

        if (client == null) {
            this.logger.warn("Client requested servers but was not registered. Closing connection...")

            channel.close()
            return
        }

        this.proxyProcessRequestHandler.requestProcessesOnConnect(client)
        this.serverProcessRequestHandler.requestProcessesOnConnect(client)
    }
}