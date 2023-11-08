package de.liruhg.lirucloud.api.proxy.network.protocol.`in`

import de.liruhg.lirucloud.api.global.runtime.RuntimeVars
import de.liruhg.lirucloud.api.proxy.LiruCloudProxyApi
import de.liruhg.lirucloud.library.network.protocol.Packet
import de.liruhg.lirucloud.library.proxy.ProxyInformationModel
import io.netty.channel.ChannelHandlerContext
import org.kodein.di.instance

class PacketInUpdateProxyInformation : Packet {

    private val runtimeVars: RuntimeVars by LiruCloudProxyApi.KODEIN.instance()

    private lateinit var proxyInformation: ProxyInformationModel

    override fun handle(channelHandlerContext: ChannelHandlerContext) {
        this.runtimeVars.proxyInformation = this.proxyInformation
    }
}