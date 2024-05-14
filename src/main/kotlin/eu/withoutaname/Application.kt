package eu.withoutaname

import com.papsign.ktor.openapigen.route.apiRouting
import eu.withoutaname.plugins.*
import eu.withoutaname.routes.chatRoutes
import eu.withoutaname.routes.gameRoutes
import eu.withoutaname.routes.lobbyRoutes
import eu.withoutaname.routes.userRoutes
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
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

