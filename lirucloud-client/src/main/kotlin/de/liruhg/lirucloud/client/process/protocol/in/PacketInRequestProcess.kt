package de.liruhg.lirucloud.client.process.protocol.`in`

import de.liruhg.lirucloud.client.LiruCloudClient
import de.liruhg.lirucloud.client.process.protocol.out.PacketOutRequestProcessResult
import de.liruhg.lirucloud.client.process.proxy.ProxyProcessRequestHandler
import de.liruhg.lirucloud.client.process.server.ServerProcessRequestHandler
import de.liruhg.lirucloud.library.network.protocol.Packet
import de.liruhg.lirucloud.library.network.util.NetworkUtil
import de.liruhg.lirucloud.library.process.CloudProcess
import de.liruhg.lirucloud.library.process.ProcessType
import io.netty.channel.ChannelHandlerContext
import org.kodein.di.instance

class PacketInRequestProcess : Packet() {

    private val networkUtil: NetworkUtil by LiruCloudClient.KODEIN.instance()
    private val serverProcessRequestHandler: ServerProcessRequestHandler by LiruCloudClient.KODEIN.instance()
    private val proxyProcessRequestHandler: ProxyProcessRequestHandler by LiruCloudClient.KODEIN.instance()

    private lateinit var cloudProcess: CloudProcess

    override fun handle(channelHandlerContext: ChannelHandlerContext) {
        val result = when (this.cloudProcess.type) {
            ProcessType.SERVER -> this.serverProcessRequestHandler.handle(this.cloudProcess)
            ProcessType.PROXY -> this.proxyProcessRequestHandler.handle(this.cloudProcess)
        }

        this.networkUtil.sendResponse(
            this,
            PacketOutRequestProcessResult(result.first, result.second),
            channelHandlerContext.channel()
        )
    }
}