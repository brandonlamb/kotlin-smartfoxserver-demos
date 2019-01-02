package com.example.ports.sfs2x

import akka.actor.ActorRef
import akka.actor.ActorRef.noSender
import com.example.infrastructure.config.AppConfig
import com.smartfoxserver.v2.entities.User
import com.smartfoxserver.v2.entities.data.ISFSObject
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler
import com.smartfoxserver.v2.extensions.SFSExtension

class PlayerRequestHandler(
  private val appConfig: AppConfig,
  private val room: ActorRef
) : BaseClientRequestHandler() {
  override fun handleClientRequest(user: User, params: ISFSObject) {
    val requestId = params.getUtfString(SFSExtension.MULTIHANDLER_REQUEST_ID)

    when (requestId.trim().toLowerCase()) {
      appConfig.player.attack -> room.tell("Attack", noSender())
      appConfig.player.move -> room.tell("MovePlayer", noSender())
    }
  }
}
