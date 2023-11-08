package de.liruhg.lirucloud.library.util

import java.net.ServerSocket

class PortUtils {

    companion object {
        private val usedPorts: MutableSet<Int> = mutableSetOf()

        private fun isPortFree(port: Int): Boolean {
            return try {
                val serverSocket = ServerSocket(port)
                serverSocket.close()

                true
            } catch (e: Exception) {
                false
            }
        }

        fun getNextFreePort(startPort: Int): Int {
            var port = startPort

            while (!this.isPortFree(port) || this.usedPorts.contains(port)) {
                port++
            }

            return port
        }

        fun blockPort(port: Int) {
            this.usedPorts.add(port)
        }

        fun unblockPort(port: Int) {
            this.usedPorts.remove(port)
        }
    }
}