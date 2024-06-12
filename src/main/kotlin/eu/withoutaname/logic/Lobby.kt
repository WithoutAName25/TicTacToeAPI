package eu.withoutaname.logic

import eu.withoutaname.routes.LobbyStatus
import eu.withoutaname.routes.RoomNameUnavailableException

object Lobby {
    private val rooms = mutableMapOf<String, Room>()
    val chat = Chat()

    val status: LobbyStatus.InLobby
        get() = LobbyStatus.InLobby(rooms.values.map { it.data })

    fun createRoom(roomName: String): Room {
        if (rooms.containsKey(roomName)) throw RoomNameUnavailableException()

        val room = Room(roomName)
        rooms[roomName] = room
        return room
    }

    fun getRoom(roomName: String) = rooms[roomName]

    fun removeRoom(roomName: String) {
        rooms.remove(roomName)?.clear()
    }
}

