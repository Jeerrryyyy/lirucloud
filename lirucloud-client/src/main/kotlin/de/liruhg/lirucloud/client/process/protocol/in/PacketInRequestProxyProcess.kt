package de.liruhg.lirucloud.client.process.protocol.`in`

import de.liruhg.lirucloud.client.LiruCloudClient
import de.liruhg.lirucloud.client.process.proxy.ProxyProcessRequestHandler
import de.liruhg.lirucloud.library.network.protocol.Packet
import de.liruhg.lirucloud.library.process.model.ProxyProcess
import io.netty.channel.ChannelHandlerContext
import org.kodein.di.instance

class PacketInRequestProxyProcess : Packet {

    private val proxyProcessRequestHandler: ProxyProcessRequestHandler by LiruCloudClient.KODEIN.instance()

    private lateinit var proxyProcess: ProxyProcess

    override fun handle(channelHandlerContext: ChannelHandlerContext) {
        this.proxyProcessRequestHandler.handle(this.proxyProcess)
    }
}