package de.liruhg.lirucloud.library.router

class Router {

    private val routes: MutableMap<String, Pair<Route, Set<Middleware>>> = mutableMapOf()

    fun registerRoute(path: String, route: Route, vararg middleware: Middleware) {
        if (this.routes.containsKey(path)) return
        this.routes[path] = Pair(route, middleware.toSet())
    }

    fun getRoute(path: String): Pair<Route, Set<Middleware>>? {
        return this.routes[path]
    }
}