package de.liruhg.lirucloud.client.protocol.`in`

import de.liruhg.lirucloud.library.network.protocol.Packet

class PacketInHandshakeResult : Packet() {

    lateinit var message: String
}
