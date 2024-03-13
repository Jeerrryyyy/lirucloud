package de.liruhg.lirucloud.library.request

import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URI

class DefaultRequestFactory : RequestFactory {

    private lateinit var httpUrlConnection: HttpURLConnection

    override fun newFactory(url: String): RequestFactory {
        HttpURLConnection.setFollowRedirects(true)
        this.httpUrlConnection = URI(url).toURL().openConnection() as HttpURLConnection
        return this
    }

    override fun setRequestProperty(key: String, value: String): RequestFactory {
        this.httpUrlConnection.setRequestProperty(key, value)
        return this
    }

    override fun setUseCache(useCache: Boolean): RequestFactory {
        this.httpUrlConnection.useCaches = useCache
        return this
    }

    override fun setContentType(contentType: ContentType): RequestFactory {
        this.httpUrlConnection.setRequestProperty("Content-Type", contentType.definedType)
        return this
    }

    override fun setRequestMethod(requestMethod: RequestMethod): RequestFactory {
        this.httpUrlConnection.requestMethod = requestMethod.definedName
        return this
    }

    override fun setReadTimeout(timeout: Int): RequestFactory {
        this.httpUrlConnection.readTimeout = timeout
        return this
    }

    override fun setConnectTimeout(timeout: Int): RequestFactory {
        this.httpUrlConnection.connectTimeout = timeout
        return this
    }

    override fun fire(): InputStream {
        this.httpUrlConnection.connect()
        return this.httpUrlConnection.inputStream
    }

    override fun fireAndForget() {
        this.httpUrlConnection.connect()
        this.httpUrlConnection.disconnect()
    }
}