package eu.withoutaname.plugins

import com.papsign.ktor.openapigen.OpenAPIGen
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.conditionalheaders.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureHTTP() {
    install(CachingHeaders) {
        options { _, outgoingContent ->
            when (outgoingContent.contentType?.withoutParameters()) {
                ContentType.Text.CSS -> CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 60 * 60))
                else -> null
            }
        }
    }
    install(Compression) {
        gzip {
            priority = 1.0
            minimumSize(1024)
        }
        deflate {
            priority = 10.0
            minimumSize(1024)
        }
    }
    install(ConditionalHeaders)
    install(CORS) {
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Head)
        allowHeader(HttpHeaders.ContentType)
        exposeHeader(HttpHeaders.ContentType)
        allowHeader("user_session")
        exposeHeader("user_session")
        allowHost("api.tic-tac-toe.withoutaname.eu", listOf("https"))
        allowHost("mineplay.link", listOf("https"))
        allowHost("localhost")
        val allowedLocalhostRegex = Regex("https?://localhost:\\d+")
        allowOrigins(allowedLocalhostRegex::matches)
    }
//    install(ForwardedHeaders) // WARNING: for security, do not include this if not behind a reverse proxy
//    install(XForwardedHeaders) // WARNING: for security, do not include this if not behind a reverse proxy
    install(OpenAPIGen) {
        serveOpenApiJson = true
        serveSwaggerUi = true
        info {
            title = "TicTacToe API"
        }
    }
    routing {
        get {
            call.respondRedirect("/swagger-ui")
        }
    }
}
