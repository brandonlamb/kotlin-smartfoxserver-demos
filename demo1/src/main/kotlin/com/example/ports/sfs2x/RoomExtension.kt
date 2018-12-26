package com.example.ports.sfs2x

import akka.actor.ActorRef
import akka.pattern.Patterns
import com.example.domain.core.room.CreateRoom
import com.example.domain.core.room.RoomCreated
import org.pmw.tinylog.Logger
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

abstract class RoomExtension : BaseExtension() {
  private lateinit var roomActor: ActorRef
  private lateinit var roomTask: ScheduledFuture<*>
  private lateinit var roomId: String

  override fun init() {
    Logger.info("Creating room {}", parentRoom.name)

    roomId = if (parentRoom.containsVariable(ROOM_ID_VAR_NAME)) {
      parentRoom.getVariable(ROOM_ID_VAR_NAME).stringValue
    } else {
      this::class.java.simpleName
    }

    Logger.debug("THREAD={}", Thread.currentThread().name)

    val d = Duration.create(5, TimeUnit.SECONDS)
    val roomsActor = Await.result(actorSystem.actorSelection("/user/world/rooms").resolveOne(d), d)
    roomActor = (Await.result(Patterns.ask(roomsActor, CreateRoom(this), 5000), d) as RoomCreated).actor
  }

  fun roomActor() = roomActor
  fun roomId() = roomId

  override fun destroy() {
    super.destroy()
    roomTask.cancel(true)
  }

  private companion object {
    private const val ROOM_ID_VAR_NAME = "roomId"
  }
}
