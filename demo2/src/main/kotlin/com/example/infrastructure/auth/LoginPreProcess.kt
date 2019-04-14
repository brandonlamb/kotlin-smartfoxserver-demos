package com.example.infrastructure.auth

import com.smartfoxserver.v2.components.login.ILoginAssistantPlugin
import com.smartfoxserver.v2.components.login.LoginData
import com.smartfoxserver.v2.components.login.PasswordCheckException
import org.pmw.tinylog.Logger

class LoginPreProcess : ILoginAssistantPlugin {
  override fun execute(ld: LoginData) {
    Logger.info("checking password")

    val password = ld.clientIncomingData.getUtfString("password")

    if (ld.password != password) {
      throw PasswordCheckException()
    }
  }
}
