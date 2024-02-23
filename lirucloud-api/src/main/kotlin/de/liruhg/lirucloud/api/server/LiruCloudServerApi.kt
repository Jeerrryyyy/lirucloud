package de.liruhg.lirucloud.api.server

import de.liruhg.lirucloud.api.global.GlobalPluginDi
import org.bukkit.plugin.java.JavaPlugin
import org.kodein.di.DI
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Suppress("unused") // This class is used by Spigot.
class LiruCloudServerApi : JavaPlugin() {

    private val logger: Logger = LoggerFactory.getLogger(LiruCloudServerApi::class.java)

    companion object {
        lateinit var KODEIN: DI
    }

    override fun onEnable() {
        this.logger.info("Welcome to LiruCloud! Please wait while we initialize components and start systems...")

        GlobalPluginDi().initializeDi()
        this.initializeDI()
    }

    override fun onDisable() {
        this.logger.info("Thank you for using LiruCloud!")
    }

    private fun initializeDI() {
        KODEIN = DI {
            extend(GlobalPluginDi.KODEIN)
        }
    }
}