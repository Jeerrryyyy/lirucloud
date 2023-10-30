package de.liruhg.lirucloud.client.task

import de.liruhg.lirucloud.client.LiruCloudClient
import de.liruhg.lirucloud.client.network.protocol.out.PacketOutClientUpdateLoadStatus
import de.liruhg.lirucloud.client.runtime.RuntimeVars
import de.liruhg.lirucloud.library.network.util.NetworkUtil
import de.liruhg.lirucloud.library.util.HardwareUtils
import org.kodein.di.instance
import java.util.*

class UpdateLoadStatusTask : TimerTask() {

    private val runtimeVars: RuntimeVars by LiruCloudClient.KODEIN.instance()
    private val networkUtil: NetworkUtil by LiruCloudClient.KODEIN.instance()

    override fun run() {
        this.networkUtil.sendPacket(
            PacketOutClientUpdateLoadStatus(
                currentOnlineServers = 0, // TODO: fetch from registry
                currentMemoryUsage = HardwareUtils.getRuntimeMemoryUsage(),
                currentCpuUsage = HardwareUtils.getInternalCpuUsage(),
            ),
            this.runtimeVars.masterChannel
        )
    }
}