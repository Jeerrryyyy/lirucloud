package de.liruhg.lirucloud.master.client.protocol.`in`

import de.liruhg.lirucloud.library.client.ClientInfoModel
import de.liruhg.lirucloud.library.network.protocol.Packet
import de.liruhg.lirucloud.library.network.util.NetworkUtil
import de.liruhg.lirucloud.master.LiruCloudMaster
import io.netty.channel.ChannelHandlerContext
import org.kodein.di.instance

class PacketInHandshake : Packet() {

    private val networkUtil: NetworkUtil by LiruCloudMaster.KODEIN.instance()

    private lateinit var clientInfoModel: ClientInfoModel

    override fun handle(channelHandlerContext: ChannelHandlerContext) {
        val channel = channelHandlerContext.channel()

        // TODO: Validate client and send back response
    }
}