package de.liruhg.lirucloud.client.network.protocol.`in`

import de.liruhg.lirucloud.library.network.protocol.Packet

class PacketInClientHandshakeResult : Packet() {

    lateinit var message: String
    val success: Boolean = false
}