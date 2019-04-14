package com.example.domain.core

import akka.actor.ActorRef
import com.example.ports.sfs2x.RoomExtension
import com.example.ports.sfs2x.ZoneExtension
import com.smartfoxserver.v2.core.ISFSEvent
import com.smartfoxserver.v2.entities.Room
import com.smartfoxserver.v2.entities.User
import org.dyn4j.geometry.Vector2
import org.pmw.tinylog.Logger

// Commands
sealed class Command

data class SayHello(val message: String) : Command()
data class RegisterRoomExtension(val extension: RoomExtension) : Command()
data class CreateNpc(val name: String) : Command()

// Player Commands
data class MovePlayer(val id: Int, val position: Vector2) : Command()

data class AddPlayer(val user: User) : Command()
data class RemovePlayer(val user: User) : Command()
object CheckPlayerConnected : Command()

// Room Commands
data class JoinRoom(val user: User) : Command()

data class CreateRoom(val room: RoomExtension) : Command()
data class CreateRooms(val zone: ZoneExtension) : Command()

// Trade Commands
data class TradeItem(val id: Int) : Command()

// Events
sealed class Event

data class StateChanged(val id: Int) : Event()

// Player Events
data class PlayerMoved(val id: Int, val position: Vector2) : Event()

// Room Events
data class RoomJoined(val user: User) : Event()

data class RoomCreated(val actor: ActorRef) : Event()
data class RoomsCreated(val actorRef: ActorRef) : Event()

// Trade Events
data class ItemTraded(val id: Int) : Event()

// Zone Events
data class UserJoinedZone(val user: User) : Event()

data class UserJoinedRoom(val user: User, val room: Room) : Event()
data class UserLeftRoom(val user: User, val room: Room) : Event()
data class UserLoggedOut(val user: User) : Event()
data class UserDisconnected(val user: User) : Event()
data class RoomAdded(val room: Room)
data class RoomRemoved(val room: Room)

// Extension Functions
fun User.disconnect(event: ISFSEvent?) {
  Logger.info("disconnect user={}", this.name)

  joinedRooms.forEach { room ->
    Logger.info("leave room={}", room.name)

//    (room.extension as RoomExtension).roomActor().tell(RemovePlayer(this), ActorRef.noSender())
  }
}
