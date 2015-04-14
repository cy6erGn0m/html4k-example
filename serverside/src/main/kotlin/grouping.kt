package market.util.grouping

import market.events.ItemEvent
import market.events.ItemEventListener
import java.util.ArrayList
import java.util.HashSet
import java.util.LinkedHashSet
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread
import kotlin.concurrent.withLock

class GroupInfo<T, Q>(val group : T, val out : BlockingQueue<in Q>) {
    val count = AtomicInteger(0)
}

fun <Q> GroupInfo<*, in Q>.put(event: Q) {
    count.incrementAndGet()
    out.put(event)
}

class GroupingHandler<T, Q> (val workers : Int = Runtime.getRuntime().availableProcessors(), val groupFunction : (Q) -> T, val handler : (Q) -> Unit) {
    private val lock = ReentrantLock()
    private val routeTable = ConcurrentHashMap<T, GroupInfo<T, Q>>()
    private val workerGroups = ConcurrentHashMap(workers.indices.map {it to HashSet<T>()}.toMap())
    private val groupQueues = workers.indices.map { LinkedBlockingQueue<Q>() }
    private val threads = workers.indices.map { id -> thread { Executor(groupQueues[id]).run() } }

    fun put(event : Q) {
        val group = groupFunction(event)

        val groupInfo = lock.withLock {
            if (routeTable.size() > 10000) {
                purge()
            }

            val existingRoute = routeTable[group]
            if (existingRoute != null) {
                existingRoute
            } else {
                val index = workerGroups.entrySet().sortDescendingBy { it.getValue().size() }.first().getKey()
                val queue = groupQueues[index]
                val groupInfo = GroupInfo(group, queue)
                routeTable[group] = groupInfo

                groupInfo
            }
        }

        groupInfo.put(event)
    }

    fun stop() {
        threads.forEach { it.interrupt() }
    }

    fun join() {
        threads.forEach { it.join() }
    }

    private fun purge() {
        lock.withLock {
            routeTable.values().filter {it.count.get() == 0}.map {it.group}.forEach {
                routeTable.remove(it)
            }
        }
    }

    inner class Executor(val queue : BlockingQueue<Q>) : Runnable {
        override fun run() {
            try {
                do {
                    handler(queue.take())
                } while(true)
            } catch (ignore : InterruptedException) {
            }
        }
    }
}

fun <T> GroupingHandler<*, ItemEvent<T>>.toEventListener() : ItemEventListener<T> = object: ItemEventListener<T> {
    override fun onEvent(event: ItemEvent<T>) {
        this@toEventListener.put(event)
    }
}