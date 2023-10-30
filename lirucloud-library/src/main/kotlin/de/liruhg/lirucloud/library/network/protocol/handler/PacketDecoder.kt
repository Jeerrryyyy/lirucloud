package de.liruhg.lirucloud.library.network.protocol.handler

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import de.liruhg.lirucloud.library.network.protocol.PacketRegistry
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufInputStream
import io.netty.buffer.EmptyByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder

class PacketDecoder(
    private val packetRegistry: PacketRegistry
) : ByteToMessageDecoder() {

    private val gson: Gson = GsonBuilder().disableHtmlEscaping().create()

    override fun decode(channelHandlerContext: ChannelHandlerContext, byteBuf: ByteBuf, output: MutableList<Any>) {
        if (byteBuf is EmptyByteBuf) return

        val packetId = byteBuf.readInt()
        val packet = this.packetRegistry.getIncomingPacketById(packetId)
            ?: throw IllegalStateException("Packet with id $packetId is not registered")

        val byteBufInputStream = ByteBufInputStream(byteBuf)

        output.add(this.gson.fromJson(byteBufInputStream.readUTF(), packet::class.java))
    }
}