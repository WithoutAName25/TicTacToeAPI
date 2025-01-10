package eu.withoutaname.logic

import eu.withoutaname.plugins.UserSession
import eu.withoutaname.routes.UserStatus

object Users {
    private val users = mutableMapOf<Int, User>()

    fun addUser(user: User): UserSession {
        val userSession = UserSession()
        synchronized(users) {
            users[userSession.id] = user
        }
        return userSession
    }

    fun getUser(userSession: UserSession) = users[userSession.id]!!
}

class User(val username: String) {
    var room: Room? = null
    val status: UserStatus
        get() = UserStatus(username)
}
