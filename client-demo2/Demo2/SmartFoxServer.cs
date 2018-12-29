using Godot;
using Sfs2X;
using Sfs2X.Core;
using Sfs2X.Entities;
using Sfs2X.Entities.Data;
using Sfs2X.Requests;
using Sfs2X.Util;

namespace Demo2
{
    public class SmartFoxServer : Node
    {
        private readonly ServerConfig _config = new ServerConfig
        {
            Host = "localhost",
            Port = 9933,
            UserName = "user1",
            Password = "password1",
            ZoneName = "World",
            RoomName = "room-1"
        };

        private SmartFox _sfs;

        public override void _Ready()
        {
            // Connect to connection popup signals for connect and disconnect
            var node = (Popup) GetTree().GetRoot().GetNode("game/CanvasLayer/Connection");
            node.Connect("ConnectServer", this, "OnConnect");
            node.Connect("DisconnectServer", this, "OnDisconnect");
        }

        public override void _Process(float delta)
        {
            _sfs?.ProcessEvents();
        }

        public void OnConnect()
        {
            if (_sfs != null)
            {
                return;
            }

            var cfg = new ConfigData
            {
                Host = _config.Host,
                Port = _config.Port,
                Zone = _config.ZoneName
            };

            GD.Print("Connecting...");

            _sfs = new SmartFox {ThreadSafeMode = false, Debug = true};
            _sfs.AddEventListener(SFSEvent.CONNECTION, OnConnection);
            _sfs.AddEventListener(SFSEvent.CONNECTION_LOST, OnConnectionLost);
            _sfs.AddEventListener(SFSEvent.LOGIN, OnLogin);
            _sfs.AddEventListener(SFSEvent.LOGIN_ERROR, OnLoginError);
            _sfs.AddEventListener(SFSEvent.ROOM_JOIN, OnRoomJoin);
            _sfs.AddEventListener(SFSEvent.ROOM_JOIN_ERROR, OnRoomJoinError);
            _sfs.AddEventListener(SFSEvent.LOGOUT, OnLogout);

            _sfs.Connect(cfg);
        }

        public void OnDisconnect()
        {
//            _sfs.Send(new LeaveRoomRequest());
            _sfs?.Send(new LogoutRequest());
        }

        private void OnConnection(BaseEvent evt)
        {
            if ((bool) evt.Params[SFSEventParam.Success])
            {
                GD.Print($"\nConnected to server {_config.Host}:{_config.Port.ToString()}\n");

//                _sfs.InitCrypto();
                ISFSObject parameters = new SFSObject();
                parameters.PutUtfString("password", _config.Password);

//				_sfs.Send(new LoginRequest(_config.UserName, _config.Password, _config.ZoneName, parameters));
                _sfs?.Send(new LoginRequest(_config.UserName, _config.Password));
            }
            else
            {
                var message = (string) evt.Params[SFSEventParam.ErrorMessage];
                var code = (string) evt.Params[SFSEventParam.ErrorCode];

                GD.Print($"Unable to connect to {_config.Host}:{_config.Port.ToString()}");
                GD.Print($"code={code}, message={message}");
            }
        }

        private void OnConnectionLost(BaseEvent evt)
        {
            var reason = (string) evt.Params[SFSEventParam.Reason];
            GD.Print($"Lost connection to {_config.Host}");
            GD.Print($"Connection was lost; reason is: ${reason}");
        }

        private void OnLogin(BaseEvent evt)
        {
            var user = (User) evt.Params[SFSEventParam.User];
            GD.Print($"Logged into {_config.Host} as {user.Name}");
            _sfs?.Send(new JoinRoomRequest(_config.RoomName));
        }

        private void OnLoginError(BaseEvent evt)
        {
            var message = (string) evt.Params[SFSEventParam.ErrorMessage];
            GD.Print($"Login error; message={message}");
            OnLogout(evt);
        }

        private void OnLogout(BaseEvent evt)
        {
            GD.Print($"Logged out of {_config.Host}");
            _sfs?.Disconnect();
            _sfs = null;
        }

        private void OnRoomJoin(BaseEvent evt)
        {
            var room = (Room) evt.Params[SFSEventParam.Room];
            GD.Print($"\nYou joined room '{room.Name}'\n");
        }

        private void OnRoomJoinError(BaseEvent evt)
        {
            var message = (string) evt.Params[SFSEventParam.ErrorMessage];
            GD.Print($"Room join failed: {message}");
        }
    }
}
