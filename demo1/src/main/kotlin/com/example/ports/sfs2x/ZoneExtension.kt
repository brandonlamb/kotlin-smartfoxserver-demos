package com.example.ports.sfs2x

import akka.actor.ActorRef
import akka.actor.ActorRef.noSender
import akka.pattern.Patterns
import com.example.domain.auth.api.AuthService
import com.example.domain.core.ZoneActor
import com.example.domain.core.room.CreateRooms
import com.example.domain.core.room.RoomsCreated
import com.example.domain.core.zone.RoomAdded
import com.example.domain.core.zone.RoomRemoved
import com.example.domain.core.zone.UserDisconnected
import com.example.domain.core.zone.UserJoinedRoom
import com.example.domain.core.zone.UserJoinedZone
import com.example.domain.core.zone.UserLeftRoom
import com.example.domain.core.zone.UserLoggedOut
import com.smartfoxserver.bitswarm.sessions.ISession
import com.smartfoxserver.v2.api.CreateRoomSettings
import com.smartfoxserver.v2.core.SFSEventParam.LOGIN_NAME
import com.smartfoxserver.v2.core.SFSEventParam.LOGIN_PASSWORD
import com.smartfoxserver.v2.core.SFSEventParam.ROOM
import com.smartfoxserver.v2.core.SFSEventParam.SESSION
import com.smartfoxserver.v2.core.SFSEventParam.USER
import com.smartfoxserver.v2.core.SFSEventType.ROOM_ADDED
import com.smartfoxserver.v2.core.SFSEventType.ROOM_REMOVED
import com.smartfoxserver.v2.core.SFSEventType.SERVER_READY
import com.smartfoxserver.v2.core.SFSEventType.USER_DISCONNECT
import com.smartfoxserver.v2.core.SFSEventType.USER_JOIN_ROOM
import com.smartfoxserver.v2.core.SFSEventType.USER_JOIN_ZONE
import com.smartfoxserver.v2.core.SFSEventType.USER_LEAVE_ROOM
import com.smartfoxserver.v2.core.SFSEventType.USER_LOGIN
import com.smartfoxserver.v2.core.SFSEventType.USER_LOGOUT
import com.smartfoxserver.v2.entities.Room
import com.smartfoxserver.v2.entities.SFSRoomRemoveMode
import com.smartfoxserver.v2.entities.User
import com.smartfoxserver.v2.entities.variables.SFSRoomVariable
import com.smartfoxserver.v2.mmo.CreateMMORoomSettings
import com.smartfoxserver.v2.mmo.Vec3D
import org.pmw.tinylog.Logger
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit

//@Instantiation(SINGLE_INSTANCE)
class ZoneExtension : BaseExtension() {
  private val auth: AuthService = ctx.getBean(AuthService::class.java)
  private lateinit var zoneActor: ActorRef
//  private val config: ISFSObject = SFSObject()

  override fun init() {
    Logger.info("Creating zone {}", parentZone.name)

    registerEventListeners()

    zoneActor = actorSystem.actorOf(
      ZoneActor.props(appConfig, this).withDispatcher(appConfig.dispatchers.affinity),
      "world"
    )
    (Await.result(Patterns.ask(zoneActor, CreateRooms(this), 5000), Duration.create(5, TimeUnit.SECONDS)) as RoomsCreated)

//    SmartFoxServer.getInstance().taskScheduler.scheduleAtFixedRate({
//      Logger.info("Connected user sweep")
//      parentZone.userList.forEach { user ->
//        if (!user.isConnected) {
//          user.disconnect(null)
//        }
//      }
//    }, 10, 20, SECONDS)

    Logger.info("log_type=extension_loaded, name={}", this::class.java.simpleName)
  }

  fun zone() = zoneActor

  private fun registerEventListeners() {
    addEventListener(USER_JOIN_ZONE) {
      Logger.info("event=USER_JOIN_ZONE, username={}", (it.getParameter(USER) as User).name)
      zoneActor.tell(UserJoinedZone(it.getParameter(USER) as User), noSender())
    }

    addEventListener(USER_JOIN_ROOM) {
      Logger.info("event=USER_JOIN_ROOM, username={}", (it.getParameter(USER) as User).name)
      zoneActor.tell(UserJoinedRoom(it.getParameter(USER) as User, it.getParameter(ROOM) as Room), noSender())
    }

    addEventListener(USER_LEAVE_ROOM) {
      Logger.info("event=USER_LEAVE_ROOM, username={}", (it.getParameter(USER) as User).name)
      zoneActor.tell(UserLeftRoom(it.getParameter(USER) as User, it.getParameter(ROOM) as Room), noSender())
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

    addEventListener(ROOM_ADDED) {
      Logger.info("event=ROOM_ADDED, room={}", (it.getParameter(ROOM) as Room).name)
      zoneActor.tell(RoomAdded(it.getParameter(ROOM) as Room), noSender())
    }

    addEventListener(ROOM_REMOVED) {
      Logger.info("event=ROOM_REMOVED, room={}", (it.getParameter(ROOM) as Room).name)
      zoneActor.tell(RoomRemoved(it.getParameter(ROOM) as Room), noSender())
    }

    addEventListener(SERVER_READY) {
      Logger.info("event=SERVER_READY")
      createRooms()
    }
  }

  private fun createRooms() {
    Logger.debug("THREAD={}", Thread.currentThread().name)

//    zoneActor = actorSystem.actorOf(
//      ZoneActor.props(appConfig, this).withDispatcher(appConfig.dispatchers.affinity),
//      "world"
//    )
//    (Await.result(ask(zoneActor, CreateRooms(this), 5000), Duration.create(5, TimeUnit.SECONDS)) as RoomsCreated)

    parentZone.createRoom(CreateRoomSettings().apply {
      name = "lobby"
      isGame = false
      isDynamic = true
      autoRemoveMode = SFSRoomRemoveMode.NEVER_REMOVE
      setAllowOwnerOnlyInvitation(true)
      extension = CreateRoomSettings.RoomExtensionSettings("__lib__", "com.example.ports.sfs2x.StaticExtension")
      roomVariables = listOf(SFSRoomVariable("roomId", "lobby", true, false, false))
    })

    for (i in 1..2) {
      parentZone.createRoom(CreateMMORoomSettings().apply {
        name = "room-$i"
        defaultAOI = Vec3D(200, 200, 0)
        isGame = true
        isDynamic = true
        autoRemoveMode = SFSRoomRemoveMode.NEVER_REMOVE
        isSendAOIEntryPoint = true
        setAllowOwnerOnlyInvitation(true)
        extension = CreateRoomSettings.RoomExtensionSettings("__lib__", "com.example.ports.sfs2x.StaticExtension")
        roomVariables = listOf(SFSRoomVariable("roomId", "room-$i", true, false, false))
      })
    }
  }

//  override fun handleInternalMessage(cmdName: String, params: Any): Any = when (cmdName.trim()) {
//    "ctx" -> "ctx"
//    else -> ""
//  }
}
