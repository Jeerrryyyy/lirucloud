package de.liruhg.lirucloud.library.util

import de.liruhg.lirucloud.library.cache.CacheConnectionFactory
import de.liruhg.lirucloud.library.cache.CachePrefix
import de.liruhg.lirucloud.library.cache.extension.deleteEntity
import de.liruhg.lirucloud.library.cache.extension.existsEntity
import de.liruhg.lirucloud.library.cache.extension.insertEntity
import java.net.ServerSocket

class PortUtil(
    private val cacheConnectionFactory: CacheConnectionFactory
) {

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

        while (!this.isPortFree(port) || this.cacheConnectionFactory.jedisPooled.existsEntity(CachePrefix.PORT, port.toString())) {
            port++
        }

        return port
    }

    fun blockPort(port: Int) {
        this.cacheConnectionFactory.jedisPooled.insertEntity(CachePrefix.PORT, port.toString(), port)
    }

    fun unblockPort(port: Int) {
        this.cacheConnectionFactory.jedisPooled.deleteEntity(CachePrefix.PORT, port.toString())
    }
}