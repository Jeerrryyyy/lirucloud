package de.liruhg.lirucloud.client.network.protocol.out

import de.liruhg.lirucloud.library.network.client.model.ClientInfoModel
import de.liruhg.lirucloud.library.network.protocol.Packet

data class PacketOutClientRequestHandshake(
    private val clientKey: String,
    private val clientInfoModel: ClientInfoModel
) : Packet