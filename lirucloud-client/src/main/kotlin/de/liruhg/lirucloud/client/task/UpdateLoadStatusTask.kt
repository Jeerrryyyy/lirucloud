package de.liruhg.lirucloud.client.task

import de.liruhg.lirucloud.client.LiruCloudClient
import de.liruhg.lirucloud.client.network.protocol.out.PacketOutClientUpdateLoadStatus
import de.liruhg.lirucloud.client.process.ProcessRegistry
import de.liruhg.lirucloud.client.process.proxy.model.InternalProxyProcess
import de.liruhg.lirucloud.client.process.server.model.InternalServerProcess
import de.liruhg.lirucloud.client.runtime.RuntimeVars
import de.liruhg.lirucloud.library.network.util.NetworkUtil
import de.liruhg.lirucloud.library.util.HardwareUtils
import org.kodein.di.instance
import java.util.*

class UpdateLoadStatusTask : TimerTask() {

    private val runtimeVars: RuntimeVars by LiruCloudClient.KODEIN.instance()
    private val networkUtil: NetworkUtil by LiruCloudClient.KODEIN.instance()
    private val proxyProcessRegistry: ProcessRegistry<InternalProxyProcess> by LiruCloudClient.KODEIN.instance()
    private val serverProcessRegistry: ProcessRegistry<InternalServerProcess> by LiruCloudClient.KODEIN.instance()

    override fun run() {
        val totalProxiesRunning = this.proxyProcessRegistry.getRunningProcessCount()
        val totalServersRunning = this.serverProcessRegistry.getRunningProcessCount()

        this.networkUtil.sendPacket(
            PacketOutClientUpdateLoadStatus(
                currentOnlineServers = totalProxiesRunning + totalServersRunning,
                currentMemoryUsage = HardwareUtils.getRuntimeMemoryUsage(),
                currentCpuUsage = HardwareUtils.getInternalCpuUsage(),
            ),
            this.runtimeVars.masterChannel
        )
    }
}