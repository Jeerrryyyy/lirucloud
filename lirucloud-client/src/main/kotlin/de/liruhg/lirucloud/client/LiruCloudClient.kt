package de.liruhg.lirucloud.client

import org.kodein.di.DI
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.system.exitProcess

class LiruCloudClient {

    private val logger: Logger = LoggerFactory.getLogger(LiruCloudClient::class.java)

    companion object {
        lateinit var KODEIN: DI
    }

    fun start(args: Array<String>) {
        this.logger.info("Welcome to LiruCloud! Please wait while we initialize components and start systems...")

        this.initializeDI()
        this.checkForRoot(args)
        this.checkForDebug(args)

        this.startTasks()
    }

    fun shutdownGracefully() {
        this.logger.info("Shutting down LiruCloud gracefully... Please be patient.")

        this.logger.info("Thank you for using LiruCloud!")
    }

    private fun initializeDI() {
        KODEIN = DI {

        }
    }

    private fun checkForRoot(args: Array<String>) {
        if (System.getProperty("user.name") != "root" || args.contains("--enable-root")) {
            return
        }

        this.logger.warn("Please consider not to use the \"root\" user for security reasons!")
        this.logger.warn("If you want to use it anyway, at your own risk, add \"--enable-root\" to the start arguments.")
        exitProcess(1)
    }

    private fun checkForDebug(args: Array<String>) {

    }

    private fun startTasks() {

    }
}