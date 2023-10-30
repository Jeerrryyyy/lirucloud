package de.liruhg.lirucloud.master.process

import de.liruhg.lirucloud.library.process.AbstractProcess

open class ProcessRegistry<T : AbstractProcess> {

    val processes: MutableMap<String, T> = mutableMapOf()

    fun registerProcess(process: T) {
        if (this.processes.containsKey(process.uuid!!)) return

        this.processes[process.uuid!!] = process
    }

    fun unregisterProcess(process: T) {
        this.processes.remove(process.uuid!!)
    }

    fun updateProcess(process: T) {
        this.unregisterProcess(process)
        this.registerProcess(process)
    }

    fun getProcess(uuid: String): T? {
        return this.processes[uuid]
    }

    fun getRunningProcessCount(name: String): Int {
        return this.processes.values.stream().filter { it.groupName == name }.count().toInt()
    }
}
