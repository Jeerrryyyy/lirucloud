package de.liruhg.lirucloud.master.process.registry

import de.liruhg.lirucloud.library.process.CloudProcess
import io.netty.channel.Channel
import java.util.concurrent.ConcurrentHashMap

class ProcessRegistry {

    val processes: ConcurrentHashMap<String, CloudProcess> = ConcurrentHashMap()

    fun addProcess(process: CloudProcess) {
        this.processes[process.uuid!!] = process
    }

    fun removeProcess(process: CloudProcess) {
        this.processes.remove(process.uuid)
    }

    fun containsProcess(uuid: String): Boolean {
        return this.processes.containsKey(uuid)
    }

    fun getProcess(uuid: String): CloudProcess? {
        return this.processes.getOrDefault(uuid, null)
    }

    fun getRunningProcessCount(name: String): Int {
        return this.processes.values.count { it.groupName == name }
    }

    fun getRunningProcessCount(): Int {
        return this.processes.size
    }

    fun getProcessByChannel(channel: Channel): CloudProcess? {
        return processes.values.firstOrNull { it.channel == channel }
    }
}