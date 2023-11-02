package de.liruhg.lirucloud.client.process

abstract class ProcessRegistry<T : InternalCloudProcess> {

    val processes: MutableMap<String, T> = mutableMapOf()

    fun registerProcess(process: T) {
        if (this.processes.containsKey(process.uuid)) return

        this.processes[process.uuid!!] = process
    }

    fun unregisterProcess(process: T) {
        this.processes.remove(process.uuid)
    }

    fun updateProcess(process: T) {
        this.unregisterProcess(process)
        this.registerProcess(process)
    }

    fun getProcess(uuid: String): T? {
        return this.processes[uuid]
    }

    fun getRunningProcessCount(processName: String): Int {
        return this.processes.values.filter { it.name == processName }.size
    }

    fun getRunningProcessCount(): Int {
        return this.processes.size
    }

    fun getTotalMemoryUsage(): Int {
        return this.processes.values.sumOf { it.maxMemory }
    }

    abstract fun shutdownProcesses()
}