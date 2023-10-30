package de.liruhg.lirucloud.master.web.route

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import de.liruhg.lirucloud.library.network.helper.NettyHelper
import de.liruhg.lirucloud.library.router.Route
import de.liruhg.lirucloud.library.util.RoundUtils
import de.liruhg.lirucloud.master.client.ClientRegistry
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.FullHttpRequest
import io.netty.handler.codec.http.HttpResponse
import io.netty.handler.codec.http.HttpResponseStatus

class StatusRoute(
    private val nettyHelper: NettyHelper,
    private val clientRegistry: ClientRegistry
) : Route() {

    override fun handle(channelHandlerContext: ChannelHandlerContext, fullHttpRequest: FullHttpRequest): HttpResponse {
        val jsonObject = JsonObject()
        jsonObject.addProperty("status", "online")
        jsonObject.addProperty("version", "0.0.1")

        val clientInfos = JsonArray()

        this.clientRegistry.getClients().forEach {
            val name = "${it.name}${it.delimiter}${it.suffix}"

            val clientInfo = JsonObject()
            clientInfo.addProperty("uuid", it.uuid)
            clientInfo.addProperty("name", name)
            clientInfo.addProperty("currentOnlineServers", it.currentOnlineServers)
            clientInfo.addProperty("currentMemoryUsage", it.currentMemoryUsage)
            clientInfo.addProperty("currentCpuUsage", RoundUtils.roundDouble(it.currentCpuUsage, 2))

            clientInfos.add(clientInfo)
        }

        jsonObject.add("clientInfos", clientInfos)

        return this.nettyHelper.createHttpResponse(jsonObject, HttpResponseStatus.OK)
    }
}