package de.liruhg.lirucloud.master.protocol.`in`

import de.liruhg.lirucloud.library.network.protocol.Packet
import de.liruhg.lirucloud.library.network.util.NetworkUtil
import de.liruhg.lirucloud.master.LiruCloudMaster
import de.liruhg.lirucloud.master.protocol.out.PacketOutHandshakeResult
import io.netty.channel.ChannelHandlerContext
import org.kodein.di.instance

class PacketInHandshake : Packet() {

    private val networkUtil: NetworkUtil by LiruCloudMaster.KODEIN.instance()

    override fun handle(channelHandlerContext: ChannelHandlerContext) {
        this.networkUtil.sendResponse(this, PacketOutHandshakeResult("done"), channelHandlerContext.channel())
    }
}