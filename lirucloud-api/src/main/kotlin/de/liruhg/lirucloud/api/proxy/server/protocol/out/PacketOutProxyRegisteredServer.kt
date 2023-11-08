package de.liruhg.lirucloud.api.proxy.server.protocol.out

import de.liruhg.lirucloud.library.network.protocol.Packet

data class PacketOutProxyRegisteredServer(
    private val uuid: String,
) : Packet