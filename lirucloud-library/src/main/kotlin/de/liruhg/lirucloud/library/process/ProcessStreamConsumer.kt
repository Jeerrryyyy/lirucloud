package de.liruhg.lirucloud.library.process

import java.io.InputStream

class ProcessStreamConsumer(
    private val inputStream: InputStream
) : Runnable {

    private val logLines: MutableList<String> = mutableListOf()

    override fun run() {
        this.inputStream.bufferedReader().lines().forEach(this::handleLine)
    }

    private fun handleLine(line: String) {
        this.logLines.add(line)
    }

    fun printLog() {
        this.logLines.forEach(::println)
        this.logLines.clear()
    }
}