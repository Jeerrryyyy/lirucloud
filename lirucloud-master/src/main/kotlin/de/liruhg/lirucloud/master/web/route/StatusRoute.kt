package de.liruhg.lirucloud.master.web.route

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import de.liruhg.lirucloud.library.network.helper.NettyHelper
import de.liruhg.lirucloud.library.process.ProcessType
import de.liruhg.lirucloud.library.router.Route
import de.liruhg.lirucloud.library.util.RoundUtils
import de.liruhg.lirucloud.master.client.ClientRegistry
import de.liruhg.lirucloud.master.process.registry.ProcessRegistry
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.FullHttpRequest
import io.netty.handler.codec.http.HttpResponse
import io.netty.handler.codec.http.HttpResponseStatus

class StatusRoute(
    private val nettyHelper: NettyHelper,
    private val clientRegistry: ClientRegistry,
    private val processRegistry: ProcessRegistry
) : Route() {

    override fun handle(channelHandlerContext: ChannelHandlerContext, fullHttpRequest: FullHttpRequest): HttpResponse {
        val jsonObject = JsonObject()
        jsonObject.addProperty("status", "online")
        jsonObject.addProperty("version", "0.0.1")

        val clientInfos = JsonArray()

        this.clientRegistry.getClients().forEach { client ->
            val name = "${client.name}${client.delimiter}${client.suffix}"

            val clientInformation = JsonObject()
            clientInformation.addProperty("uuid", client.uuid)
            clientInformation.addProperty("name", name)
            clientInformation.addProperty("currentOnlineServers", client.currentOnlineServers)
            clientInformation.addProperty("currentMemoryUsage", client.currentMemoryUsage)
            clientInformation.addProperty("currentCpuUsage", RoundUtils.roundDouble(client.currentCpuUsage, 2))
            clientInformation.add("runningProcesses", JsonArray().apply { client.runningProcesses.forEach { add(it) } })

            clientInfos.add(clientInformation)
        }

        jsonObject.add("clientInformation", clientInfos)
        jsonObject.add("proxyProcessInformation", this.generateProxyJsonOutput())
        jsonObject.add("serverProcessInformation", this.generateServerJsonOutput())

        return this.nettyHelper.createHttpResponse(jsonObject, HttpResponseStatus.OK)
    }

    private fun generateProxyJsonOutput(): JsonObject {
        val jsonObject = JsonObject()
        jsonObject.addProperty("running", this.processRegistry.getRunningProcessCount())

        val proxyProcesses = JsonArray()

        this.processRegistry.getProcesses().filter { it.type == ProcessType.PROXY }.forEach { process ->
            val proxyProcess = JsonObject()

            proxyProcess.addProperty("name", process.name)
            proxyProcess.addProperty("uuid", process.uuid)
            proxyProcess.addProperty("type", process.type.name)
            proxyProcess.addProperty("stage", process.stage.name)

            proxyProcesses.add(proxyProcess)
        }

        jsonObject.add("proxyProcesses", proxyProcesses)

        return jsonObject
    }

    private fun generateServerJsonOutput(): JsonObject {
        val jsonObject = JsonObject()
        jsonObject.addProperty("running", this.processRegistry.getRunningProcessCount())

        val serverProcesses = JsonArray()

        this.processRegistry.getProcesses().filter { it.type == ProcessType.SERVER }.forEach { process ->
            val serverProcess = JsonObject()

            serverProcess.addProperty("name", process.name)
            serverProcess.addProperty("uuid", process.uuid)
            serverProcess.addProperty("type", process.type.name)
            serverProcess.addProperty("stage", process.stage.name)
            serverProcess.addProperty("mode", process.mode.name)

            serverProcesses.add(serverProcess)
        }

        jsonObject.add("proxyProcesses", serverProcesses)

        return jsonObject
    }
}