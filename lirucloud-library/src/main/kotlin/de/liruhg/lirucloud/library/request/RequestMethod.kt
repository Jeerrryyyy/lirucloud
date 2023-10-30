package de.liruhg.lirucloud.library.request

enum class RequestMethod(val definedName: String) {

    GET("GET"),
    POST("POST"),
    HEAD("HEAD"),
    OPTIONS("OPTIONS"),
    PUT("PUT"),
    DELETE("DELETE"),
    TRACE("TRACE")
}