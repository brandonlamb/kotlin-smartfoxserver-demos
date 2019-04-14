package com.example.application

import com.example.domain.core.AddPlayer
import com.example.ports.sfs2x.RoomExtension
import com.smartfoxserver.v2.entities.User
import com.smartfoxserver.v2.mmo.IMMOItemVariable
import com.smartfoxserver.v2.mmo.MMOItem
import com.smartfoxserver.v2.mmo.MMOItemVariable
import com.smartfoxserver.v2.mmo.MMORoom
import com.smartfoxserver.v2.mmo.Vec3D
import net.engio.mbassy.listener.Handler
import net.engio.mbassy.listener.Listener
import org.pmw.tinylog.Logger

@Listener
class RoomHandler(private val room: RoomExtension) {
  @Handler
  fun handle(cmd: AddPlayer) {
    Logger.info("Handling command")

    val npc = room.api.createNPC("npc-1", room.parentZone, false)
    val npcs = mutableMapOf<Int, User>()

    // Create NPCs
    when (room.parentRoom) {
      is MMORoom -> {
        for (i in 1..100) {
          val npc = room.api.createNPC("npc-$i", room.parentZone, false)
          npcs[i] = npc
        }
      }
    }

    // Create items
    when (room.parentRoom) {
      is MMORoom -> {
        for (i in 1..100) {
          val vars = mutableListOf<IMMOItemVariable>()
          vars.add(MMOItemVariable("id", "item-$i"))
          vars.add(MMOItemVariable("n", "Item Description $i"))
          room.mmoApi.setMMOItemPosition(MMOItem(vars), Vec3D(i, i, 0), room.parentRoom as MMORoom)
        }
      }
    }

    when (room.parentRoom) {
      is MMORoom -> {
        val allItems = (room.parentRoom as MMORoom).allMMOItems
//        allItems.forEach { Logger.info("item={}/{}", room.roomId(), it.variables.first { it.id == "id" }.value) }

//        if (allItems.size < 20) {
//          val vars = mutableListOf<IMMOItemVariable>()
//          vars.add(MMOItemVariable("id", "item-$tick"))
//          room.mmoApi.setMMOItemPosition(MMOItem(vars), Vec3D(1, 1, 0), room.parentRoom as MMORoom)
//        }
      }
    }
  }
}
