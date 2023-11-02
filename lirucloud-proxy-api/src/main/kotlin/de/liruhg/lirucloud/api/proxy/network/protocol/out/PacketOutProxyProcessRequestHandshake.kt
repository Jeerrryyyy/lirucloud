package de.liruhg.lirucloud.api.proxy.network.protocol.out

import de.liruhg.lirucloud.library.network.protocol.Packet

data class PacketOutProxyProcessRequestHandshake(
    val uuid: String,
    val clientKey: String
) : Packet