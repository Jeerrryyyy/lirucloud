package de.liruhg.lirucloud.client.network.protocol.out

import de.liruhg.lirucloud.library.client.ClientInfo
import de.liruhg.lirucloud.library.network.protocol.Packet

data class PacketOutClientRequestHandshake(
    private val clientKey: String,
    private val clientInfo: ClientInfo
) : Packet()