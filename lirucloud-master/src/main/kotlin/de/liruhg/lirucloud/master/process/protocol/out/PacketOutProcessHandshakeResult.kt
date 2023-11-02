package de.liruhg.lirucloud.master.process.protocol.out

import de.liruhg.lirucloud.library.network.protocol.Packet

data class PacketOutProcessHandshakeResult(
    val message: String,
    val success: Boolean,
) : Packet