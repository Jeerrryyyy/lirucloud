package de.liruhg.lirucloud.client.process

import de.liruhg.lirucloud.client.LiruCloudClient
import de.liruhg.lirucloud.library.thread.ThreadPool
import de.liruhg.lirucloud.library.util.FileUtils
import org.kodein.di.instance
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.io.path.exists

open class ProcessRegistry<T : InternalCloudProcess> {

    private val logger: Logger = LoggerFactory.getLogger(ProcessRegistry::class.java)
    private val processes: MutableMap<String, T> = mutableMapOf()

    private val threadPool: ThreadPool by LiruCloudClient.KODEIN.instance()

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

    fun getTotalMemoryUsage(): Int {
        return this.processes.values.sumOf { it.maxMemory }
    }

    fun killAllProcesses() {
        this.processes.values.forEach {
            it.process.destroy()

            var attempts = 1
            while (it.process.isAlive) {
                this.logger.info("Awaiting shutdown of process with Name: ${it.name} - UUID: ${it.uuid} (Attempt: $attempts/10)")

                Thread.sleep(3000)

                if (attempts >= 10) {
                    this.logger.warn("Failed to shutdown process with Name: ${it.name} - UUID: ${it.uuid} after 10 attempts. Killing process now.")
                    it.process.destroyForcibly()
                    break
                }

                attempts++
            }

            while (it.serverDirectoryPath.exists()) {
                FileUtils.deleteFullDirectory(it.serverDirectoryPath)

                Thread.sleep(3000)
            }

            this.logger.info("Successfully shutdown process with Name: ${it.name} - UUID: ${it.uuid}")
        }
    }
}