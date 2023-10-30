package de.liruhg.lirucloud.master.web.route

import com.google.gson.JsonObject
import de.liruhg.lirucloud.library.network.helper.NettyHelper
import de.liruhg.lirucloud.library.router.Route
import de.liruhg.lirucloud.library.user.CloudWebUser
import de.liruhg.lirucloud.master.web.repository.CloudWebUserRepository
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.FullHttpRequest
import io.netty.handler.codec.http.HttpResponse
import io.netty.handler.codec.http.HttpResponseStatus

class CreateUserRoute(
    private val nettyHelper: NettyHelper,
    private val cloudWebUserRepository: CloudWebUserRepository
) : Route() {

    override fun handle(channelHandlerContext: ChannelHandlerContext, fullHttpRequest: FullHttpRequest): HttpResponse {
        val jsonObject = JsonObject()

        val body = fullHttpRequest.content().toString(Charsets.UTF_8)
        val cloudWebUser = this.gson.fromJson(body, CloudWebUser::class.java)
        val foundCloudWebUser = this.cloudWebUserRepository.getCloudWebUser(cloudWebUser.email)

        if (foundCloudWebUser != null) {
            jsonObject.addProperty("status", "error")
            jsonObject.addProperty("message", "User already exists")

            return this.nettyHelper.createHttpResponse(jsonObject, HttpResponseStatus.CONFLICT)
        }

        this.cloudWebUserRepository.createCloudWebUser(cloudWebUser)

        jsonObject.addProperty("status", "success")
        jsonObject.addProperty("message", "User created")

        return this.nettyHelper.createHttpResponse(jsonObject, HttpResponseStatus.OK)
    }
}