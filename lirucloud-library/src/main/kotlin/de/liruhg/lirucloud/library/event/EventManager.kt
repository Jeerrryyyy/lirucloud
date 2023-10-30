package de.liruhg.lirucloud.library.event

import de.liruhg.lirucloud.library.event.listener.EventHandler
import de.liruhg.lirucloud.library.event.listener.EventListener
import java.lang.reflect.Method

class EventManager {

    private val listeners: MutableList<Class<out EventListener>> = mutableListOf()

    fun registerListener(eventListener: EventListener) {
        this.listeners.add(eventListener::class.java)
    }

    inline fun <reified T : Event> listen(crossinline consumer: suspend EventListener.(T) -> Unit): EventListener {
        return object : EventListener() {
            @EventHandler
            suspend fun handleEvent(event: Event) {
                if (event is T) consumer(event)
            }
        }.also { this.registerListener(it) }
    }

    fun fireEvent(event: Event) {
        val eventClazz = event::class.java

        for (listener in this.listeners) {
            val listenerMethods = this.getListenerMethods(listener) ?: continue

            for (method in listenerMethods) {
                if (!this.isMethodValid(method)) continue

                val parameterTypes = this.getParameterTypes(method) ?: continue

                for (parameterType in parameterTypes) {
                    if (method.parameterCount == 1) {
                        if (parameterType != eventClazz) continue

                        method.invoke(listener.getConstructor().newInstance(), event)
                    } else {
                        if (parameterType !in eventClazz.genericInterfaces) continue

                        method.invoke(listener.getConstructor().newInstance(), event, null)
                    }
                }
            }
        }
    }

    private fun getListenerMethods(listener: Class<out EventListener>): Array<Method>? {
        return if (listener.methods.isNotEmpty()) listener.methods else null
    }

    private fun getParameterTypes(method: Method): Array<Class<*>>? {
        return if (method.parameterTypes.isNotEmpty()) method.parameterTypes else null
    }

    private fun isMethodValid(method: Method): Boolean {
        return method.isAnnotationPresent(EventHandler::class.java) && method.parameterTypes.isNotEmpty()
    }
}