package de.liruhg.lirucloud.master.process.proxy.protocol.out

import de.liruhg.lirucloud.library.network.protocol.Packet
import de.liruhg.lirucloud.library.proxy.ProxyInformationModel

data class PacketOutUpdateProxyInformation(
    val proxyInformation: ProxyInformationModel
) : Packet