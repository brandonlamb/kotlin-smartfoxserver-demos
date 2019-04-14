package com.example.domain.core

import akka.actor.AbstractActorWithTimers
import akka.actor.OneForOneStrategy
import akka.actor.Props
import akka.actor.SupervisorStrategy
import akka.actor.SupervisorStrategy.restart
import akka.actor.SupervisorStrategy.stop
import akka.japi.pf.DeciderBuilder
import com.example.infrastructure.config.AppConfig
import com.example.ports.sfs2x.RoomExtension
import org.dyn4j.dynamics.Body
import org.dyn4j.dynamics.BodyFixture
import org.dyn4j.dynamics.World
import org.dyn4j.geometry.MassType
import org.dyn4j.geometry.Rectangle
import org.dyn4j.geometry.Vector2
import org.pmw.tinylog.Logger
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit.MINUTES

class PlayersActor(private val appConfig: AppConfig, private val room: RoomExtension) : AbstractActorWithTimers() {
  private object TickKey

  private val world = World()
  private var last = 0L

  override fun supervisorStrategy(): SupervisorStrategy = OneForOneStrategy(
    10,
    Duration.create(1, MINUTES),
    DeciderBuilder
      .match(TimeoutException::class.java) { restart() }
      .match(UnstableClientException::class.java) { stop() }
      .build()
  )

  init {
    Logger.info("Creating ${context.parent.path()}/${self.path().name()}")

    // Init world
    world.gravity = Vector2(0.0, 0.0)

    for (i in 1..5) {
      Body().apply {
        addFixture(BodyFixture(Rectangle(10.0, 5.0)))
        translate(10.0 * i, 10.0 * i)
        setMassType(MassType.INFINITE)
        world.addBody(this)
      }

      for (j in 1..5000) {
        val time = System.nanoTime()
        val delta = time - last
        last = time

        val elapsedTime = delta / NANO_TO_BASE

        world.bodyIterator.forEach {
          it.transform.translation.normalized
        }

        world.update(elapsedTime)
      }
    }

    timers.startPeriodicTimer(TickKey, CheckPlayerConnected, java.time.Duration.ofSeconds(CHECK_CONNECTED_TIMEOUT))
  }

  override fun createReceive(): Receive = receiveBuilder()
    .matchAny {}
    .build()

  companion object {
    private const val CHECK_CONNECTED_TIMEOUT = 10L
    private const val NANO_TO_BASE = 1.0e9

    fun props(config: AppConfig, room: RoomExtension): Props = Props.create(PlayersActor::class.java, config, room)
  }
}
