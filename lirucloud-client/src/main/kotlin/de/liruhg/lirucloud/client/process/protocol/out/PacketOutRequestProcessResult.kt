package de.liruhg.lirucloud.client.process.protocol.out

import de.liruhg.lirucloud.library.network.protocol.Packet

data class PacketOutRequestProcessResult(
    private val message: String,
    private val success: Boolean
) : Packet()