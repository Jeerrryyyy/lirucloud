package de.liruhg.lirucloud.master.bootstrap

import de.liruhg.lirucloud.master.LiruCloudMaster

fun main(args: Array<String>) {
    Thread.currentThread().name = "lirucloud-main"

    val liruCloudMaster = LiruCloudMaster()
    liruCloudMaster.start(args)

    Runtime.getRuntime().addShutdownHook(Thread({
        liruCloudMaster.shutdownGracefully()
    }, "lirucloud-shutdown-hook"))
}