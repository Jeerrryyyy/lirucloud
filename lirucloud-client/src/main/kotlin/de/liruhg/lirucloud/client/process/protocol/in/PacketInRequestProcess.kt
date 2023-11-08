package de.liruhg.lirucloud.client.process.protocol.`in`

import de.liruhg.lirucloud.client.LiruCloudClient
import de.liruhg.lirucloud.client.process.proxy.handler.ProxyProcessRequestHandler
import de.liruhg.lirucloud.client.process.server.handler.ServerProcessRequestHandler
import de.liruhg.lirucloud.library.network.protocol.Packet
import de.liruhg.lirucloud.library.process.CloudProcess
import de.liruhg.lirucloud.library.process.ProcessType
import io.netty.channel.ChannelHandlerContext
import org.kodein.di.instance

class PacketInRequestProcess : Packet {

    private val serverProcessRequestHandler: ServerProcessRequestHandler by LiruCloudClient.KODEIN.instance()
    private val proxyProcessRequestHandler: ProxyProcessRequestHandler by LiruCloudClient.KODEIN.instance()

    private lateinit var cloudProcess: CloudProcess

    override fun handle(channelHandlerContext: ChannelHandlerContext) {
        when (this.cloudProcess.type) {
            ProcessType.SERVER -> this.serverProcessRequestHandler.handle(this.cloudProcess)
            ProcessType.PROXY -> this.proxyProcessRequestHandler.handle(this.cloudProcess)
        }
    }
}