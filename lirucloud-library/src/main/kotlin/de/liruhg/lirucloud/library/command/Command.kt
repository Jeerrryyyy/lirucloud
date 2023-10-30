package de.liruhg.lirucloud.library.command

interface Command {

    fun execute(args: Array<String>): Boolean
}