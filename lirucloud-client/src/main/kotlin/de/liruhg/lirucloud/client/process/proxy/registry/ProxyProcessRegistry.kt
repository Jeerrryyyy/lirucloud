package de.liruhg.lirucloud.client.process.proxy.registry

import de.liruhg.lirucloud.client.process.ProcessRegistry
import de.liruhg.lirucloud.client.process.proxy.model.InternalProxyProcess
import de.liruhg.lirucloud.library.util.FileUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.io.path.exists

class ProxyProcessRegistry : ProcessRegistry<InternalProxyProcess>() {

    private val logger: Logger = LoggerFactory.getLogger(ProxyProcessRegistry::class.java)

    override fun shutdownProcesses() {
        this.processes.values.forEach {
            val bufferedWriter = it.process.outputWriter()
            bufferedWriter.write("end\n")
            bufferedWriter.flush()

            it.process.waitFor()

            while (it.serverDirectoryPath.exists()) {
                FileUtils.deleteFullDirectory(it.serverDirectoryPath)

                Thread.sleep(3000)
            }

            this.logger.info("Successfully shutdown process with Name: ${it.name} - UUID: ${it.uuid}")
        }
    }
}