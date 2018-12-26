package com.example.ports.sfs2x

import akka.actor.ActorSystem
import com.example.Main
import com.example.infrastructure.config.AppConfig
import com.smartfoxserver.v2.extensions.SFSExtension
import io.micronaut.context.ApplicationContext

abstract class BaseExtension : SFSExtension() {
  protected val ctx: ApplicationContext = Main.ctx
  protected val appConfig: AppConfig = ctx.getBean(AppConfig::class.java)
  protected val actorSystem: ActorSystem = ctx.getBean(ActorSystem::class.java)
}
