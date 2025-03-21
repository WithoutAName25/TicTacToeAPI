package eu.withoutaname

import com.papsign.ktor.openapigen.route.apiRouting
import eu.withoutaname.logic.Users
import eu.withoutaname.plugins.*
import eu.withoutaname.routes.chatRoutes
import eu.withoutaname.routes.gameRoutes
import eu.withoutaname.routes.lobbyRoutes
import eu.withoutaname.routes.userRoutes
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun main() {
    CoroutineScope(Dispatchers.IO).launch {
        while (true) {
            Users.cleanup()
            delay(60000L) // 1 minute in milliseconds
        }
    }

    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module).start(wait = true)
}

fun Application.module() {
    configureHTTP()
    configureSerialization()
    configureSession()
    configureSockets()
    configureRouting()
    apiRouting {
        chatRoutes()
        gameRoutes()
        lobbyRoutes()
        userRoutes()
    }
}

