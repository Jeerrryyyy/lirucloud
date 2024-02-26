package de.liruhg.lirucloud.master.client.protocol.out

import de.liruhg.lirucloud.library.network.protocol.Packet

class PacketOutHandshakeResult(
     private val message: String,
     private val success: Boolean,
) : Packet()