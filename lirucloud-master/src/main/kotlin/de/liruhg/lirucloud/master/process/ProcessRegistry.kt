package de.liruhg.lirucloud.master.process

import de.liruhg.lirucloud.library.process.AbstractProcess
import io.netty.channel.Channel

open class ProcessRegistry<T : AbstractProcess> {

    val danglingProcesses: MutableMap<String, T> = mutableMapOf()
    val processes: MutableMap<String, T> = mutableMapOf()

    fun registerDanglingProcess(process: T) {
        if (this.danglingProcesses.containsKey(process.uuid!!)) return

        this.danglingProcesses[process.uuid!!] = process
    }

    fun unregisterDanglingProcess(process: T) {
        this.danglingProcesses.remove(process.uuid!!)
    }

    fun getDanglingProcess(uuid: String): T? {
        return this.danglingProcesses[uuid]
    }

    fun registerProcess(process: T) {
        if (this.processes.containsKey(process.uuid!!)) return

        this.processes[process.uuid!!] = process
    }

    fun unregisterProcess(process: T) {
        this.processes.remove(process.uuid!!)
    }

    fun getProcess(uuid: String): T? {
        return this.processes[uuid]
    }

    fun getRunningProcessCount(name: String): Int {
        return this.processes.values.stream().filter { it.groupName == name }.count().toInt()
    }

    fun getRunningProcessCount(): Int {
        return this.processes.size
    }

    fun getProcessByChannel(channel: Channel): T? {
        return processes.values.firstOrNull { it.channel == channel }
    }

    fun getDanglingProcessCount(): Int {
        return this.danglingProcesses.size
    }
}
