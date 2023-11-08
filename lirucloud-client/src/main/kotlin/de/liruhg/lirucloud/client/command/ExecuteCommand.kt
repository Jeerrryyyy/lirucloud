package de.liruhg.lirucloud.client.command

import de.liruhg.lirucloud.client.process.ProcessRegistry
import de.liruhg.lirucloud.library.command.Command
import de.liruhg.lirucloud.library.command.CommandInformation
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@CommandInformation(
    command = "execute",
    description = "Executes a command on a process",
    aliases = ["exec"]
)
class ExecuteCommand(
    private val processRegistry: ProcessRegistry,
) : Command {

    private val logger: Logger = LoggerFactory.getLogger(ExecuteCommand::class.java)

    override fun execute(args: Array<String>): Boolean {
        if (args.size < 3) {
            this.logger.error("Invalid arguments! Usage: execute <group> <command>")
            return true
        }

        val processName = args[1]
        val command = args.sliceArray(2..<args.size).joinToString(" ")

        val process = this.processRegistry.getProcessByName(processName)

        if (process != null) {
            val bufferedWRiter = process.process.outputWriter()
            bufferedWRiter.write("$command\n")
            bufferedWRiter.flush()
            return true
        }

        this.logger.error("Could not find process with name: [$processName]")

        return true
    }
}