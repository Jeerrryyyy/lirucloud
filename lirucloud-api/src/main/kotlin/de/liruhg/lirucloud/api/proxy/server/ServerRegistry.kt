package de.liruhg.lirucloud.api.proxy.server

import de.liruhg.lirucloud.api.global.runtime.RuntimeVars
import de.liruhg.lirucloud.api.proxy.server.protocol.out.PacketOutProxyRegisteredServer
import de.liruhg.lirucloud.library.network.util.NetworkUtil
import de.liruhg.lirucloud.library.process.ProcessMode
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.config.ServerInfo
import java.net.InetSocketAddress
import java.util.concurrent.ConcurrentHashMap

class ServerRegistry(
    private val networkUtil: NetworkUtil,
    private val runtimeVars: RuntimeVars
) {

    private val serverProcesses: ConcurrentHashMap<String, Pair<String, ProcessMode>> = ConcurrentHashMap()

    fun registerServer(uuid: String, name: String, mode: ProcessMode, ip: String, port: Int) {
        val iNetSocketAddress = InetSocketAddress.createUnresolved(ip, port)
        val serverInfo = ProxyServer.getInstance().constructServerInfo(
            name,
            iNetSocketAddress,
            "LiruCloud Client Server",
            false
        )

        @Suppress("DEPRECATION") // This is the only way to register a server.
        ProxyServer.getInstance().servers[name] = serverInfo

        this.serverProcesses[uuid] = Pair(name, mode)

        this.networkUtil.sendPacket(
            PacketOutProxyRegisteredServer(uuid),
            runtimeVars.masterChannel
        )
    }

    fun unregisterServer(name: String) {
        @Suppress("DEPRECATION") // This is the only way to unregister a server.
        ProxyServer.getInstance().servers.remove(name)

        this.serverProcesses.remove(name)
    }

    fun getLeastUsedLobbyProcess(): ServerInfo? {
        val lobbyProcesses = this.serverProcesses.values.filter { it.second == ProcessMode.LOBBY }

        if (lobbyProcesses.isEmpty()) {
            return null
        }

        val lobbyProcess = lobbyProcesses.minBy { ProxyServer.getInstance().getServerInfo(it.first).players.size }

        return ProxyServer.getInstance().getServerInfo(lobbyProcess.first)
    }
}