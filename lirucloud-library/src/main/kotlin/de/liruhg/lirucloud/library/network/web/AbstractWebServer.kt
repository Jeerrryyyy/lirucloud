package de.liruhg.lirucloud.library.network.web

import de.liruhg.lirucloud.library.network.helper.NettyHelper
import de.liruhg.lirucloud.library.thread.ThreadPool
import io.netty.bootstrap.ServerBootstrap
import io.netty.buffer.ByteBufAllocator
import io.netty.channel.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class AbstractWebServer(
    private val nettyHelper: NettyHelper,
    private val threadPool: ThreadPool
) {

    private val logger: Logger = LoggerFactory.getLogger(AbstractWebServer::class.java)

    private lateinit var workerGroup: EventLoopGroup

    fun startServer(port: Int) {
        this.workerGroup = this.nettyHelper.getEventLoopGroup("liruweb-worker")

        val channelClass = this.nettyHelper.getWebServerChannelClass()

        this.threadPool.execute({
            try {
                ServerBootstrap().group(this.workerGroup)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.IP_TOS, 24)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.ALLOCATOR, ByteBufAllocator.DEFAULT)
                    .childOption(ChannelOption.AUTO_READ, true)

                    .option(ChannelOption.ALLOCATOR, ByteBufAllocator.DEFAULT)
                    .option(ChannelOption.AUTO_READ, true)

                    .channel(channelClass)
                    .childHandler(object : ChannelInitializer<Channel>() {
                        override fun initChannel(channel: Channel) {
                            preparePipeline(channel)
                        }
                    }).bind(port)
                    .addListener {
                        if (it.isSuccess) {
                            this.logger.info("Web Server started and was bound to 127.0.0.1:$port")
                        } else {
                            this.logger.error("Exception caught in Component: [WEB_SERVER] - Reason: [${it.cause().message}]")
                        }
                    }
                    .addListener(ChannelFutureListener.CLOSE_ON_FAILURE)
                    .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE)
                    .channel().closeFuture().syncUninterruptibly()
            } catch (e: Exception) {
                this.logger.error("Exception caught in Component: [WEB_SERVER] - Reason: [${e.message}]")
            } finally {
                this.workerGroup.shutdownGracefully()
            }
        }, false)
    }

    fun shutdownGracefully() {
        this.workerGroup.shutdownGracefully()
    }

    abstract fun preparePipeline(channel: Channel)
}