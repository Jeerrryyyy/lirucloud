package de.liruhg.lirucloud.library.network.server

import de.liruhg.lirucloud.library.network.helper.NettyHelper
import de.liruhg.lirucloud.library.thread.ThreadPool
import io.netty.bootstrap.ServerBootstrap
import io.netty.buffer.ByteBufAllocator
import io.netty.channel.*
import io.netty.handler.ssl.SslContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class AbstractNetworkServer(
    private val nettyHelper: NettyHelper,
    private val threadPool: ThreadPool
) {

    private val logger: Logger = LoggerFactory.getLogger(AbstractNetworkServer::class.java)

    private lateinit var bossGroup: EventLoopGroup
    private lateinit var workerGroup: EventLoopGroup

    fun startServer(port: Int) {
        this.bossGroup = this.nettyHelper.getEventLoopGroup("liruserver-boss")
        this.workerGroup = this.nettyHelper.getEventLoopGroup("liruserver-worker")

        val sslContext = this.nettyHelper.createServerCert()
        val channelClass = this.nettyHelper.getServerChannelClass()

        this.threadPool.execute({
            try {
                ServerBootstrap().group(this.bossGroup, this.workerGroup)
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
                            preparePipeline(sslContext, channel)
                        }
                    }).bind(port)
                    .addListener {
                        if (it.isSuccess) {
                            this.logger.info("Network Server started and was bound to 127.0.0.1:$port")
                        } else {
                            this.logger.error("Exception caught in Component: [SERVER] - Reason: [${it.cause().message}]")
                        }
                    }
                    .addListener(ChannelFutureListener.CLOSE_ON_FAILURE)
                    .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE)
                    .channel().closeFuture().syncUninterruptibly()
            } catch (e: Exception) {
                this.logger.error("Exception caught in Component: [SERVER] - Reason: [${e.message}]")
            } finally {
                this.workerGroup.shutdownGracefully()
                this.bossGroup.shutdownGracefully()
            }
        }, false)
    }

    fun shutdownGracefully() {
        this.workerGroup.shutdownGracefully()
        this.bossGroup.shutdownGracefully()
    }

    abstract fun preparePipeline(sslContext: SslContext?, channel: Channel)
}