package com.example.domain.auth.api

import com.smartfoxserver.bitswarm.sessions.ISession
import com.smartfoxserver.v2.api.ISFSApi

/**
 * Authentication Domain Service
 */
interface AuthService {
  /**
   * Lookup and authenticate the username based on the provided username and password.
   */
  fun authenticate(api: ISFSApi, session: ISession, username: String, password: String)
}
