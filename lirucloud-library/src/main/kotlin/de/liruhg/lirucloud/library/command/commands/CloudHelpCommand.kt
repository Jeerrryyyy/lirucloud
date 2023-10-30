package de.liruhg.lirucloud.library.command.commands

import de.liruhg.lirucloud.library.command.Command
import de.liruhg.lirucloud.library.command.CommandInformation
import de.liruhg.lirucloud.library.command.CommandManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@CommandInformation(
    command = "help",
    description = "This command will show you all available commands!",
    aliases = ["?"]
)
class CloudHelpCommand(
    private val commandManager: CommandManager
) : Command {

    private val logger: Logger = LoggerFactory.getLogger(CloudHelpCommand::class.java)

    override fun execute(args: Array<String>): Boolean {
        this.logger.info("Available commands:")
        this.commandManager.commands.forEach(::printCommandInformation)
        return true
    }

    private fun printCommandInformation(command: Command) {
        val commandInformation = command::class.java.getAnnotation(CommandInformation::class.java)

        val commandName = commandInformation.command
        val commandDescription = commandInformation.description
        val commandAliases = commandInformation.aliases.joinToString(", ")

        this.logger.info("$commandName [$commandAliases] - $commandDescription")
    }
}