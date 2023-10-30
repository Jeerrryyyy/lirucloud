package de.liruhg.lirucloud.library.network.protocol.handler

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import de.liruhg.lirucloud.library.network.protocol.Packet
import de.liruhg.lirucloud.library.network.protocol.PacketRegistry
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufOutputStream
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

class PacketEncoder(
    private val packetRegistry: PacketRegistry
) : MessageToByteEncoder<Packet>() {

    private val gson: Gson = GsonBuilder().disableHtmlEscaping().create()

    override fun encode(channelHandlerContext: ChannelHandlerContext, packet: Packet, byteBuf: ByteBuf) {
        val packetId = this.packetRegistry.getIdByOutgoingPacket(packet)

        if (packetId == -1) {
            throw IllegalStateException("Packet with class ${packet::class.java} is not registered")
        } else {
            byteBuf.writeInt(packetId)

            val byteBufOutputStream = ByteBufOutputStream(byteBuf)
            byteBufOutputStream.writeUTF(this.gson.toJson(packet))
        }
    }
}