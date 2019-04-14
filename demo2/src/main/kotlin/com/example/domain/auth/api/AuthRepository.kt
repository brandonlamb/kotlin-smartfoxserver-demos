package com.example.domain.auth.api

import com.example.domain.auth.User

/**
 * Authentication Infrastructure Service
 */
interface AuthRepository {
  /**
   * Find user by username
   */
  fun findUser(username: String): User?
}
