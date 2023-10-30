package de.liruhg.lirucloud.library.command

import de.liruhg.lirucloud.library.thread.ThreadPool
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.BufferedReader

class CommandManager(
    private val threadPool: ThreadPool
) {

    private val logger: Logger = LoggerFactory.getLogger(CommandManager::class.java)
    private var enabled: Boolean = false

    val commands: MutableSet<Command> = mutableSetOf()

    fun start() {
        this.enabled = true

        this.threadPool.execute({
            val bufferedReader: BufferedReader = System.`in`.bufferedReader()

            while (this.enabled) {
                val input = bufferedReader.readLine()

                if (input == null || input.isEmpty()) {
                    break
                }

                val args = input.split(" ").toTypedArray()
                val command = this.parseArgs(args)

                if (command == null) {
                    this.logger.warn("Command not found. Please try \"help\"")
                    continue
                }

                val executed = command.execute(args)

                if (!executed) {
                    this.logger.warn("Something went wrong while executing the command! Please try \"help\"")
                }
            }
        }, false)
    }

    fun registerCommand(command: Command) {
        if (!command::class.java.isAnnotationPresent(CommandInformation::class.java)) return
        this.commands.add(command)
    }

    fun stop() {
        this.enabled = false
    }

    private fun parseArgs(args: Array<String>): Command? {
        val commandName = args[0]

        return this.commands.firstOrNull { command ->
            val commandInformation = command::class.java.getAnnotation(CommandInformation::class.java)
                ?: return@firstOrNull false

            if (commandName == commandInformation.command || commandInformation.aliases.contains(commandName)) {
                return@firstOrNull true
            }

            false
        }
    }
}