package com.example.domain.core.player.api

import com.example.domain.core.player.Player

/**
 * Player Read Infrastructure Service
 */
interface PlayerReadRepository {
  /**
   * Find a user by username
   */
  fun findByUsername(username: String): Player?
}
