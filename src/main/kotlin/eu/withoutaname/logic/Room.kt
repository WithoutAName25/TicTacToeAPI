package eu.withoutaname.logic

import eu.withoutaname.routes.LobbyStatus
import eu.withoutaname.routes.RoomData

class Room(val name: String) {
    private val users = mutableListOf<User>()
    val chat = Chat()
    var game: Game? = null
        private set
    var deleted = false

    val data: RoomData
        get() = RoomData(name, users.map { it.username }, game != null)

    val status: LobbyStatus.InRoom
        get() = LobbyStatus.InRoom(data)

    fun join(user: User) {
        synchronized(users) {
            if (deleted) return
            synchronized(user) {
                if (user.room != null) return
                user.room = this
            }
            users.add(user)
        }
    }

    fun leave(user: User) {
        synchronized(users) {
            if (deleted) return
            if (user.room != this) return

            user.room = null
            users.remove(user)

            if (users.isEmpty()) {
                Lobby.removeRoom(name)
            }
        }
    }

    fun clear() {
        synchronized(users) {
            for (user in users) {
                user.room = null
            }
            deleted = true
        }
    }

    fun startGame(user: User): Game {
        if (game?.finished == false) {
            throw GameRunningException()
        }
        if (users.size < 2) {
            throw NotEnoughPlayersException()
        }
        if (user != users[0] && user != users[1]) {
            throw OnlySpectatorException()
        }
        val game = Game(users[0], users[1])
        this.game = game
        return game
    }
}

class GameRunningException : Exception()

class NotEnoughPlayersException : Exception()

class OnlySpectatorException : Exception()
