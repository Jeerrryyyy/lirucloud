package de.liruhg.lirucloud.api.proxy

import de.liruhg.lirucloud.api.global.GlobalPluginDi
import net.md_5.bungee.api.plugin.Plugin
import org.kodein.di.DI
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Suppress("unused") // This class is used by BungeeCord.
class LiruCloudProxyApi : Plugin() {

    private val logger: Logger = LoggerFactory.getLogger(LiruCloudProxyApi::class.java)

    companion object {
        lateinit var KODEIN: DI
    }

    override fun onEnable() {
        this.logger.info("Welcome to LiruCloud! Please wait while we initialize components and start systems...")

        GlobalPluginDi().initializeDi()
        this.initializeDI()

        this.registerListeners()
    }

    override fun onDisable() {
        this.logger.info("Thank you for using LiruCloud!")
    }

    private fun initializeDI() {
        KODEIN = DI {
            extend(GlobalPluginDi.KODEIN)
        }
    }

    private fun registerListeners() {
    }
}