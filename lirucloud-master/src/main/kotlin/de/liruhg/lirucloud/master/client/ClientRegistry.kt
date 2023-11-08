package de.liruhg.lirucloud.master.client

import de.liruhg.lirucloud.library.network.client.model.ClientInfoModel
import de.liruhg.lirucloud.library.process.CloudProcess
import io.netty.channel.Channel
import java.util.concurrent.ConcurrentHashMap

class ClientRegistry {

    private val clients: ConcurrentHashMap<String, ClientInfoModel> = ConcurrentHashMap()

    fun registerClient(clientInfoModel: ClientInfoModel): Boolean {
        if (this.clients.containsKey(clientInfoModel.uuid)) return false
        this.clients[clientInfoModel.uuid] = clientInfoModel

        return this.clients.containsKey(clientInfoModel.uuid)
    }

    fun unregisterClient(clientInfoModel: ClientInfoModel): Boolean {
        if (!this.clients.containsKey(clientInfoModel.uuid)) return false
        this.clients.remove(clientInfoModel.uuid)

        return !this.clients.containsKey(clientInfoModel.uuid)
    }

    fun updateClient(clientInfoModel: ClientInfoModel) {
        this.unregisterClient(clientInfoModel)
        this.registerClient(clientInfoModel)
    }

    fun getClient(uuid: String): ClientInfoModel? {
        return this.clients[uuid]
    }

    fun getClient(process: CloudProcess): ClientInfoModel? {
        return this.clients.values.firstOrNull { client ->
            client.runningProcesses.any { it == process.uuid }
        }
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
}