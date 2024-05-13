package eu.withoutaname

import eu.withoutaname.plugins.configureHTTP
import eu.withoutaname.plugins.configureRouting
import eu.withoutaname.plugins.configureSerialization
import eu.withoutaname.plugins.configureSockets
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureHTTP()
    configureSerialization()
    configureSockets()
    configureRouting()
}
