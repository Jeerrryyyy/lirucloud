package de.liruhg.lirucloud.library.network.util

import de.liruhg.lirucloud.library.network.protocol.Packet
import io.netty.channel.Channel
import java.util.*

class NetworkUtil {

    val callbacks: MutableMap<UUID, (Packet) -> Unit> = mutableMapOf()

    fun sendPacket(packet: Packet, channel: Channel) {
        channel.writeAndFlush(packet)
    }

    fun sendResponse(requestPacket: Packet, responsePacket: Packet, channel: Channel) {
        responsePacket.callbackId = requestPacket.callbackId
        this.sendPacket(responsePacket, channel)
    }

    inline fun <reified T : Packet> sendPacket(packet: Packet, channel: Channel, crossinline callback: (T) -> Unit) {
        this.callbacks[packet.callbackId] = {
            if (it is T) callback(it)
            else throw IllegalStateException("Received unwanted response packet with CallbackId: [${packet.callbackId}]")
        }

        this.sendPacket(packet, channel)
    }

    fun handleCallback(id: UUID, response: Packet) {
        this.callbacks.remove(id)?.invoke(response)
    }

    fun isCallbackPacket(uuid: UUID): Boolean {
        return this.callbacks.containsKey(uuid)
    }
}