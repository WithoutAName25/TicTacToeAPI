package eu.withoutaname.plugins

import io.ktor.server.application.*
import io.ktor.server.sessions.*

data class UserSession(val id: Int = UserSession.id) {
    companion object {
        private var id = 0
            get() {
                return field++
            }
    }
}

fun Application.configureSession() {
    install(Sessions) {
        header<UserSession>("user_session", SessionStorageMemory())
    }
}
