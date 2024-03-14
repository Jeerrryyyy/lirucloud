package de.liruhg.lirucloud.master.client

import de.liruhg.lirucloud.library.client.ClientInfo
import io.netty.channel.Channel

class ClientRegistry {

    private val clients: MutableMap<String, ClientInfo> = mutableMapOf()

    fun registerClient(clientInfo: ClientInfo): Boolean {
        if (this.clients.containsKey(clientInfo.uuid)) return false
        this.clients[clientInfo.uuid] = clientInfo

        return this.clients.containsKey(clientInfo.uuid)
    }

    fun unregisterClient(clientInfo: ClientInfo): Boolean {
        if (!this.clients.containsKey(clientInfo.uuid)) return false
        this.clients.remove(clientInfo.uuid)

        return !this.clients.containsKey(clientInfo.uuid)
    }

    fun updateClient(clientInfo: ClientInfo) {
        this.unregisterClient(clientInfo)
        this.registerClient(clientInfo)
    }

    fun getClient(uuid: String): ClientInfo? {
        return this.clients[uuid]
    }

    //fun getClient(process: CloudProcess): ClientInfo? {
    //return this.clients.values.firstOrNull { client ->
    //client.runningProcesses.any { it == process.uuid }
    //}
    //}

    fun getClientsByGroup(group: String): Set<ClientInfo> {
        return this.clients.values.filter { it.responsibleGroups.contains(group) }.toSet()
    }

    fun getClientByChannel(channel: Channel): ClientInfo? {
        return this.clients.values.firstOrNull { it.channel == channel }
    }

    fun getClients(): Set<ClientInfo> {
        return this.clients.values.toSet()
    }

    fun getLeastUsedClient(group: String): ClientInfo? {
        val clientsForGroup = this.getClientsByGroup(group)

        if (clientsForGroup.isEmpty()) return null

        var bestClient: ClientInfo? = null

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
}