package de.liruhg.lirucloud.library.network.client

import de.liruhg.lirucloud.library.network.helper.NettyHelper
import de.liruhg.lirucloud.library.thread.ThreadPool
import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.EventLoopGroup
import io.netty.handler.ssl.SslContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class AbstractNetworkClient(
    private val nettyHelper: NettyHelper,
    private val threadPool: ThreadPool
) {

    private val logger: Logger = LoggerFactory.getLogger(AbstractNetworkClient::class.java)

    private lateinit var workerGroup: EventLoopGroup

    fun startClient(host: String, port: Int) {
        this.workerGroup = this.nettyHelper.getEventLoopGroup("liruclient-worker")

        val sslContext = this.nettyHelper.createClientCert()
        val channelClass = this.nettyHelper.getClientChannelClass()

        this.threadPool.execute({
            try {
                Bootstrap().group(this.workerGroup)
                    .option(ChannelOption.AUTO_READ, true)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.IP_TOS, 24)
                    .option(ChannelOption.TCP_NODELAY, true)

                    .channel(channelClass)
                    .handler(object : ChannelInitializer<Channel>() {
                        override fun initChannel(channel: Channel) {
                            preparePipeline(sslContext, channel)
                        }
                    }).connect(host, port).channel().closeFuture().syncUninterruptibly()
            } catch (e: Exception) {
                this.logger.error("Exception caught in Component: [CLIENT] - Reason: [${e.message}]")
            } finally {
                this.workerGroup.shutdownGracefully()
            }
        }, false)
    }

    fun shutdownGracefully() {
        this.workerGroup.shutdownGracefully()
    }

    abstract fun preparePipeline(sslContext: SslContext?, channel: Channel)
}