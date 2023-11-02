package de.liruhg.lirucloud.master.process.proxy.protocol.out

import de.liruhg.lirucloud.library.network.protocol.Packet
import de.liruhg.lirucloud.library.process.model.ProxyProcess

data class PacketOutRequestProxyProcess(
    private val proxyProcess: ProxyProcess
) : Packet