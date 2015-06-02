package serverutils

import java.io.File
import java.io.Writer
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantLock
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.withLock

object CacheConfig {
    var cacheRoot = Files.createTempDirectory("kotlin-server-cache-")!! // TODO make it configurable

    init {
        println("Cache configured for directory ${cacheRoot}")
    }
}

private val cacheLocks = ConcurrentHashMap<String, ReadWriteLock>()

private fun lock(key: String): ReadWriteLock {
    val lock = ReentrantReadWriteLock()
    return cacheLocks.putIfAbsent(key, lock) ?: lock
}

fun Writer.withCache(vararg keys: Any?, block: Writer.() -> Unit) {
    val cacheKey = keys.toList().toEtag().etagToFileName()
    val cacheFile = CacheConfig.cacheRoot.resolve(cacheKey)

    val lock = lock(cacheKey)

    while (true) {
        val gotCachedValue = lock.readLock().withLock {
            if (Files.exists(cacheFile)) {
                Files.newBufferedReader(cacheFile).use {
                    it.copyTo(this)
                }

                true
            } else {
                false
            }
        }

        if (gotCachedValue) {
            break
        }

        val completed = lock.writeLock().withLock {
            if (Files.exists(cacheFile)) {
                false
            } else {
                val tempFile = Files.createTempFile(CacheConfig.cacheRoot, "response-temp-", ".tmp")
                Files.newBufferedWriter(tempFile, Charsets.UTF_8).use { cacheWriter ->
                    TeeWriter(this, cacheWriter).block()
                }

                do {
                    try {
                        Files.move(tempFile, cacheFile, StandardCopyOption.ATOMIC_MOVE)
                        break
                    } catch (e: FileAlreadyExistsException) {
                        Files.deleteIfExists(cacheFile)
                    }
                } while (true)

                true
            }
        }

        if (completed) {
            break
        }
    }
}

private fun String.etagToFileName() = replace("[^0-9A-Za-z_.]+".toRegex()) { it.range.start.toString() }

private class TeeWriter(vararg outputs : Writer) : Writer() {
    private val outputs = outputs.toList()

    override fun write(cbuf: CharArray?, off: Int, len: Int) {
        outputs.forEach { it.write(cbuf, off, len) }
    }

    override fun flush() {
        outputs.forEach { it.flush() }
    }

    override fun close() {
        outputs.forEach { it.close() }
    }
}
