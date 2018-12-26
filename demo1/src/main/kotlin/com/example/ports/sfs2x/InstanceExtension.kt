package com.example.ports.sfs2x

import com.smartfoxserver.v2.annotations.Instantiation
import com.smartfoxserver.v2.annotations.Instantiation.InstantiationMode.SINGLE_INSTANCE
import com.smartfoxserver.v2.core.SFSEventType.SERVER_READY
import org.pmw.tinylog.Logger

/**
 * Represents a dynamically generated room, such as a dungeon or player housing instance
 */
@Instantiation(SINGLE_INSTANCE)
class InstanceExtension : RoomExtension() {
  override fun init() {
    super.init()
    Logger.info("roomId={}", roomId())

    addEventListener(SERVER_READY) { Logger.info("log_type=server_ready") }

    Logger.info("log_type=extension_loaded, name={}", this::class.java.simpleName)
  }
}
