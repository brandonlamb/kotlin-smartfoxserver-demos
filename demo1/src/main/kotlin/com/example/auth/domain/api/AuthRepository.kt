package com.example.auth.domain.api

/**
 * Authentication Infrastructure Service
 */
interface AuthRepository {
  /**
   * Lookup and authenticate the username based on the provided username and password.
   */
  fun authenticate(username: String, password: String): Boolean
}
