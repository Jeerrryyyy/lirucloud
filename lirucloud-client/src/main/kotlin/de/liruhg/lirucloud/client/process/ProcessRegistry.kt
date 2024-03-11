package de.liruhg.lirucloud.client.process

import de.liruhg.lirucloud.library.process.ProcessType
import de.liruhg.lirucloud.library.util.FileUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException
import kotlin.io.path.exists

class ProcessRegistry {

    private val logger: Logger = LoggerFactory.getLogger(ProcessRegistry::class.java)

    private val processes: MutableMap<String, InternalCloudProcess> = mutableMapOf()

    fun registerProcess(process: InternalCloudProcess) {
        if (this.processes.containsKey(process.uuid)) return

        this.processes[process.uuid!!] = process
    }

    fun unregisterProcess(process: InternalCloudProcess) {
        this.processes.remove(process.uuid)
    }

    fun updateProcess(process: InternalCloudProcess) {
        this.unregisterProcess(process)
        this.registerProcess(process)
    }

    fun getProcess(uuid: String): InternalCloudProcess? {
        return this.processes[uuid]
    }

    fun getProcessByName(name: String): InternalCloudProcess? {
        return this.processes.values.firstOrNull { it.name == name }
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

    fun shutdownServers() {
        this.processes.values.filter { it.type == ProcessType.SERVER }.forEach {
            try {
                val bufferedWriter = it.process.outputWriter()
                bufferedWriter.write("stop\n")
                bufferedWriter.flush()

                it.process.waitFor()

                while (it.serverDirectoryPath.exists()) {
                    FileUtils.deleteFullDirectory(it.serverDirectoryPath)

                    Thread.sleep(3000)
                }

                this.logger.info("Successfully shutdown process with Name: ${it.name} - UUID: ${it.uuid}")
            } catch (e: IOException) {
                this.logger.error("Failed to shutdown process with Name: ${it.name} - UUID: ${it.uuid}")
            }
        }
    }

    fun shutdownProxies() {
        this.processes.values.filter { it.type == ProcessType.PROXY }.forEach {
            try {
                val bufferedWriter = it.process.outputWriter()
                bufferedWriter.write("end\n")
                bufferedWriter.flush()

                it.process.waitFor()

                while (it.serverDirectoryPath.exists()) {
                    FileUtils.deleteFullDirectory(it.serverDirectoryPath)

                    Thread.sleep(3000)
                }

                this.logger.info("Successfully shutdown process with Name: ${it.name} - UUID: ${it.uuid}")
            } catch (e: IOException) {
                this.logger.error("Failed to shutdown process with Name: ${it.name} - UUID: ${it.uuid}")
            }
        }
    }
}