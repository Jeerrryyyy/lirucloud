package de.liruhg.lirucloud.client.bootstrap

import de.liruhg.lirucloud.client.LiruCloudClient

fun main(args: Array<String>) {
    Thread.currentThread().name = "lirucloud-main"

    val liruCloudClient = LiruCloudClient()
    liruCloudClient.start(args)

    val shutdownHook = Thread({
        liruCloudClient.shutdownGracefully()
    }, "lirucloud-shutdown-hook")

    shutdownHook.isDaemon = false

    Runtime.getRuntime().addShutdownHook(shutdownHook)
}