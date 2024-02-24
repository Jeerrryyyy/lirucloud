package de.liruhg.lirucloud.master.protocol.out

import de.liruhg.lirucloud.library.network.protocol.Packet

class PacketOutHandshakeResult(
    private val message: String
) : Packet()