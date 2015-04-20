package market.events

import market.model.*
import market.model.server.*
import java.util.concurrent.ConcurrentHashMap

trait ItemEvent<out T> {
    val item: T
}
class ItemPlaced<T>(override val item: T) : ItemEvent<T>
class ItemCancelled<T>(override val item: T) : ItemEvent<T>
class ItemCompleted<T>(override val item: T) : ItemEvent<T>

trait ItemEventListener<in T> {
    fun onEvent(event : ItemEvent<T>)
}

val ItemEvent<Order>.order : Order
    get() = item

val ItemEvent<OrderOnStack>.orderOnStack : OrderOnStack
    get() = item

val ItemEvent<OrderTrade>.trade : OrderTrade
    get() = item


fun <T> ItemEventListener<T>.place(e : T) = onEvent(ItemPlaced(e))
fun <T> ItemEventListener<T>.cancel(e : T) = onEvent(ItemCancelled(e))
fun <T> ItemEventListener<T>.complete(e : T) = onEvent(ItemCompleted(e))

class CompositeListener<T>(val liseners : List<ItemEventListener<T>>) : ItemEventListener<T> {
    override fun onEvent(event: ItemEvent<T>) {
        liseners.forEach { it.onEvent(event) }
    }
}
