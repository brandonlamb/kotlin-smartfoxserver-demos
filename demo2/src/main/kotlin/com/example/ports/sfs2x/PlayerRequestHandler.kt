package com.example.ports.sfs2x

import akka.actor.ActorRef
import akka.actor.ActorRef.noSender
import com.example.domain.core.MovePlayer
import com.smartfoxserver.v2.entities.User
import com.smartfoxserver.v2.entities.data.ISFSObject
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler
import com.smartfoxserver.v2.extensions.SFSExtension
import org.dyn4j.geometry.Vector2

class PlayerRequestHandler(private val roomActor: ActorRef) : BaseClientRequestHandler() {
  override fun handleClientRequest(user: User, params: ISFSObject) {
    when (params.getInt(SFSExtension.MULTIHANDLER_REQUEST_ID)) {
      PlayerRequestMappings.ATTACK -> roomActor.tell(MovePlayer(user.id, Vector2()), noSender())
      PlayerRequestMappings.MOVE -> roomActor.tell(MovePlayer(user.id, Vector2()), noSender())
    }
  }
}
