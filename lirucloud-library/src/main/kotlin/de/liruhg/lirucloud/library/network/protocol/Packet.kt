package de.liruhg.lirucloud.library.network.protocol

import io.netty.channel.ChannelHandlerContext
import java.util.*

abstract class Packet(
    var callbackId: UUID = UUID.randomUUID()
) {

    open fun handle(channelHandlerContext: ChannelHandlerContext) {}
}