package de.liruhg.lirucloud.master.task

import de.liruhg.lirucloud.master.LiruCloudMaster
import de.liruhg.lirucloud.master.client.ClientRegistry
import org.kodein.di.instance
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

class CheckDanglingConnectionsTask : TimerTask() {

    private val logger: Logger = LoggerFactory.getLogger(CheckDanglingConnectionsTask::class.java)

    private val clientRegistry: ClientRegistry by LiruCloudMaster.KODEIN.instance()

    override fun run() {
        val current = System.currentTimeMillis()

        this.clientRegistry.getDanglingConnections().forEach { (channel, maxTimeAlive) ->
            if (current > maxTimeAlive) {
                this.logger.info(
                    "Removing dangling connection Remote: [${
                        channel.remoteAddress().toString().replace("/", "")
                    }] - Diff: [${current - maxTimeAlive}ms]"
                )

                this.clientRegistry.unregisterDanglingConnection(channel)

                channel.close()
            }
        }
    }
}