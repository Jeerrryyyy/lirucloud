package de.liruhg.lirucloud.master.web

import com.google.gson.JsonObject
import de.liruhg.lirucloud.library.network.helper.NettyHelper
import de.liruhg.lirucloud.library.router.Router
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.http.FullHttpRequest
import io.netty.handler.codec.http.HttpResponseStatus
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class WebHandler(
    private val router: Router,
    private val nettyHelper: NettyHelper
) : SimpleChannelInboundHandler<FullHttpRequest>() {

    private val logger: Logger = LoggerFactory.getLogger(WebHandler::class.java)

    override fun channelRead0(channelHandlerContext: ChannelHandlerContext, fullHttpRequest: FullHttpRequest) {
        val uri = fullHttpRequest.uri().split("?")[0]

        if (!fullHttpRequest.decoderResult().isSuccess) {
            this.sendError(channelHandlerContext, HttpResponseStatus.BAD_REQUEST, uri)
            return
        }

        val route = router.getRoute(uri)

        if (route == null) {
            this.sendError(channelHandlerContext, HttpResponseStatus.NOT_FOUND, uri)
            return
        }

        for (middleware in route.second) {
            val httpResponse = middleware.handle(channelHandlerContext, fullHttpRequest)

            if (httpResponse != null) {
                this.logger.info(
                    "Handled http request on Middleware: [$uri] - Status: [${httpResponse.status()}] - Remote: [${
                        channelHandlerContext.channel().remoteAddress().toString()
                            .replace("/", "")
                            .split("]:")[0]
                            .replace("[", "")
                    }]"
                )

                channelHandlerContext.writeAndFlush(httpResponse).addListener(ChannelFutureListener.CLOSE)
                return
            }
        }

        val httpResponse = route.first.handle(channelHandlerContext, fullHttpRequest)

        this.logger.info(
            "Handled http request on Route: [$uri] - Status: [${httpResponse.status()}] - Remote: [${
                channelHandlerContext.channel().remoteAddress().toString()
                    .replace("/", "")
                    .split("]:")[0]
                    .replace("[", "")
            }]"
        )

        channelHandlerContext.writeAndFlush(httpResponse).addListener(ChannelFutureListener.CLOSE)
    }

    @Deprecated("Deprecated in Java")
    override fun exceptionCaught(channelHandlerContext: ChannelHandlerContext, cause: Throwable) {
        if (!channelHandlerContext.channel().isActive) {
            return
        }

        this.sendError(channelHandlerContext, HttpResponseStatus.INTERNAL_SERVER_ERROR, cause.message ?: "Unknown")
        return
    }

    private fun sendError(
        channelHandlerContext: ChannelHandlerContext,
        httpResponseStatus: HttpResponseStatus,
        uri: String
    ) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("status", "error")
        jsonObject.addProperty("message", httpResponseStatus.reasonPhrase())

        this.logger.info(
            "Handled http request failure on Route: [$uri] - Status: [${httpResponseStatus}] - Remote: [${
                channelHandlerContext.channel().remoteAddress().toString()
                    .replace("/", "")
                    .split("]:")[0]
                    .replace("[", "")
            }]"
        )

        channelHandlerContext.writeAndFlush(nettyHelper.createHttpResponse(jsonObject, httpResponseStatus))
            .addListener(ChannelFutureListener.CLOSE)
    }
}