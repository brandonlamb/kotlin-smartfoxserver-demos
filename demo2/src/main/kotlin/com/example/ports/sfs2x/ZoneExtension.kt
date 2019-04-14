package com.example.ports.sfs2x

import com.example.domain.auth.api.AuthService
import com.smartfoxserver.bitswarm.sessions.ISession
import com.smartfoxserver.v2.annotations.Instantiation
import com.smartfoxserver.v2.annotations.Instantiation.InstantiationMode.SINGLE_INSTANCE
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

@Instantiation(SINGLE_INSTANCE)
class ZoneExtension : BaseExtension() {
  private val auth: AuthService = ctx.getBean(AuthService::class.java)

  override fun init() {
    Logger.info("Creating zone {}", parentZone.name)

    registerEventListeners()

    Logger.info("log_type=extension_loaded, id={}", this::class.java.simpleName)
  }

  private fun registerEventListeners() {
    addEventListener(USER_JOIN_ZONE) {
      Logger.info("event=USER_JOIN_ZONE, username={}", (it.getParameter(USER) as User).name)
    }

    addEventListener(USER_JOIN_ROOM) {
      Logger.info("event=USER_JOIN_ROOM, username={}", (it.getParameter(USER) as User).name)
    }

    addEventListener(USER_LEAVE_ROOM) {
      Logger.info("event=USER_LEAVE_ROOM, username={}", (it.getParameter(USER) as User).name)
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
    }

    // A client simply disconnecting is pretty brute-force and doesnt trigger events like USER_LEAVE_ROOM
    addEventListener(USER_DISCONNECT) {
      Logger.info("event=USER_DISCONNECT, username={}", (it.getParameter(USER) as User).name)
    }

    addEventListener(ROOM_ADDED) {
      Logger.info("event=ROOM_ADDED, room={}", (it.getParameter(ROOM) as Room).name)
    }

    addEventListener(ROOM_REMOVED) {
      Logger.info("event=ROOM_REMOVED, room={}", (it.getParameter(ROOM) as Room).name)
    }

    addEventListener(SERVER_READY) {
      Logger.info("event=SERVER_READY")
      createRooms()
    }
  }

  private fun createRooms() {
    Logger.debug("THREAD={}", Thread.currentThread().name)

    parentZone.createRoom(CreateRoomSettings().apply {
      name = "lobby"
      isGame = false
      isDynamic = true
      autoRemoveMode = SFSRoomRemoveMode.NEVER_REMOVE
      setAllowOwnerOnlyInvitation(true)
      extension = CreateRoomSettings.RoomExtensionSettings("__lib__", "com.example.ports.sfs2x.StaticExtension")
      roomVariables = listOf(SFSRoomVariable("roomId", "lobby", true, false, false))
    })

    val i = 1
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

//  override fun handleInternalMessage(cmdName: String, params: Any): Any = when (cmdName.trim()) {
//    "ctx" -> "ctx"
//    else -> ""
//  }
}
