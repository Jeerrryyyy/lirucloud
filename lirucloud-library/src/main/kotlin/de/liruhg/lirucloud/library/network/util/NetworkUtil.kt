package de.liruhg.lirucloud.library.network.util

import de.liruhg.lirucloud.library.network.protocol.Packet
import io.netty.channel.Channel

class NetworkUtil {

    fun sendPacket(packet: Packet, channel: Channel) {
        channel.writeAndFlush(packet)
    }
}