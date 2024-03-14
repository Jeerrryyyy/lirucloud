package de.liruhg.lirucloud.master.configuration.proxy

import de.liruhg.lirucloud.library.configuration.Configuration
import de.liruhg.lirucloud.library.proxy.ProxyInformation
import de.liruhg.lirucloud.master.group.proxy.ProxyGroup
import de.liruhg.lirucloud.master.group.proxy.ProxyGroupHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ProxyGroupLoader(
    private val proxyGroupHandler: ProxyGroupHandler
) : Configuration {

    private val logger: Logger = LoggerFactory.getLogger(ProxyGroupLoader::class.java)

    override fun execute() {
        val shouldCreateGroup = this.proxyGroupHandler.shouldCreateGroup()

        if (shouldCreateGroup) {
            this.logger.warn("No proxy groups found. Continuing with creating default proxy group..")

            val proxyGroup = ProxyGroup(
                name = "Proxy",
                minServersOnline = 1,
                maxMemory = 512,
                minMemory = 128,
                maxPlayers = 1000,
                proxyInformation = ProxyInformation(
                    joinPower = 0,
                    maintenance = false,
                    maintenanceProtocolMessage = "§cProtocol message",
                    maintenanceMotd = Pair("§cFirstline", "§cSecondline"),
                    motd = Pair("§cFirstline", "§cSecondline")
                )
            )

            this.proxyGroupHandler.createGroup(proxyGroup)
            return
        }

        this.proxyGroupHandler.fetchGroups().forEach {
            this.proxyGroupHandler.registerGroup(it)

            this.logger.info("Registered proxy group with Name: [${it.name}]")
        }
    }
}