package de.liruhg.lirucloud.library.util

import java.net.ServerSocket

class PortUtils {

    companion object {
        fun isPortFree(port: Int): Boolean {
            return try {
                val serverSocket = ServerSocket(port)
                serverSocket.close()

                true
            } catch (e: Exception) {
                false
            }
        }

        fun getNextFreePort(startPort: Int): Int {
            return if (this.isPortFree(startPort)) {
                startPort
            } else {
                this.getNextFreePort(startPort)
            }
        }
    }
}