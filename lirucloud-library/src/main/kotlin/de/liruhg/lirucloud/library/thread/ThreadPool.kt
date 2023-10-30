package de.liruhg.lirucloud.library.thread

import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.ThreadPoolExecutor

class ThreadPool {

    private val internalPoolNonDaemon: ThreadPoolExecutor =
        Executors.newFixedThreadPool(32, InternalThreadFactory("lirucloud", false)) as ThreadPoolExecutor

    private val internalPoolDaemon: ThreadPoolExecutor =
        Executors.newFixedThreadPool(32, InternalThreadFactory("lirucloud", true)) as ThreadPoolExecutor

    fun <T> submit(runnable: () -> T, daemon: Boolean = false): Future<T> {
        if (daemon) {
            return this.internalPoolDaemon.submit(runnable)
        }

        return this.internalPoolNonDaemon.submit(runnable)
    }

    fun execute(runnable: Runnable, daemon: Boolean = false) {
        if (daemon) {
            this.internalPoolDaemon.execute(runnable)
            return
        }

        this.internalPoolNonDaemon.execute(runnable)
    }

    fun execute(runnable: () -> Unit, daemon: Boolean = false) {
        if (daemon) {
            this.internalPoolDaemon.execute(runnable)
            return
        }

        this.internalPoolNonDaemon.execute(runnable)
    }

    fun createThread(runnable: () -> Unit, daemon: Boolean = false) {
        if (daemon) {
            this.internalPoolDaemon.threadFactory.newThread(runnable).start()
            return
        }

        this.internalPoolNonDaemon.threadFactory.newThread(runnable).start()
    }

    fun shutdown() {
        this.internalPoolDaemon.shutdown()
        this.internalPoolNonDaemon.shutdown()
    }
}