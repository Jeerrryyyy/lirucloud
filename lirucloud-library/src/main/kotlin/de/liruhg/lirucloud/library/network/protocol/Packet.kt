package de.liruhg.lirucloud.library.network.protocol

import io.netty.channel.ChannelHandlerContext

interface Packet {

    fun handle(channelHandlerContext: ChannelHandlerContext) {}
}