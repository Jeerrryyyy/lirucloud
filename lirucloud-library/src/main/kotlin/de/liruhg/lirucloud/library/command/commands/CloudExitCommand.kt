package de.liruhg.lirucloud.library.command.commands

import de.liruhg.lirucloud.library.command.Command
import de.liruhg.lirucloud.library.command.CommandInformation
import kotlin.system.exitProcess

@CommandInformation(
    command = "exit",
    description = "This command will shutdown the cloud!",
    aliases = ["stop", "shutdown"]
)
class CloudExitCommand : Command {

    override fun execute(args: Array<String>): Boolean {
        exitProcess(0)
    }
}