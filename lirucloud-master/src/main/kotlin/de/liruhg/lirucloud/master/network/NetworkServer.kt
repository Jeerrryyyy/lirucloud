package de.liruhg.lirucloud.master.network

import de.liruhg.lirucloud.library.network.helper.NettyHelper
import de.liruhg.lirucloud.library.network.protocol.PacketRegistry
import de.liruhg.lirucloud.library.network.protocol.handler.PacketDecoder
import de.liruhg.lirucloud.library.network.protocol.handler.PacketEncoder
import de.liruhg.lirucloud.library.network.server.AbstractNetworkServer
import de.liruhg.lirucloud.library.thread.ThreadPool
import io.netty.channel.Channel
import io.netty.handler.codec.LengthFieldBasedFrameDecoder
import io.netty.handler.codec.LengthFieldPrepender
import io.netty.handler.ssl.SslContext

class NetworkServer(
    nettyHelper: NettyHelper,
    threadPool: ThreadPool,
    private val packetRegistry: PacketRegistry
) : AbstractNetworkServer(nettyHelper, threadPool) {

    override fun preparePipeline(sslContext: SslContext?, channel: Channel) {
        if (sslContext != null) {
            channel.pipeline().addLast(sslContext.newHandler(channel.alloc()))
        }

        channel.pipeline()
            .addLast(LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4))
            .addLast(PacketDecoder(packetRegistry))
            .addLast(LengthFieldPrepender(4))
            .addLast(PacketEncoder(packetRegistry))
            .addLast(NetworkHandler())
    }
}