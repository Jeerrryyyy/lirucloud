package de.liruhg.lirucloud.client.process.server.config

class ServerProperties {

    companion object {
        fun getProperties(
            viewDistance: Int = 10,
            maxBuildHeight: Int = 256,
            serverIp: String = "",
            levelSeed: String = "",
            gameMode: Int = 0,
            serverPort: Int = 25565,
            enableCommandBlock: Boolean = false,
            allowNether: Boolean = true,
            enableRcon: Boolean = false,
            opPermissionLevel: Int = 4,
            enableQuery: Boolean = false,
            generatorSettings: String = "",
            resourcePack: String = "",
            playerIdleTimeout: Int = 0,
            levelName: String = "world",
            motd: String = "Default LiruCloud Server",
            announcePlayerAchievements: Boolean = true,
            forceGameMode: Boolean = false,
            hardcore: Boolean = false,
            whitelist: Boolean = false,
            pvp: Boolean = true,
            spawnNpcs: Boolean = true,
            generateStructures: Boolean = true,
            spawnAnimals: Boolean = true,
            snooperEnabled: Boolean = true,
            difficulty: Int = 1,
            networkCompressionThreshold: Int = 256,
            levelType: String = "DEFAULT",
            spawnMonsters: Boolean = true,
            maxPlayers: Int = 20,
            onlineMode: Boolean = false,
            allowFlight: Boolean = true,
            resourcePackHash: String = "",
            maxWorldSize: Int = 29999984,
            useNativeTransport: Boolean = false
        ): String {
            return "view-distance=$viewDistance\n" +
                    "max-build-height=$maxBuildHeight\n" +
                    "server-ip=$serverIp\n" +
                    "level-seed=$levelSeed\n" +
                    "gamemode=$gameMode\n" +
                    "server-port=$serverPort\n" +
                    "enable-command-block=$enableCommandBlock\n" +
                    "allow-nether=$allowNether\n" +
                    "enable-rcon=$enableRcon\n" +
                    "op-permission-level=$opPermissionLevel\n" +
                    "enable-query=$enableQuery\n" +
                    "generator-settings=$generatorSettings\n" +
                    "resource-pack=$resourcePack\n" +
                    "player-idle-timeout=$playerIdleTimeout\n" +
                    "level-name=$levelName\n" +
                    "motd=$motd\n" +
                    "announce-player-achievements=$announcePlayerAchievements\n" +
                    "force-gamemode=$forceGameMode\n" +
                    "hardcore=$hardcore\n" +
                    "white-list=$whitelist\n" +
                    "pvp=$pvp\n" +
                    "spawn-npcs=$spawnNpcs\n" +
                    "generate-structures=$generateStructures\n" +
                    "spawn-animals=$spawnAnimals\n" +
                    "snooper-enabled=$snooperEnabled\n" +
                    "difficulty=$difficulty\n" +
                    "network-compression-threshold=$networkCompressionThreshold\n" +
                    "level-type=$levelType\n" +
                    "spawn-monsters=$spawnMonsters\n" +
                    "max-players=$maxPlayers\n" +
                    "online-mode=$onlineMode\n" +
                    "allow-flight=$allowFlight\n" +
                    "resource-pack-hash=$resourcePackHash\n" +
                    "max-world-size=$maxWorldSize\n" +
                    "use-native-transport=$useNativeTransport"
        }
    }
}