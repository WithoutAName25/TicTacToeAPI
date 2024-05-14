package eu.withoutaname.routes

import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import com.papsign.ktor.openapigen.route.path.normal.get
import com.papsign.ktor.openapigen.route.path.normal.post
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import com.papsign.ktor.openapigen.route.throws
import eu.withoutaname.logic.User
import eu.withoutaname.logic.Users
import eu.withoutaname.plugins.UserSession
import io.ktor.server.application.*
import io.ktor.server.sessions.*

fun NormalOpenAPIRoute.userRoutes() {
    route("user") {
        route("set-name")
            .throws(badRequestException)
            .post<Unit, Response, SetNameRequest>(
                exampleRequest = SetNameRequest("MyUserName"),
                exampleResponse = Response(true)
            ) { _, request ->
                val session = Users.addUser(User(request.username))
                pipeline.call.sessions.set(session)
                respond(Success)
            }
        route("status")
            .throws(invalidSessionException, missingFieldExeption)
            .get<SessionParameter, UserStatus>(
                example = UserStatus("MyUserName")
            ) {
                val session = pipeline.call.sessions.get<UserSession>() ?: throw InvalidSessionException()
                respond(Users.getUser(session).status)
            }
    }
}

data class SetNameRequest(val username: String)

data class UserStatus(val username: String) : Response(true)
