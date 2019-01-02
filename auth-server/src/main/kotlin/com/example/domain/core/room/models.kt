package com.example.domain.core.room

import akka.actor.ActorRef
import com.example.ports.sfs2x.ZoneExtension
import com.smartfoxserver.v2.entities.User

// Commands
sealed class Command

data class JoinRoom(val user: User) : Command()
data class CreateRoom(val room: RoomExtension) : Command()
data class CreateRooms(val zone: ZoneExtension) : Command()

// Events
sealed class Event

data class RoomJoined(val user: User) : Event()
data class RoomCreated(val actor: ActorRef) : Event()
data class RoomsCreated(val actorRef: ActorRef) : Event()
