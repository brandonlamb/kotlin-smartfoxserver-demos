# kotlin-smartfoxserver-demos

## Running

1. You need a working SFS2X instance already
1. `mvn clean package && cp target/DemoExtension.jar PATH-TO-SFS2X/extensions/__lib__`
1. Start the server, load the admin tool
1. Make sure there is a zone called `World`, set the extension to `ZoneExtension`
1. Remove any rooms in World zone (all rooms created via code)
1. Restart server and theoretically it should just work

The server will create `lobby`, `room-1` and `room-2` rooms.

