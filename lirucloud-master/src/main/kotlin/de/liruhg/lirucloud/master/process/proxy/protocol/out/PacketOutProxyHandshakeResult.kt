package de.liruhg.lirucloud.master.process.proxy.protocol.out

import de.liruhg.lirucloud.library.network.protocol.Packet

data class PacketOutProxyHandshakeResult(
    val message: String,
    val success: Boolean,
) : Packet