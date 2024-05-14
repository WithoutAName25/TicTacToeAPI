package eu.withoutaname.routes

import com.papsign.ktor.openapigen.annotations.parameters.HeaderParam
import com.papsign.ktor.openapigen.annotations.parameters.QueryParam
import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import com.papsign.ktor.openapigen.route.path.normal.get
import com.papsign.ktor.openapigen.route.path.normal.post
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import com.papsign.ktor.openapigen.route.throws
import eu.withoutaname.logic.Lobby
import eu.withoutaname.logic.Message
import eu.withoutaname.logic.Users
import eu.withoutaname.plugins.UserSession
import io.ktor.server.application.*
import io.ktor.server.sessions.*


fun NormalOpenAPIRoute.chatRoutes() {
    route("chat").throws(invalidSessionException, missingFieldExeption) {
        throws(badRequestException)
            .post<SessionParameter, Response, MessageRequest>(
                exampleRequest = MessageRequest("Hello World!"), exampleResponse = Success
            ) { _, request ->
                val session = pipeline.call.sessions.get<UserSession>() ?: throw InvalidSessionException()
                val user = Users.getUser(session)
                val chat = user.room?.chat ?: Lobby.chat
                chat.sendMessage(user, request.message)
                respond(Success)
            }

        get<MessagesParameters, MessagesResponse>(
            example = MessagesResponse(
                listOf(
                    Message(8, "User1", "Hello World!"),
                    Message(9, "User2", "Hi there"),
                    Message(10, "User1", "How are you?")
                ), 11
            )
        ) {
            val session = pipeline.call.sessions.get<UserSession>() ?: throw InvalidSessionException()
            val user = Users.getUser(session)
            val chat = user.room?.chat ?: Lobby.chat
            val messages = chat.getMessages(it.sinceId ?: 0)
            respond(MessagesResponse(messages, chat.nextId))
        }
    }
}

data class MessageRequest(val message: String)

data class MessagesParameters(
    @HeaderParam("User session (obtained by setting the username)") val user_session: String,
    @QueryParam("Since which message you want the history") val sinceId: Int?
)

data class MessagesResponse(val messages: List<Message>, val nextId: Int) : Response(true)
