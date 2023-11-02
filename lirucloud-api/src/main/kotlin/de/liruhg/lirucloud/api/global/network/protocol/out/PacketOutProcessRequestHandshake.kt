package de.liruhg.lirucloud.api.global.network.protocol.out

import de.liruhg.lirucloud.library.network.protocol.Packet

data class PacketOutProcessRequestHandshake(
    val uuid: String,
    val clientKey: String
) : Packet