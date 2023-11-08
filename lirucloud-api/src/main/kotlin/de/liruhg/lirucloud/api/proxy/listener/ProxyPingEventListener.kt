package de.liruhg.lirucloud.api.proxy.listener

import de.liruhg.lirucloud.api.global.runtime.RuntimeVars
import de.liruhg.lirucloud.api.proxy.LiruCloudProxyApi
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.event.ProxyPingEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import org.kodein.di.instance

class ProxyPingEventListener : Listener {

    private val runtimeVars: RuntimeVars by LiruCloudProxyApi.KODEIN.instance()

    @EventHandler
    fun onProxyPing(proxyPingEvent: ProxyPingEvent) {
        val serverPing = proxyPingEvent.response
        val players = serverPing.players
        val protocol = serverPing.version

        if (this.runtimeVars.proxyInformation.maintenance) {
            val firstLine = this.runtimeVars.proxyInformation.maintenanceMotd.first
            val secondLine = this.runtimeVars.proxyInformation.maintenanceMotd.second

            serverPing.descriptionComponent = TextComponent("$firstLine\n$secondLine")

            protocol.name = this.runtimeVars.proxyInformation.maintenanceProtocolMessage
            protocol.protocol = 999
        } else {
            val firstLine = this.runtimeVars.proxyInformation.motd.first
            val secondLine = this.runtimeVars.proxyInformation.motd.second

            serverPing.descriptionComponent = TextComponent("$firstLine\n$secondLine")
        }

        serverPing.players = players
        serverPing.version = protocol

        proxyPingEvent.response = serverPing
    }
}