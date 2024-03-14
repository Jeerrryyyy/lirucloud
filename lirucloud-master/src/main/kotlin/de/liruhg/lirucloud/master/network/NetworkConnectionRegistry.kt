package de.liruhg.lirucloud.master.network

import io.netty.channel.Channel
import io.netty.channel.ChannelId

class NetworkConnectionRegistry {

    private val danglingConnections: MutableMap<ChannelId, Pair<Long, Channel>> = mutableMapOf()

    fun registerDanglingConnection(chanelId: ChannelId, channel: Channel): Boolean {
        if (this.danglingConnections.containsKey(chanelId)) return false
        this.danglingConnections[chanelId] = Pair(System.currentTimeMillis(), channel)

        return this.danglingConnections.containsKey(chanelId)
    }

    fun unregisterDanglingConnection(chanelId: ChannelId): Boolean {
        if (!this.danglingConnections.containsKey(chanelId)) return false
        this.danglingConnections.remove(chanelId)

        return !this.danglingConnections.containsKey(chanelId)
    }

    fun getDanglingConnection(chanelId: ChannelId): Pair<Long, Channel>? {
        return this.danglingConnections[chanelId]
    }

    fun getDanglingConnections(): MutableMap<ChannelId, Pair<Long, Channel>> {
        return this.danglingConnections
    }
}