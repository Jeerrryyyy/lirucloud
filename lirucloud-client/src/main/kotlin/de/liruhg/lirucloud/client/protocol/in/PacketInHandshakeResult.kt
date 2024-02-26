package de.liruhg.lirucloud.client.protocol.`in`

import de.liruhg.lirucloud.library.network.protocol.Packet

class PacketInHandshakeResult : Packet() {

    var message: String = ""
    var success: Boolean = false
}