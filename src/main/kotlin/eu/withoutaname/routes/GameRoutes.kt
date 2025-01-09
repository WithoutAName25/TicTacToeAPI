package eu.withoutaname.routes

import com.papsign.ktor.openapigen.APIException
import com.papsign.ktor.openapigen.annotations.type.number.integer.clamp.Clamp
import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import com.papsign.ktor.openapigen.route.path.normal.get
import com.papsign.ktor.openapigen.route.path.normal.post
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import com.papsign.ktor.openapigen.route.throws
import eu.withoutaname.logic.*
import eu.withoutaname.plugins.UserSession
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.sessions.*


fun NormalOpenAPIRoute.gameRoutes() {
    route("game").throws(invalidSessionException, missingFieldExeption) {
        route("play")
            .throws(
                badRequestException,
                notInRoomException,
                notYourTurnException,
                illegalMoveException,
                noGameStartedException
            )
            .post<SessionParameter, GameStatus, GamePlayRequest>(
                exampleRequest = GamePlayRequest(2, 0),
                exampleResponse = GameStatus(
                    arrayOf(
                        arrayOf(Field.EMPTY, Field.EMPTY, Field.X),
                        arrayOf(Field.O, Field.X, Field.EMPTY),
                        arrayOf(Field.EMPTY, Field.EMPTY, Field.EMPTY)
                    ),
                    PlayerType.O,
                    true,
                    false,
                    false,
                    false,
                    null
                )
            ) { _, request ->
                val session = pipeline.call.sessions.get<UserSession>() ?: throw InvalidSessionException()
                val user = Users.getUser(session)
                val room = user.room ?: throw NotInRoomException()
                val game = room.game ?: throw NoGameStartedException()

                game.play(user, request.x, request.y)

                respond(game.getGameStatus(user))
            }

        route("start")
            .throws(badRequestException, gameRunningException, notEnoughPlayersException, notRoomOwnerException)
            .post<SessionParameter, GameStatus, Unit>(
                exampleRequest = Unit,
                exampleResponse = GameStatus(
                    arrayOf(
                        arrayOf(Field.EMPTY, Field.EMPTY, Field.EMPTY),
                        arrayOf(Field.EMPTY, Field.EMPTY, Field.EMPTY),
                        arrayOf(Field.EMPTY, Field.EMPTY, Field.EMPTY)
                    ),
                    PlayerType.X,
                    true,
                    false,
                    false,
                    false,
                    null
                )
            ) { _, _ ->
                val session = pipeline.call.sessions.get<UserSession>() ?: throw InvalidSessionException()
                val user = Users.getUser(session)
                val room = user.room ?: throw NotInRoomException()

                val game = room.startGame(user)

                respond(game.getGameStatus(user))
            }

        route("status")
            .throws(notInRoomException, noGameStartedException)
            .get<SessionParameter, GameStatus>(
                example = GameStatus(
                    arrayOf(
                        arrayOf(Field.EMPTY, Field.EMPTY, Field.X),
                        arrayOf(Field.O, Field.X, Field.EMPTY),
                        arrayOf(Field.EMPTY, Field.EMPTY, Field.EMPTY)
                    ),
                    PlayerType.O,
                    true,
                    false,
                    false,
                    false,
                    null
                )
            ) {
                val session = pipeline.call.sessions.get<UserSession>() ?: throw InvalidSessionException()
                val user = Users.getUser(session)
                val room = user.room ?: throw NotInRoomException()
                val game = room.game ?: throw NoGameStartedException()

                respond(game.getGameStatus(user))
            }
    }
}

data class GamePlayRequest(@Clamp(0, 2) val x: Int, @Clamp(0, 2) val y: Int)

class NoGameStartedException : Exception()

object NoGameStartedError : Error("There is currently no active game")

val noGameStartedException = APIException.apiException<NoGameStartedException, Error>(
    HttpStatusCode.Forbidden,
    NoGameStartedError
) {
    NoGameStartedError
}

object NotYourTurnError : Error("Not your turn")

val notYourTurnException = APIException.apiException<NotYourTurnException, Error>(
    HttpStatusCode.Forbidden,
    NotYourTurnError
) {
    NotYourTurnError
}

object IllegalMoveError : Error("Illegal move!")

val illegalMoveException = APIException.apiException<IllegalMoveException, Error>(
    HttpStatusCode.Forbidden,
    IllegalMoveError
) {
    IllegalMoveError
}

object GameRunningError : Error("Game already running!")

val gameRunningException = APIException.apiException<GameRunningException, Error>(
    HttpStatusCode.Forbidden,
    GameRunningError
) {
    GameRunningError
}

object NotEnoughPlayersError : Error("Not enough players to start a game!")

val notEnoughPlayersException = APIException.apiException<NotEnoughPlayersException, Error>(
    HttpStatusCode.Forbidden,
    NotEnoughPlayersError
) {
    NotEnoughPlayersError
}

object NotRoomOwnerError : Error("You are not the room owner!")

val notRoomOwnerException = APIException.apiException<NotRoomOwnerException, Error>(
    HttpStatusCode.Forbidden,
    NotRoomOwnerError
) {
    NotRoomOwnerError
}
