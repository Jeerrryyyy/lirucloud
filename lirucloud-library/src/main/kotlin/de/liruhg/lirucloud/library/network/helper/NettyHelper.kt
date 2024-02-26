package de.liruhg.lirucloud.library.network.helper

import com.google.gson.JsonObject
import de.liruhg.lirucloud.library.thread.InternalThreadFactory
import io.netty.buffer.Unpooled
import io.netty.channel.EventLoopGroup
import io.netty.channel.epoll.Epoll
import io.netty.channel.epoll.EpollEventLoopGroup
import io.netty.channel.epoll.EpollServerSocketChannel
import io.netty.channel.epoll.EpollSocketChannel
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.ServerSocketChannel
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.http.DefaultFullHttpResponse
import io.netty.handler.codec.http.HttpResponse
import io.netty.handler.codec.http.HttpResponseStatus
import io.netty.handler.codec.http.HttpVersion
import io.netty.handler.ssl.SslContext
import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.util.InsecureTrustManagerFactory
import io.netty.handler.ssl.util.SelfSignedCertificate
import io.netty.util.CharsetUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.security.cert.CertificateException
import javax.net.ssl.SSLException

class NettyHelper {

    private val logger: Logger = LoggerFactory.getLogger(NettyHelper::class.java)

    fun getEventLoopGroup(threadPrefix: String): EventLoopGroup {
        return if (this.isEpoll()) EpollEventLoopGroup(InternalThreadFactory(threadPrefix))
        else NioEventLoopGroup(InternalThreadFactory(threadPrefix))
    }

    fun getClientChannelClass(): Class<out SocketChannel> {
        return if (this.isEpoll()) EpollSocketChannel::class.java else NioSocketChannel::class.java
    }

    fun getServerChannelClass(): Class<out ServerSocketChannel> {
        return if (this.isEpoll()) EpollServerSocketChannel::class.java else NioServerSocketChannel::class.java
    }

    fun createClientCert(): SslContext? {
        var sslContext: SslContext? = null

        try {
            sslContext = SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build()
        } catch (e: CertificateException) {
            this.logger.error("Exception caught in component Component: [CLIENT_CERT] - Reason: [${e.message}]")
        } catch (e: SSLException) {
            this.logger.error("Exception caught in component Component: [CLIENT_CERT] - Reason: [${e.message}]")
        }

        return sslContext
    }

    fun createServerCert(): SslContext? {
        var sslContext: SslContext? = null

        try {
            val selfSignedCertificate = SelfSignedCertificate()

            sslContext = SslContextBuilder
                .forServer(selfSignedCertificate.certificate(), selfSignedCertificate.privateKey())
                .build()
        } catch (e: CertificateException) {
            this.logger.error("Exception caught in Component: [SERVER_CERT] - Reason: [${e.message}]")
        } catch (e: SSLException) {
            this.logger.error("Exception caught in Component: [SERVER_CERT] - Reason: [${e.message}]")
        }

        return sslContext
    }

    fun isEpoll(): Boolean {
        return Epoll.isAvailable()
    }

    fun createHttpResponse(content: JsonObject, statusCode: HttpResponseStatus): HttpResponse {
        val httpResponse = DefaultFullHttpResponse(
            HttpVersion.HTTP_1_1,
            statusCode,
            Unpooled.copiedBuffer("$content\r\n", CharsetUtil.UTF_8)
        )

        httpResponse.headers().set("content-type", "application/json; charset=utf-8")
        return httpResponse
    }
}
