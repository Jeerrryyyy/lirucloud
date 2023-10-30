package de.liruhg.lirucloud.master.configuration.proxy

import de.liruhg.lirucloud.library.configuration.Configuration
import de.liruhg.lirucloud.library.directory.Directories
import de.liruhg.lirucloud.library.util.FileUtils
import de.liruhg.lirucloud.master.group.proxy.ProxyGroupHandler
import de.liruhg.lirucloud.master.group.proxy.model.ProxyGroupModel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

class ProxyGroupLoader(
    private val proxyGroupHandler: ProxyGroupHandler
) : Configuration {

    private val logger: Logger = LoggerFactory.getLogger(ProxyGroupLoader::class.java)

    override fun execute() {
        val proxyGroupFiles = File(Directories.MASTER_GROUPS_PROXY).listFiles()

        if (proxyGroupFiles == null || proxyGroupFiles.isEmpty()) {
            this.logger.warn("No proxy groups found. Continuing with creating default proxy group..")

            val proxyGroupModel = ProxyGroupModel(
                name = "Proxy",
                maxServersOnline = 1,
                minServersOnline = 1,
                maxMemory = 512,
                minMemory = 128,
                maxPlayers = 1000,
                joinPower = 0,
                maintenance = false,
            )

            this.proxyGroupHandler.createGroup(proxyGroupModel)
            return
        }

        for (proxyGroupFile in proxyGroupFiles) {
            if (!proxyGroupFile.name.endsWith(".json")) {
                this.logger.warn("Found invalid proxy group file Name: [${proxyGroupFile.name}]")
                continue
            }

            val proxyGroupModel = FileUtils.readClassFromJson(proxyGroupFile, ProxyGroupModel::class.java)
            this.proxyGroupHandler.registerGroup(proxyGroupModel)
        }
    }
}