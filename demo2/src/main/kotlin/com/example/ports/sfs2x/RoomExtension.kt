package com.example.ports.sfs2x

import akka.actor.ActorRef
import akka.pattern.Patterns
import com.example.domain.core.RoomActor
import com.example.domain.core.room.CreateRoom
import com.example.domain.core.room.RoomCreated
import org.pmw.tinylog.Logger
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import com.smartfoxserver.v2.annotations.Instantiation
import com.smartfoxserver.v2.annotations.Instantiation.InstantiationMode.SINGLE_INSTANCE
import com.smartfoxserver.v2.core.SFSEventType.SERVER_READY

@Instantiation(SINGLE_INSTANCE)
class RoomExtension : BaseExtension() {
  private lateinit var roomActor: ActorRef

  override fun init() {
    Logger.info("Creating room {}", parentRoom.name)
    Logger.debug("THREAD={}", Thread.currentThread().name)

    roomActor = actorSystem.actorOf(RoomActor.props(appConfig, this))

    registerRequestHandlers()
    registerEventListeners()

    Logger.debug("log_type=extension_loaded, id={}", this::class.java.simpleName)
  }

  /**
   * Register request handlers which deal with interactions with clients
   */
  private fun registerRequestHandlers() {
    addRequestHandler(appConfig.requests.player) { user, params ->
      val requestId = params.getUtfString(MULTIHANDLER_REQUEST_ID)

      when (requestId.trim().toLowerCase()) {
//        appConfig.player.attack -> commandBus.publish(MovePlayer(user.id, Vector2()))
//        appConfig.player.move -> commandBus.publish(MovePlayer(user.id, Vector2()))
//        "add" -> commandBus.publish(AddPlayer(user))
      }
    }

    addRequestHandler(appConfig.requests.item) { user, params ->
      val requestId = params.getUtfString(MULTIHANDLER_REQUEST_ID)

      when (requestId.trim().toLowerCase()) {
//        appConfig.player.attack -> commandBus.publish(MovePlayer(user.id, Vector2()))
//        appConfig.player.move -> commandBus.publish(MovePlayer(user.id, Vector2()))
      }
    }

    addRequestHandler(appConfig.requests.trade) { user, params ->
      val requestId = params.getUtfString(MULTIHANDLER_REQUEST_ID)

      when (requestId.trim().toLowerCase()) {
//        appConfig.player.attack -> commandBus.publish(TradeItem(user.id))
      }
    }
  }

  /**
   * Register event listeners
   */
  private fun registerEventListeners() {
    addEventListener(SERVER_READY) { Logger.info("log_type=server_ready") }
  }
}
