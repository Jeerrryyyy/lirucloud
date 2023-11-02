package de.liruhg.lirucloud.client.process.server.registry

import de.liruhg.lirucloud.client.process.ProcessRegistry
import de.liruhg.lirucloud.client.process.server.model.InternalServerProcess
import de.liruhg.lirucloud.library.util.FileUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.io.path.exists

class ServerProcessRegistry : ProcessRegistry<InternalServerProcess>() {

    private val logger: Logger = LoggerFactory.getLogger(ServerProcessRegistry::class.java)

    override fun shutdownProcesses() {
        this.processes.values.forEach {
            val bufferedWriter = it.process.outputWriter()
            bufferedWriter.write("stop\n")
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