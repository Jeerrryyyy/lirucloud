package de.liruhg.lirucloud.master.process.protocol.out

import de.liruhg.lirucloud.library.network.protocol.Packet
import de.liruhg.lirucloud.library.process.ProcessMode

data class PacketOutProxyRegisterServer(
    private val uuid: String,
    private val name: String,
    private val mode: ProcessMode,
    private val ip: String,
    private val port: Int
) : Packet