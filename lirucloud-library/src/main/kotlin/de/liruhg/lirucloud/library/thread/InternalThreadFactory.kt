package de.liruhg.lirucloud.library.thread

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.ThreadFactory

class InternalThreadFactory(
    private val threadPrefix: String,
    private val daemon: Boolean = false
) : ThreadFactory {

    private val logger: Logger = LoggerFactory.getLogger(InternalThreadFactory::class.java)

    override fun newThread(runnable: Runnable): Thread {
        val thread = Thread(runnable)

        thread.name = "${this.threadPrefix}-${thread.threadId()}"
        thread.isDaemon = this.daemon

        thread.setUncaughtExceptionHandler { shadowThread, cause ->
            this.logger.error("Exception caught in thread with Name: [${shadowThread.name}] - Reason: [${cause.message}]")
        }

        return thread
    }
}