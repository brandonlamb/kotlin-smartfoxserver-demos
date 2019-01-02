package com.example.ports.sfs2x

import akka.actor.ActorRef
import akka.actor.ActorRef.noSender
import akka.pattern.Patterns
import com.example.domain.auth.api.AuthService
import com.example.domain.core.ZoneActor
import com.example.domain.core.room.CreateRooms
import com.example.domain.core.room.RoomsCreated
import com.example.domain.core.zone.UserDisconnected
import com.example.domain.core.zone.UserJoinedZone
import com.example.domain.core.zone.UserLoggedOut
import com.smartfoxserver.bitswarm.sessions.ISession
import com.smartfoxserver.v2.annotations.Instantiation
import com.smartfoxserver.v2.annotations.Instantiation.InstantiationMode.SINGLE_INSTANCE
import com.smartfoxserver.v2.core.SFSEventParam.LOGIN_NAME
import com.smartfoxserver.v2.core.SFSEventParam.LOGIN_PASSWORD
import com.smartfoxserver.v2.core.SFSEventParam.SESSION
import com.smartfoxserver.v2.core.SFSEventParam.USER
import com.smartfoxserver.v2.core.SFSEventType.SERVER_READY
import com.smartfoxserver.v2.core.SFSEventType.USER_DISCONNECT
import com.smartfoxserver.v2.core.SFSEventType.USER_JOIN_ZONE
import com.smartfoxserver.v2.core.SFSEventType.USER_LOGIN
import com.smartfoxserver.v2.core.SFSEventType.USER_LOGOUT
import com.smartfoxserver.v2.entities.User
import com.smartfoxserver.v2.extensions.SFSExtension
import org.pmw.tinylog.Logger
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit

@Instantiation(SINGLE_INSTANCE)
class ZoneExtension : BaseExtension() {
  private val auth: AuthService = ctx.getBean(AuthService::class.java)
  private lateinit var zoneActor: ActorRef

  override fun init() {
    Logger.info("Creating zone {}", parentZone.name)

    registerEventListeners()

    zoneActor = actorSystem.actorOf(
      ZoneActor.props(appConfig, this).withDispatcher(appConfig.dispatchers.affinity),
      "world"
    )
    (Await.result(Patterns.ask(zoneActor, CreateRooms(this), 5000), Duration.create(5, TimeUnit.SECONDS)) as RoomsCreated)

    Logger.info("log_type=extension_loaded, name={}", this::class.java.simpleName)
  }

  private fun registerEventListeners() {
    addEventListener(USER_JOIN_ZONE) {
      Logger.info("event=USER_JOIN_ZONE, username={}", (it.getParameter(USER) as User).name)
      zoneActor.tell(UserJoinedZone(it.getParameter(USER) as User), noSender())
    }

    addEventListener(USER_LOGIN) {
      Logger.info("event=USER_LOGIN, username={}", it.getParameter(LOGIN_NAME) as String)

      val username = it.getParameter(LOGIN_NAME) as String
      val password = it.getParameter(LOGIN_PASSWORD) as String
      val session = it.getParameter(SESSION) as ISession
//      val data = it.getParameter(LOGIN_IN_DATA) as ISFSObject
//      val pw = data.getUtfString("password")
      auth.authenticate(api, session, username, password)
    }

    addEventListener(USER_LOGOUT) {
      Logger.info("event=USER_LOGOUT, username={}", (it.getParameter(USER) as User).name)
      zoneActor.tell(UserLoggedOut(it.getParameter(USER) as User), noSender())
    }

    // A client simply disconnecting is pretty brute-force and doesnt trigger events like USER_LEAVE_ROOM
    addEventListener(USER_DISCONNECT) {
      Logger.info("event=USER_DISCONNECT, username={}", (it.getParameter(USER) as User).name)
      zoneActor.tell(UserDisconnected(it.getParameter(USER) as User), noSender())
    }

    addEventListener(SERVER_READY) {
      Logger.info("event=SERVER_READY")
    }

    addRequestHandler("realms") { user, params ->
      val requestId = params.getUtfString(SFSExtension.MULTIHANDLER_REQUEST_ID)

      when (requestId.trim().toLowerCase()) {
//        appConfig.player.attack -> commandBus.publish(MovePlayer(user.id, Vector2()))
//        appConfig.player.move -> commandBus.publish(MovePlayer(user.id, Vector2()))
      }
    }
  }
}
