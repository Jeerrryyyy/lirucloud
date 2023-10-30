package de.liruhg.lirucloud.library.router

import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.FullHttpRequest
import io.netty.handler.codec.http.HttpResponse

interface Middleware {

    fun handle(channelHandlerContext: ChannelHandlerContext, fullHttpRequest: FullHttpRequest): HttpResponse?
}