package com.example.ports.sfs2x

import akka.actor.ActorRef
import akka.actor.ActorRef.noSender
import com.example.domain.core.MovePlayer
import com.example.domain.core.RoomActor
import com.smartfoxserver.v2.annotations.Instantiation
import com.smartfoxserver.v2.annotations.Instantiation.InstantiationMode.SINGLE_INSTANCE
import com.smartfoxserver.v2.core.SFSEventType.SERVER_READY
import org.dyn4j.geometry.Vector2
import org.pmw.tinylog.Logger

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
    addRequestHandler(appConfig.requests.player, PlayerRequestHandler(roomActor))

    addRequestHandler(appConfig.requests.item) { user, params ->
      when (params.getInt(MULTIHANDLER_REQUEST_ID)) {
        ItemRequestMappings.TRADE -> roomActor.tell(MovePlayer(user.id, Vector2()), noSender())
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
