package de.liruhg.lirucloud.master.client

import de.liruhg.lirucloud.library.network.client.model.ClientInfoModel
import io.netty.channel.Channel

class ClientRegistry {

    private val clients: MutableMap<String, ClientInfoModel> = mutableMapOf()
    private val danglingConnections: MutableMap<Channel, Long> = mutableMapOf()

    fun registerClient(clientInfoModel: ClientInfoModel): Boolean {
        if (this.clients.containsKey(clientInfoModel.uuid)) return false
        this.clients[clientInfoModel.uuid] = clientInfoModel

        return this.clients.containsKey(clientInfoModel.uuid)
    }

    fun registerDanglingConnection(channel: Channel): Boolean {
        if (this.danglingConnections.containsKey(channel)) return false
        this.danglingConnections[channel] = System.currentTimeMillis() + 10000

        return this.danglingConnections.containsKey(channel)
    }

    fun unregisterClient(clientInfoModel: ClientInfoModel): Boolean {
        if (!this.clients.containsKey(clientInfoModel.uuid)) return false
        this.clients.remove(clientInfoModel.uuid)

        return !this.clients.containsKey(clientInfoModel.uuid)
    }

    fun unregisterDanglingConnection(channel: Channel): Boolean {
        if (!this.danglingConnections.containsKey(channel)) return false
        this.danglingConnections.remove(channel)

        return !this.danglingConnections.containsKey(channel)
    }

    fun updateClient(clientInfoModel: ClientInfoModel): Boolean {
        if (!this.clients.containsKey(clientInfoModel.uuid)) return false
        this.clients[clientInfoModel.uuid] = clientInfoModel

        return this.clients.containsKey(clientInfoModel.uuid)
    }

    fun getClient(uuid: String): ClientInfoModel? {
        return this.clients[uuid]
    }

    fun getClientsByGroup(group: String): Set<ClientInfoModel> {
        return this.clients.values.filter { it.responsibleGroups.contains(group) }.toSet()
    }

    fun getClientByChannel(channel: Channel): ClientInfoModel? {
        return this.clients.values.firstOrNull { it.channel == channel }
    }

    fun getClients(): Set<ClientInfoModel> {
        return this.clients.values.toSet()
    }

    fun getLeastUsedClient(group: String): ClientInfoModel? {
        val clientsForGroup = this.getClientsByGroup(group)

        if (clientsForGroup.isEmpty()) return null

        var bestClient: ClientInfoModel? = null

        for (client in clientsForGroup) {
            if (bestClient == null) {
                bestClient = client
                continue
            }

            if (client.currentMemoryUsage < bestClient.currentMemoryUsage
                && client.currentCpuUsage < bestClient.currentCpuUsage
            ) {
                bestClient = client
            }
        }

        if (bestClient == clientsForGroup.first()) {
            for (client in clientsForGroup) {
                if (bestClient == null) {
                    bestClient = client
                    continue
                }

                if (client.currentMemoryUsage < bestClient.currentMemoryUsage) {
                    bestClient = client
                }
            }
        }

        return bestClient
    }

    fun getDanglingConnection(channel: Channel): Long? {
        return this.danglingConnections[channel]
    }

    fun getDanglingConnections(): Map<Channel, Long> {
        return this.danglingConnections
    }
}