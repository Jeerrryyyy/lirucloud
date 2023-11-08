package de.liruhg.lirucloud.api.proxy.listener

import de.liruhg.lirucloud.api.proxy.LiruCloudProxyApi
import de.liruhg.lirucloud.api.proxy.server.ServerRegistry
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.event.ServerConnectEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import org.kodein.di.instance

class ServerConnectEventListener : Listener {

    private val serverRegistry: ServerRegistry by LiruCloudProxyApi.KODEIN.instance()

    @EventHandler
    fun onServerConnect(serverConnectEvent: ServerConnectEvent) {
        if (serverConnectEvent.reason != ServerConnectEvent.Reason.JOIN_PROXY) return

        val serverInfo = this.serverRegistry.getLeastUsedLobbyProcess()

        if (serverInfo == null) {
            serverConnectEvent.player.disconnect(TextComponent("§cThere are no lobbies available at the moment. Please try again later."))
            return
        }

        serverConnectEvent.target = serverInfo
    }
}