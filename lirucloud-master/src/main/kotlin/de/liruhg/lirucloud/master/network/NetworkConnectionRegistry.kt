package de.liruhg.lirucloud.master.network

import io.netty.channel.Channel

class NetworkConnectionRegistry {

    private val danglingConnections: MutableMap<Channel, Long> = mutableMapOf()

    fun registerDanglingConnection(channel: Channel): Boolean {
        if (this.danglingConnections.containsKey(channel)) return false
        this.danglingConnections[channel] = System.currentTimeMillis() + 10000

        return this.danglingConnections.containsKey(channel)
    }

    fun unregisterDanglingConnection(channel: Channel): Boolean {
        if (!this.danglingConnections.containsKey(channel)) return false
        this.danglingConnections.remove(channel)

        return !this.danglingConnections.containsKey(channel)
    }

    fun getDanglingConnection(channel: Channel): Long? {
        return this.danglingConnections[channel]
    }

    fun getDanglingConnections(): Map<Channel, Long> {
        return this.danglingConnections
    }
}