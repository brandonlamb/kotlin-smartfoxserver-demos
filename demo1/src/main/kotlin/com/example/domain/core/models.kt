package com.example.domain.core

import akka.actor.ActorRef
import com.example.domain.core.player.RemovePlayer
import com.example.ports.sfs2x.RoomExtension
import com.smartfoxserver.v2.core.ISFSEvent
import com.smartfoxserver.v2.entities.User
import org.pmw.tinylog.Logger

// Data Models
data class Game(val name: String)

// Commands
sealed class Command

class SayHello(val message: String) : Command()
class RegisterRoomExtension(val extension: RoomExtension) : Command()

object Tick : Command()

// Events
sealed class Event

data class StateChanged(val id: Int) : Event()

// Extension Functions
fun User.disconnect(event: ISFSEvent?) {
  Logger.info("disconnect user={}", this.name)

  joinedRooms.forEach { room ->
    Logger.info("leave room={}", room.name)

    (room.extension as RoomExtension).roomActor().tell(RemovePlayer(this), ActorRef.noSender())
  }
}
