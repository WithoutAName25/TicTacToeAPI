package eu.withoutaname.logic

import eu.withoutaname.plugins.UserSession
import eu.withoutaname.routes.UserStatus
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

object Users {
    private val users = mutableMapOf<Int, User>()
    private val lastAccessed = mutableMapOf<Int, Instant>()
    private const val INACTIVITY_THRESHOLD = 3600 // In seconds (1 hour)

    fun addUser(user: User): UserSession {
        val userSession = UserSession()
        synchronized(users) {
            users[userSession.id] = user
        }
        synchronized(lastAccessed) {
            lastAccessed[userSession.id] = Clock.System.now()
        }
        return userSession
    }

    fun getUser(userSession: UserSession): User {
        synchronized(lastAccessed) {
            lastAccessed[userSession.id] = Clock.System.now()
        }
        return users[userSession.id]!!
    }

    fun deleteUser(userSession: UserSession) {
        synchronized(users) {
            users.remove(userSession.id)?.let {
                it.room?.leave(it)
            }
        }
    }

    fun cleanup() {
        val now = Clock.System.now()
        synchronized(lastAccessed) {
            lastAccessed.entries.removeIf { (id, lastAccessedTime) ->
                val inactiveDuration = now.epochSeconds - lastAccessedTime.epochSeconds
                val remove = inactiveDuration > INACTIVITY_THRESHOLD
                if (remove) synchronized(users) {
                    val user = users.remove(id)
                    if (user != null) {
                        user.room?.leave(user)
                    }
                }
                remove
            }
        }
    }
}

class User(val username: String) {
    var room: Room? = null
    val status: UserStatus
        get() = UserStatus(username)
}
