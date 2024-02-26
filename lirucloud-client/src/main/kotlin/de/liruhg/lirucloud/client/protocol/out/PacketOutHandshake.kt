package de.liruhg.lirucloud.client.protocol.out

import de.liruhg.lirucloud.library.client.ClientInfoModel
import de.liruhg.lirucloud.library.network.protocol.Packet

class PacketOutHandshake(
    private val clientInfoModel: ClientInfoModel
) : Packet()