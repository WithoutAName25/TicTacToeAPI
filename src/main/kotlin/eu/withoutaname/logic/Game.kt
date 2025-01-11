package eu.withoutaname.logic

import eu.withoutaname.routes.Response

class Game(val playerX: User, val playerO: User) {
    private val board = Array(3) { Array(3) { Field.EMPTY } }
    private var currentPlayer: User? = playerX

    private val winner: User?
        get() {
            for (i in 0..2) {
                getWinnerIn(i, 0, 0, 1)?.let { return it }
                getWinnerIn(0, i, 1, 0)?.let { return it }
            }
            getWinnerIn(0, 0, 1, 1)?.let { return it }
            getWinnerIn(0, 2, 1, -1)?.let { return it }
            return null
        }

    private val boardFull: Boolean
        get() = board.all { row -> row.all { it != Field.EMPTY } }

    private val draw: Boolean
        get() = winner == null && boardFull

    val finished: Boolean
        get() = draw || winner != null

    private fun getWinnerIn(x: Int, y: Int, dx: Int, dy: Int): User? {
        val maybeWinner = board[y][x]
        return if (maybeWinner == Field.EMPTY || maybeWinner != board[y + dy][x + dx] || maybeWinner != board[y + 2 * dy][x + 2 * dx]) {
            null
        } else if (maybeWinner == Field.X) {
            playerX
        } else {
            playerO
        }
    }

    fun play(user: User, x: Int, y: Int) {
        synchronized(this) {
            if (user != currentPlayer) throw NotYourTurnException()
            if (x !in 0..2 || y !in 0..2 || board[y][x] != Field.EMPTY) throw IllegalMoveException()

            board[y][x] = if (user == playerX) Field.X else Field.O

            currentPlayer = when {
                finished -> null
                currentPlayer == playerX -> playerO
                else -> playerX
            }
        }
    }

    fun getGameStatus(user: User): GameStatus {
        return GameStatus(
            board,
            when (user) {
                playerX -> PlayerType.X
                playerO -> PlayerType.O
                else -> PlayerType.SPECTATOR
            },
            PlayerInfo.from(currentPlayer, user, playerX, playerO),
            finished,
            draw,
            PlayerInfo.from(winner, user, playerX, playerO)
        )
    }
}

enum class Field {
    EMPTY, X, O
}

data class GameStatus(
    val board: Array<Array<Field>>,
    val playerType: PlayerType,
    val currentPlayer: PlayerInfo?,
    val isFinished: Boolean,
    val isDraw: Boolean,
    val winner: PlayerInfo?,
) : Response(true)

enum class PlayerType {
    SPECTATOR, X, O
}

data class PlayerInfo(val type: PlayerType, val username: String, val isMe: Boolean) {
    companion object {
        fun from(user: User?, me: User, playerX: User, playerO: User): PlayerInfo? {
            if (user == null) return null
            return PlayerInfo(
                when (user) {
                    playerX -> PlayerType.X
                    playerO -> PlayerType.O
                    else -> PlayerType.SPECTATOR
                },
                user.username,
                user == me
            )
        }
    }
}

class IllegalMoveException : Exception()

class NotYourTurnException : Exception()
