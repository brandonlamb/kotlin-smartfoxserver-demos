# SmartFoxServer 2X DemoExtension

This demo tries to show how to build a concurrent, thread-safe server implementation utilizing the following concepts/technologies:

1. Akka Actors
1. Domain-Driven Design
1. Ports-and-Adapters Architecture

## Zone/Room Architecture

In SFS2X, a `Zone` represents a game, so we'll have only one. A `Room` can represent a map or level. In MMO terms, I view a Room as a "zone" like "Elwynn Forest" in WoW.

Because SFS2X calls methods on extensions concurrently, we have to deal with typical multi-threading concurrency issues.

To avoid these headaches, I've tried to implement `Akka Actors` to handle state and business logic.

An outline of the general extension-to-actor structure would look like this:

### Zone/Room Hierarchy

* World (ZoneExtension)
  * lobby (StaticExtension)
  * room-1 (StaticExtension)
  * room-2 (StaticExtension)
  * instance-1 (InstanceExtension)
  
### Extension Class Hierarchy

* SFSExtension
  * BaseExtension
    * ZoneExtension
    * RoomExtension
      * InstanceExtension
      * StaticExtension

### Actor System Hierarchy

* world (ZoneActor)
  * rooms (RoomsActor)
    * lobby (RoomActor)
      * players (PlayersActor)
        * 13 (PlayerActor)
    * room-1 (RoomActor)
      * players (PlayersActor)
        * 0 (PlayerActor)
    * room-2 (RoomActor)
      * players (PlayersActor)
        * 1 (PlayerActor)
        * 2 (PlayerActor)
    * instance-1 (RoomActor)
      * players (PlayersActor)
        * 3 (PlayerActor)
        * 4 (PlayerActor)
        * 5 (PlayerActor)
