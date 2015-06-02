package market.web.impl

import market.web.*
import rx.Observable
import rx.Subscriber
import rx.subjects.BehaviorSubject
import rx.subjects.PublishSubject
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

data class Quote(val instrument: String, val value: Double)
private val tickerPool = Executors.newScheduledThreadPool(4) {
    Thread(it, "ticker-${java.lang.Long.toHexString(System.currentTimeMillis())}")
}

private val quotes = ConcurrentHashMap<String, Double>()
fun createTicker(instrumentId: String) =
    Observable.create<Quote> { subscriber ->
        scheduleTick(instrumentId, subscriber)
    }

private fun scheduleTick(instrumentId: String, subscriber: Subscriber<in Quote>) {
    tickerPool.schedule({
        if (subscriber.isUnsubscribed()) {
            subscriber.onCompleted()
            return@schedule
        }

        val oldTick = quotes[instrumentId] ?: (Math.random() * 1000.0)
        val delta = oldTick * 0.01 * (Math.random() * 2.0 - 1.0)
        val newTick = oldTick + delta
        quotes[instrumentId] = newTick

        subscriber.onNext(Quote(instrumentId, newTick))
        scheduleTick(instrumentId, subscriber)
    }, randomInterval(), TimeUnit.MILLISECONDS)
}

private fun randomInterval() = Math.max(50.0, Math.abs(Math.random() - 0.5) * 1000.0).toLong()

val ticksSubject = PublishSubject.create<Quote>()
fun initTicksSubject() = Observable.range(1, 100).map { "INSTR-$it" }.flatMap {
    val s = BehaviorSubject.create<Quote>()

    createTicker(it).subscribe(s)

    s
}.subscribe(ticksSubject)
