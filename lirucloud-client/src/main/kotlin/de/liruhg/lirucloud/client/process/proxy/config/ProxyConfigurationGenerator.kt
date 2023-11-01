package de.liruhg.lirucloud.client.process.proxy.config

import org.yaml.snakeyaml.Yaml
import java.io.File

class ProxyConfigurationGenerator {

    private val yaml: Yaml = Yaml()

    fun writeConfiguration(
        serverPath: File,
        port: Int,
        maxPlayers: Int,
        tabSize: Int,
        motd: String,
    ) {
        val configFile = File(serverPath, "config.yml")

        if (!configFile.exists()) {
            configFile.createNewFile()
        }

        val config = mapOf(
            "server_connect_timeout" to 5000,
            "enforce_secure_profile" to false,
            "remote_ping_cache" to -1,
            "forge_support" to true,
            "player_limit" to maxPlayers,
            "permissions" to mapOf(
                "default" to listOf("none"),
            ),
            "timeout" to 30000,
            "log_commands" to false,
            "network_compression_threshold" to 256,
            "online_mode" to true,
            "disabled_commands" to listOf("none"),
            "servers" to mapOf(
                "dogpiss" to mapOf(
                    "motd" to "&1Forced Non Existing Server",
                    "address" to "localhost:59000",
                    "restricted" to false
                )
            ),
            "listeners" to listOf(
                mapOf(
                    "query_port" to port,
                    "motd" to motd,
                    "tab_list" to "SERVER",
                    "query_enabled" to false,
                    "proxy_protocol" to false,
                    "forced_hosts" to mapOf("pvp.md-5.net" to "dogpiss"),
                    "ping_passthrough" to false,
                    "priorities" to listOf("dogpiss"),
                    "bind_local_address" to true,
                    "host" to "0.0.0.0:$port",
                    "max_players" to maxPlayers,
                    "tab_size" to tabSize,
                    "force_default_server" to false
                )
            ),
            "ip_forward" to false,
            "remote_ping_timeout" to 5000,
            "prevent_proxy_connections" to false,
            "groups" to mapOf("Jevzo" to listOf("admin")),
            "connection_throttle" to -1,
            "stats" to "a1dc9e42-5c4f-4268-a300-a15b7f1f5d35",
            "connection_throttle_limit" to -1,
            "log_pings" to true
        )

        val writer = configFile.writer()

        this.yaml.dump(config, writer)

        writer.flush()
        writer.close()
    }
}