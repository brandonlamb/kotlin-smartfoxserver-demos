package com.example.infrastructure.akka

import io.micronaut.context.annotation.Factory

@Factory
class ActorFactory {
/*
  @Bean
  @Named("game")
  fun createGameActor(system: ActorSystem, config: AppConfig): ActorRef = system.actorOf(
    GameActor.props(config).withDispatcher(config.dispatchers.pinned),
    "game"
  )

  @Bean
  @Named("scene-tree")
  fun createSceneTreeActor(system: ActorSystem, config: AppConfig): ActorRef = system.actorOf(
    SceneTreeActor.props(config).withDispatcher(config.dispatchers.pinned),
    "scene-tree"
  )

  @Bean
  @Named("scene-tree-routed")
  fun createSceneTreeActorRouted(system: ActorSystem, config: AppConfig): ActorRef = system.actorOf(
    RoundRobinPool(4).props(SceneTreeActor.props(config).withDispatcher(config.dispatchers.affinity)),
    "scene-tree-routed"
  )

  @Bean
  @Named("sol")
  fun createSolActor(system: ActorSystem, config: AppConfig): ActorRef = system.actorOf(
    SolActor.props(config, "sold").withDispatcher(config.dispatchers.affinity),
    "sol"
  )
*/
}
