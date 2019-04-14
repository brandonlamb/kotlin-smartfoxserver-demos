package com.example.domain.core

import com.smartfoxserver.v2.core.ISFSEvent
import com.smartfoxserver.v2.entities.User
import org.pmw.tinylog.Logger

// Extension Functions
fun User.disconnect(event: ISFSEvent?) {
  Logger.info("disconnect user={}", this.name)

  joinedRooms.forEach { room ->
    Logger.info("leave room={}", room.name)

//    (room.extension as RoomExtension).roomActor().tell(RemovePlayer(this), ActorRef.noSender())
  }
}
