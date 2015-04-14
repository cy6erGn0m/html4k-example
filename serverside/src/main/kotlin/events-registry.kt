package market.events

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

object EventListenersRegistry {
    private val listeners = ConcurrentHashMap<Class<*>, CopyOnWriteArrayList<ItemEventListener<*>>>()

    inline fun <reified T> register(listener : ItemEventListener<T>) {
        register(javaClass<T>(), listener)
    }

    inline fun <reified T> send(event: ItemEvent<T>) {
        send(javaClass<T>(), event)
    }

    fun <T> register(jc : Class<T>, listener : ItemEventListener<T>) {
        if (jc !in listeners) {
            listeners[jc] = CopyOnWriteArrayList<ItemEventListener<*>>()
        }

        listeners[jc].add(listener)
    }

    fun <T> send(jc : Class<T>, event : ItemEvent<T>) {
        listeners[jc]?.forEach {
            it.onEvent(event)
        }
    }
}

