using Godot;
using Sfs2X;
using Sfs2X.Core;
using Sfs2X.Entities;
using Sfs2X.Requests;
using Sfs2X.Util;

namespace Demo1
{
		public class SmartFoxServer : Node
		{
				private readonly ServerConfig _config = new ServerConfig
				{
						Host = "localhost",
						Port = 9933,
						UserName = "user1",
						Password = "password1",
						ZoneName = "Zone1",
						RoomName = "Room01"
				};

				private readonly SmartFox _sfs = new SmartFox();

				public override void _Ready()
				{
						_sfs.ThreadSafeMode = false;
						_sfs.Debug = true;

						_sfs.AddEventListener(SFSEvent.CONNECTION, OnConnection);
						_sfs.AddEventListener(SFSEvent.CONNECTION_LOST, OnConnectionLost);
						_sfs.AddEventListener(SFSEvent.LOGIN, OnLogin);
						_sfs.AddEventListener(SFSEvent.LOGIN_ERROR, OnLoginError);
						_sfs.AddEventListener(SFSEvent.ROOM_JOIN, OnRoomJoin);
						_sfs.AddEventListener(SFSEvent.ROOM_JOIN_ERROR, OnRoomJoinError);

						var cfg = new ConfigData
						{
								Host = _config.Host,
								Port = _config.Port,
								Zone = _config.ZoneName
						};
						_sfs.Connect(cfg);
				}

				public override void _Process(float delta)
				{
						_sfs.ProcessEvents();
				}

				private void OnConnection(BaseEvent evt)
				{
						if ((bool) evt.Params["success"])
						{
								GD.Print($"\nConnected to server {_config.Host}:{_config.Port.ToString()}\n");
								_sfs.Send(new LoginRequest(_config.UserName, _config.Password));
						}
						else
						{
								var message = (string) evt.Params["errorMessage"];
								var code = (string) evt.Params["errorCode"];

								GD.Print($"Unable to connect to {_config.Host}:{_config.Port.ToString()}");
								GD.Print($"code={code}, message={message}");
						}
				}

				private void OnConnectionLost(BaseEvent evt)
				{
						var reason = (string) evt.Params["reason"];
						GD.Print($"Lost connection to {_config.Host}");
						GD.Print($"Connection was lost; reason is: ${reason}");
				}

				private void OnLogin(BaseEvent evt)
				{
						var user = (User) evt.Params["user"];
						GD.Print($"Logged into {_config.Host} as {user.Name}");
						_sfs.Send(new JoinRoomRequest(_config.RoomName));
				}

				private void OnLoginError(BaseEvent evt)
				{
						_sfs.Disconnect();

						var user = (string) evt.Params["user"];
						var message = (string) evt.Params["errorMessage"];
						var code = (string) evt.Params["errorCode"];

						GD.Print($"Unable to login to {_config.Host} as {user}");
						GD.Print($"code={code}, message={message}");
				}

				private void OnRoomJoin(BaseEvent evt)
				{
						var room = (Room) evt.Params["room"];
						GD.Print($"\nYou joined room '{room.Name}'\n");
				}

				private void OnRoomJoinError(BaseEvent evt)
				{
						var message = (string) evt.Params["errorMessage"];
						GD.Print($"Room join failed: {message}");
				}
		}
}