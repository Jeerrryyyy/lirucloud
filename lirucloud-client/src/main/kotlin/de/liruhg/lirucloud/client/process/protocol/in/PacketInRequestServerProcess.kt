package de.liruhg.lirucloud.client.process.protocol.`in`

import de.liruhg.lirucloud.client.LiruCloudClient
import de.liruhg.lirucloud.client.process.server.handler.ServerProcessRequestHandler
import de.liruhg.lirucloud.library.network.protocol.Packet
import de.liruhg.lirucloud.library.process.model.ServerProcess
import io.netty.channel.ChannelHandlerContext
import org.kodein.di.instance

class PacketInRequestServerProcess : Packet {

    private val serverProcessRequestHandler: ServerProcessRequestHandler by LiruCloudClient.KODEIN.instance()

    private lateinit var serverProcess: ServerProcess

    override fun handle(channelHandlerContext: ChannelHandlerContext) {
        this.serverProcessRequestHandler.handle(this.serverProcess)
    }
}