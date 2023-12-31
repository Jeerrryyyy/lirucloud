package de.liruhg.lirucloud.master.client.protocol.out

import de.liruhg.lirucloud.library.network.protocol.Packet

data class PacketOutClientHandshakeResult(
    val message: String,
    val success: Boolean,
) : Packet