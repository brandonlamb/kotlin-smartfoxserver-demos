package com.example.domain.core.zone

import com.smartfoxserver.v2.entities.Room
import com.smartfoxserver.v2.entities.User

// Events
sealed class Event

data class UserJoinedZone(val user: User) : Event()
data class UserJoinedRoom(val user: User, val room: Room) : Event()
data class UserLeftRoom(val user: User, val room: Room) : Event()
data class UserLoggedOut(val user: User) : Event()
data class UserDisconnected(val user: User) : Event()
data class RoomAdded(val room: Room)
data class RoomRemoved(val room: Room)
