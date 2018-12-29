package com.example.ports.sfs2x

import com.smartfoxserver.v2.annotations.Instantiation
import com.smartfoxserver.v2.annotations.Instantiation.InstantiationMode.SINGLE_INSTANCE
import com.smartfoxserver.v2.core.SFSEventType.SERVER_READY
import com.smartfoxserver.v2.extensions.SFSExtension
import org.pmw.tinylog.Logger

/**
 * Represents a statically managed room, such as a named map
 */
@Instantiation(SINGLE_INSTANCE)
class StaticExtension : RoomExtension() {
  override fun init() {
    super.init()
    Logger.debug("roomId={}", roomId())

    registerRequestHandlers()
    registerEventListeners()

    Logger.debug("log_type=extension_loaded, name={}", this::class.java.simpleName)
  }

  /**
   * Register request handlers which deal with interactions with clients
   */
  private fun registerRequestHandlers() {
    addRequestHandler(appConfig.requests.player) { user, params ->
      val requestId = params.getUtfString(SFSExtension.MULTIHANDLER_REQUEST_ID)

      when (requestId.trim().toLowerCase()) {
//        appConfig.player.attack -> commandBus.publish(MovePlayer(user.id, Vector2()))
//        appConfig.player.move -> commandBus.publish(MovePlayer(user.id, Vector2()))
//        "add" -> commandBus.publish(AddPlayer(user))
      }
    }

    addRequestHandler(appConfig.requests.item) { user, params ->
      val requestId = params.getUtfString(SFSExtension.MULTIHANDLER_REQUEST_ID)

      when (requestId.trim().toLowerCase()) {
//        appConfig.player.attack -> commandBus.publish(MovePlayer(user.id, Vector2()))
//        appConfig.player.move -> commandBus.publish(MovePlayer(user.id, Vector2()))
      }
    }

    addRequestHandler(appConfig.requests.trade) { user, params ->
      val requestId = params.getUtfString(SFSExtension.MULTIHANDLER_REQUEST_ID)

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
