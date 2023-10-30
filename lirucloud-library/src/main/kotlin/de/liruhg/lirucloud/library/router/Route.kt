package de.liruhg.lirucloud.library.router

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.FullHttpRequest
import io.netty.handler.codec.http.HttpResponse

abstract class Route {

    val gson: Gson = GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create()

    abstract fun handle(channelHandlerContext: ChannelHandlerContext, fullHttpRequest: FullHttpRequest): HttpResponse
}