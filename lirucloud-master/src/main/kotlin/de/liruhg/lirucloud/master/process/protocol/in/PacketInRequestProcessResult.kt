package de.liruhg.lirucloud.master.process.protocol.`in`

import de.liruhg.lirucloud.library.network.protocol.Packet

class PacketInRequestProcessResult : Packet() {

    lateinit var message: String
    val success: Boolean = false
}