package de.liruhg.lirucloud.api.proxy.server.protocol.`in`

import de.liruhg.lirucloud.api.proxy.LiruCloudProxyApi
import de.liruhg.lirucloud.api.proxy.server.ServerRegistry
import de.liruhg.lirucloud.library.network.protocol.Packet
import de.liruhg.lirucloud.library.process.ProcessMode
import io.netty.channel.ChannelHandlerContext
import org.kodein.di.instance

class PacketInProxyRegisterServer : Packet {

    private val serverRegistry: ServerRegistry by LiruCloudProxyApi.KODEIN.instance()

    private lateinit var uuid: String
    private lateinit var name: String
    private lateinit var mode: ProcessMode
    private lateinit var ip: String
    private var port: Int = 0

    override fun handle(channelHandlerContext: ChannelHandlerContext) {
        this.serverRegistry.registerServer(this.uuid, this.name, this.mode, this.ip, this.port)
    }
}