package de.liruhg.lirucloud.master.task

import de.liruhg.lirucloud.master.LiruCloudMaster
import de.liruhg.lirucloud.master.network.NetworkConnectionRegistry
import org.kodein.di.instance
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

class CheckDanglingConnectionsTask : TimerTask() {

    private val logger: Logger = LoggerFactory.getLogger(CheckDanglingConnectionsTask::class.java)

    private val networkConnectionRegistry: NetworkConnectionRegistry by LiruCloudMaster.KODEIN.instance()

    override fun run() {
        val current = System.currentTimeMillis()

        this.networkConnectionRegistry.getDanglingConnections().forEach { (channel, maxTimeAlive) ->
            if (current > maxTimeAlive) {
                this.logger.info(
                    "Removing dangling connection Remote: [${
                        channel.remoteAddress().toString().replace("/", "")
                    }] - Diff: [${current - maxTimeAlive}ms]"
                )

                this.networkConnectionRegistry.unregisterDanglingConnection(channel)

                channel.close()
            }
        }
    }
}