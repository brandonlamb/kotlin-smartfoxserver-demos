package com.example.auth.domain.api

interface AuthRepository {
  fun authenticate(username: String, password: String): Boolean
}
