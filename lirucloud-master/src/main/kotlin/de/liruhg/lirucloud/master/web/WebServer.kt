package de.liruhg.lirucloud.master.web

import de.liruhg.lirucloud.library.network.helper.NettyHelper
import de.liruhg.lirucloud.library.network.web.AbstractWebServer
import de.liruhg.lirucloud.library.router.Router
import de.liruhg.lirucloud.library.thread.ThreadPool
import io.netty.channel.Channel
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.codec.http.HttpRequestDecoder
import io.netty.handler.codec.http.HttpResponseEncoder

class WebServer(
    threadPool: ThreadPool,
    private val nettyHelper: NettyHelper,
    private val router: Router
) : AbstractWebServer(nettyHelper, threadPool) {

    override fun preparePipeline(channel: Channel) {
        channel.pipeline()
            .addLast(HttpRequestDecoder(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, false))
            .addLast(HttpObjectAggregator(Integer.MAX_VALUE))
            .addLast(HttpResponseEncoder())
            .addLast(WebHandler(router, nettyHelper))
    }
}