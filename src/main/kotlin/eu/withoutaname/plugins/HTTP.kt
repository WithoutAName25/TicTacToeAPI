package eu.withoutaname.plugins

import com.papsign.ktor.openapigen.OpenAPIGen
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.conditionalheaders.*
import io.ktor.server.plugins.cors.routing.*

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
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowHeader(HttpHeaders.Authorization)
        allowHost("mineplay.link", listOf("https"))
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
}
