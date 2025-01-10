package eu.withoutaname.routes

import com.papsign.ktor.openapigen.APIException
import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import com.papsign.ktor.openapigen.route.path.normal.get
import com.papsign.ktor.openapigen.route.path.normal.post
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import com.papsign.ktor.openapigen.route.throws
import eu.withoutaname.logic.Lobby
import eu.withoutaname.logic.Users
import eu.withoutaname.plugins.UserSession
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.sessions.*

fun NormalOpenAPIRoute.lobbyRoutes() {
    route("lobby").throws(invalidSessionException, missingFieldExeption) {
        route("status")
            .get<SessionParameter, LobbyStatus>(
                example = LobbyStatus.InLobby(
                    listOf(
                        RoomData("CoolRoom", listOf("PlayerA"), true),
                        RoomData("Room2", listOf("PlayerC", "Bot1"), false)
                    )
                )
            ) {
                val session = pipeline.call.sessions.get<UserSession>() ?: throw InvalidSessionException()
                val user = Users.getUser(session)
                val room = user.room
                if (room == null) {
                    respond(Lobby.status)
                } else {
                    respond(room.status)
                }
            }
        route("create-room")
            .throws(alreadyInRoomException, badRequestException, roomNameUnavailableException)
            .post<SessionParameter, Response, RoomRequest>(
                exampleRequest = RoomRequest("MyRoom"),
                exampleResponse = Success
            ) { _, request ->
                val session = pipeline.call.sessions.get<UserSession>() ?: throw InvalidSessionException()
                val user = Users.getUser(session)
                if (user.room != null) throw AlreadyInRoomException()

                val room = Lobby.createRoom(request.name)
                room.join(user)
                respond(Success)
            }
        route("join-room")
            .throws(alreadyInRoomException, badRequestException, roomNotFoundException)
            .post<SessionParameter, Response, RoomRequest>(
                exampleRequest = RoomRequest("MyRoom"),
                exampleResponse = Success
            ) { _, request ->
                val session = pipeline.call.sessions.get<UserSession>() ?: throw InvalidSessionException()
                val user = Users.getUser(session)
                if (user.room != null) throw AlreadyInRoomException()

                val room = Lobby.getRoom(request.name) ?: throw RoomNotFoundException()
                room.join(user)
                respond(Success)
            }
        route("leave-room")
            .throws(badRequestException)
            .post<SessionParameter, Response, Unit>(
                exampleResponse = Success
            ) { _, _ ->
                val session = pipeline.call.sessions.get<UserSession>() ?: throw InvalidSessionException()
                val user = Users.getUser(session)
                user.room?.leave(user)
                respond(Success)
            }
    }
}

data class RoomRequest(val name: String)

class AlreadyInRoomException : Exception()

object AlreadyInRoomError : Error("Already in other room")

val alreadyInRoomException = APIException.apiException<AlreadyInRoomException, Error>(
    HttpStatusCode.Forbidden,
    AlreadyInRoomError
) {
    AlreadyInRoomError
}

class NotInRoomException : Exception()

object NotInRoomError : Error("Join a room first!")

val notInRoomException = APIException.apiException<NotInRoomException, Error>(
    HttpStatusCode.Forbidden,
    NotInRoomError
) {
    NotInRoomError
}

class RoomNameUnavailableException : Exception()

object RoomNameUnavailableError : Error("Room name is not available")

val roomNameUnavailableException = APIException.apiException<RoomNameUnavailableException, Error>(
    HttpStatusCode.Forbidden,
    RoomNameUnavailableError
) {
    RoomNameUnavailableError
}

class RoomNotFoundException : Exception()

object RoomNotFoundError : Error("Room not found")

val roomNotFoundException = APIException.apiException<RoomNotFoundException, Error>(
    HttpStatusCode.NotFound,
    RoomNotFoundError
) {
    RoomNotFoundError
}

data class RoomData(val name: String, val users: List<String>, val hasGame: Boolean)

sealed class LobbyStatus(val status: String) : Response(true) {
    data class InLobby(val rooms: List<RoomData>) : LobbyStatus("in-lobby")

    data class InRoom(val room: RoomData) : LobbyStatus("in-room")
}
