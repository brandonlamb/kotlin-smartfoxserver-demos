package com.example.domain.core.player

import com.smartfoxserver.v2.entities.User
import org.dyn4j.geometry.Vector2

typealias PlayerId = Long

// Domain Models
data class Player(val id: PlayerId, val username: String, val position: Vector2)

// Commands
sealed class Command

data class MovePlayer(val id: Int, val position: Vector2) : Command()
data class AddPlayer(val user: User) : Command()
data class RemovePlayer(val user: User) : Command()
object CheckPlayerConnected : Command()

// Events
sealed class Event

data class PlayerMoved(val id: Int, val position: Vector2) : Event()
