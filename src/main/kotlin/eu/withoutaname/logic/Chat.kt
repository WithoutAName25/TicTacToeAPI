package eu.withoutaname.logic

import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.max

private const val HISTORY_LENGTH = 100

class Chat {
    private val history = Array<Message?>(HISTORY_LENGTH) { null }
    private val nextIdCounter = AtomicInteger(0)
    val nextId: Int
        get() = nextIdCounter.get()

    fun sendMessage(user: User, message: String) {
        val id = nextIdCounter.getAndIncrement()
        history[id % HISTORY_LENGTH] = Message(id, user.username, message)
    }

    fun getMessages(sinceId: Int = 0): List<Message> {
        val nextId = nextIdCounter.get()
        val start = if (sinceId < 0 || sinceId < nextId - HISTORY_LENGTH || sinceId > nextId) {
            max(0, nextId - HISTORY_LENGTH)
        } else {
            sinceId
        }
        if (start == nextId) return emptyList()

        @Suppress("UNCHECKED_CAST")
        return if (start % HISTORY_LENGTH > (nextId - 1) % HISTORY_LENGTH) {
            val firstSlice = history.slice(start until HISTORY_LENGTH)
            val secondSlice = history.slice(0 until nextId)
            firstSlice + secondSlice
        } else {
            history.slice(start until nextId)
        } as List<Message>
    }
}

data class Message(val id: Int, val username: String, val text: String)
