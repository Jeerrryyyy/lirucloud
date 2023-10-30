package de.liruhg.lirucloud.library.request

import java.io.InputStream

interface RequestFactory {

    fun newFactory(url: String): RequestFactory
    fun setRequestProperty(key: String, value: String): RequestFactory
    fun setUseCache(useCache: Boolean): RequestFactory
    fun setContentType(contentType: ContentType): RequestFactory
    fun setRequestMethod(requestMethod: RequestMethod): RequestFactory
    fun setReadTimeout(timeout: Int): RequestFactory
    fun setConnectTimeout(timeout: Int): RequestFactory
    fun fire(): InputStream
    fun fireAndForget()
}