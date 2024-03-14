package de.liruhg.lirucloud.client.process.server

import org.yaml.snakeyaml.Yaml
import java.io.File

class ServerConfigGenerator {

    private val yaml: Yaml = Yaml()

    fun generateSpigotYaml(
        serverPath: File,
    ) {
        val configFile = File(serverPath, "spigot.yml")

        if (!configFile.exists()) {
            configFile.createNewFile()
        }

        val config = mapOf(
            "config-version" to 8,
            "settings" to mapOf(
                "debug" to false,
                "save-user-cache-on-stop-only" to false,
                "sample-count" to 12,
                "int-cache-limit" to 1024,
                "bungeecord" to true,
                "late-bind" to false,
                "user-cache-size" to 1000,
                "player-shuffle" to 0,
                "timeout-time" to 60,
                "restart-on-crash" to false,
                "restart-script" to "./start.sh",
                "netty-threads" to 4,
                "attribute" to mapOf(
                    "maxHealth" to mapOf("max" to 2048.0),
                    "movementSpeed" to mapOf("max" to 2048.0),
                    "attackDamage" to mapOf("max" to 2048.0)
                ),
                "filter-creative-items" to true,
                "moved-too-quickly-threshold" to 100.0,
                "moved-wrongly-threshold" to 0.0625
            ),
            "timings" to mapOf(
                "enabled" to true,
                "verbose" to true,
                "server-name-privacy" to false,
                "hidden-config-entries" to listOf("database", "settings.bungeecord-addresses"),
                "history-interval" to 300,
                "history-length" to 3600
            ),
            "commands" to mapOf(
                "tab-complete" to 0,
                "log" to true,
                "replace-commands" to listOf("setblock", "summon", "testforblock", "tellraw"),
                "spam-exclusions" to listOf("/skill"),
                "silent-commandblock-console" to false
            ),
            "messages" to mapOf(
                "whitelist" to "You are not whitelisted on this server!",
                "unknown-command" to "Unknown command. Type \"/help\" for help.",
                "server-full" to "The server is full!",
                "outdated-client" to "Outdated client! Please use {0}",
                "outdated-server" to "Outdated server! I'm still on {0}",
                "restart" to "Server is restarting"
            ),
            "stats" to mapOf(
                "disable-saving" to false,
                "forced-stats" to emptyMap<String, Any>()
            ),
            "world-settings" to mapOf(
                "default" to mapOf(
                    "verbose" to true,
                    "arrow-despawn-rate" to 1200,
                    "merge-radius" to mapOf("item" to 2.5, "exp" to 3.0),
                    "item-despawn-rate" to 6000,
                    "enable-zombie-pigmen-portal-spawns" to true,
                    "anti-xray" to mapOf(
                        "enabled" to true,
                        "engine-mode" to 1,
                        "hide-blocks" to listOf(14, 15, 16, 21, 48, 49, 54, 56, 73, 74, 82, 129, 130),
                        "replace-blocks" to listOf(1, 5)
                    ),
                    "mob-spawn-range" to 4,
                    "growth" to mapOf(
                        "cactus-modifier" to 100,
                        "cane-modifier" to 100,
                        "melon-modifier" to 100,
                        "mushroom-modifier" to 100,
                        "pumpkin-modifier" to 100,
                        "sapling-modifier" to 100,
                        "wheat-modifier" to 100,
                        "netherwart-modifier" to 100
                    ),
                    "nerf-spawner-mobs" to false,
                    "random-light-updates" to false,
                    "entity-activation-range" to mapOf("animals" to 32, "monsters" to 32, "misc" to 16),
                    "save-structure-info" to true,
                    "entity-tracking-range" to mapOf(
                        "players" to 48,
                        "animals" to 48,
                        "monsters" to 48,
                        "misc" to 32,
                        "other" to 64
                    ),
                    "ticks-per" to mapOf("hopper-transfer" to 8, "hopper-check" to 8),
                    "hopper-amount" to 1,
                    "max-tick-time" to mapOf("tile" to 50, "entity" to 50),
                    "max-entity-collisions" to 8,
                    "hunger" to mapOf(
                        "walk-exhaustion" to 0.2,
                        "sprint-exhaustion" to 0.8,
                        "combat-exhaustion" to 0.3,
                        "regen-exhaustion" to 3.0
                    ),
                    "max-tnt-per-tick" to 100,
                    "max-bulk-chunks" to 10,
                    "seed-village" to 10387312,
                    "seed-feature" to 14357617,
                    "view-distance" to 10,
                    "wither-spawn-sound-radius" to 0,
                    "hanging-tick-frequency" to 100,
                    "zombie-aggressive-towards-villager" to true,
                    "dragon-death-sound-radius" to 0,
                    "chunks-per-tick" to 650,
                    "clear-tick-list" to false
                )
            )
        )

        val writer = configFile.writer()

        this.yaml.dump(config, writer)

        writer.flush()
        writer.close()
    }

    fun generateBukkitYaml(
        serverPath: File,
    ) {
        val configFile = File(serverPath, "bukkit.yml")

        if (!configFile.exists()) {
            configFile.createNewFile()
        }

        val config = mapOf(
            "settings" to mapOf(
                "allow-end" to true,
                "warn-on-overload" to true,
                "permissions-file" to "permissions.yml",
                "update-folder" to "update",
                "plugin-profiling" to false,
                "connection-throttle" to -1,
                "query-plugins" to true,
                "deprecated-verbose" to "default",
                "shutdown-message" to "Server closed"
            ),
            "spawn-limits" to mapOf(
                "monsters" to 70,
                "animals" to 15,
                "water-animals" to 5,
                "ambient" to 15
            ),
            "chunk-gc" to mapOf(
                "period-in-ticks" to 600,
                "load-threshold" to 0
            ),
            "ticks-per" to mapOf(
                "animal-spawns" to 400,
                "monster-spawns" to 1,
                "autosave" to 6000
            ),
            "aliases" to "now-in-commands.yml",
            "database" to mapOf(
                "username" to "bukkit",
                "isolation" to "SERIALIZABLE",
                "driver" to "org.sqlite.JDBC",
                "password" to "walrus",
                "url" to "jdbc:sqlite:{DIR}{NAME}.db"
            )
        )

        val writer = configFile.writer()

        this.yaml.dump(config, writer)

        writer.flush()
        writer.close()
    }
}