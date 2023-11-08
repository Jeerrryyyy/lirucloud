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

        this.networkConnectionRegistry.getDanglingConnections().forEach { (channelId, pair) ->
            if (current > pair.first) {
                this.logger.info(
                    "Removing dangling connection Remote: [${
                        pair.second.remoteAddress().toString().replace("/", "")
                    }] - Diff: [${current - pair.first}ms]"
                )

                this.networkConnectionRegistry.unregisterDanglingConnection(channelId)

                pair.second.close()
            }
        }
    }
}