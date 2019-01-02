package com.example.application

import akka.actor.ActorRef
import akka.actor.ActorRef.noSender
import com.example.domain.core.player.AddPlayer
import net.engio.mbassy.listener.Handler
import net.engio.mbassy.listener.Listener
import org.pmw.tinylog.Logger

@Listener
class RoomHandler(private val room: ActorRef) {
  @Handler
  fun handle(cmd: AddPlayer) {
    Logger.info("Handling command")
    room.tell(cmd, noSender())
  }
}
