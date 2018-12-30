using Godot;
using Sfs2X;
using Sfs2X.Core;
using Sfs2X.Entities;
using Sfs2X.Entities.Data;
using Sfs2X.Requests;
using Sfs2X.Util;

namespace GameClient
{
    public class SmartFoxServer : Node
    {
        [Signal]
        public delegate void Connection();

        [Signal]
        public delegate void ConnectionLost();

        [Signal]
        public delegate void Login();

        [Signal]
        public delegate void LoginError(string message);

        [Signal]
        public delegate void Logout();

        [Signal]
        public delegate void RoomJoin();

        [Signal]
        public delegate void RoomJoinError();

        private ServerConfig _config;
        private SmartFox _sfs;

        public override void _Process(float delta)
        {
            _sfs?.ProcessEvents();
        }

        public void OnConnect(string host, int port, string username, string password)
        {
            if (_sfs != null)
            {
                GD.Print("Trying to connect when already connected");
                return;
            }

            _config = new ServerConfig
            {
                Host = host,
                Port = port,
                UserName = username,
                Password = password,
                ZoneName = "World",
                RoomName = "lobby"
            };

            _sfs = new SmartFox {ThreadSafeMode = false, Debug = true};
            _sfs.AddEventListener(SFSEvent.CONNECTION, OnConnection);
            _sfs.AddEventListener(SFSEvent.CONNECTION_LOST, OnConnectionLost);
            _sfs.AddEventListener(SFSEvent.LOGIN, OnLogin);
            _sfs.AddEventListener(SFSEvent.LOGIN_ERROR, OnLoginError);
            _sfs.AddEventListener(SFSEvent.LOGOUT, OnLogout);
            _sfs.AddEventListener(SFSEvent.ROOM_JOIN, OnRoomJoin);
            _sfs.AddEventListener(SFSEvent.ROOM_JOIN_ERROR, OnRoomJoinError);

            GD.Print($"Connecting to {_config.Host}:{_config.Port.ToString()}");

            _sfs.Connect(new ConfigData
            {
                Host = _config.Host,
                Port = _config.Port,
                Zone = _config.ZoneName
            });
        }

        public void OnDisconnect()
        {
            _sfs?.Send(new LeaveRoomRequest());
            _sfs?.Send(new LogoutRequest());
        }

        private void OnConnection(BaseEvent evt)
        {
            if ((bool) evt.Params[SFSEventParam.Success])
            {
                GD.Print($"Connection: {_config.Host}:{_config.Port.ToString()}");

//                _sfs.InitCrypto();
                ISFSObject parameters = new SFSObject();
                parameters.PutUtfString("password", _config.Password);

//				_sfs.Send(new LoginRequest(_config.UserName, _config.Password, _config.ZoneName, parameters));
                _sfs?.Send(new LoginRequest(_config.UserName, _config.Password));

                EmitSignal(nameof(Connection));
            }
            else
            {
                var message = (string) evt.Params[SFSEventParam.ErrorMessage];
                var code = (string) evt.Params[SFSEventParam.ErrorCode];

                GD.Print($"Connection: {_config.Host}:{_config.Port.ToString()} - {code}, {message}");
            }
        }

        private void OnConnectionLost(BaseEvent evt)
        {
            var reason = (string) evt.Params[SFSEventParam.Reason];
            GD.Print($"ConnectionLost: {_config.Host}:{_config.Port.ToString()} - {reason}");

            EmitSignal(nameof(ConnectionLost));
        }

        private void OnLogin(BaseEvent evt)
        {
            var user = (User) evt.Params[SFSEventParam.User];
            GD.Print($"Login: {user.Name}@{_config.Host}:{_config.Port.ToString()}");
            _sfs?.Send(new JoinRoomRequest(_config.RoomName));

            EmitSignal(nameof(Login));
        }

        private void OnLoginError(BaseEvent evt)
        {
            var message = (string) evt.Params[SFSEventParam.ErrorMessage];
            GD.Print($"LoginError: {message}");
            OnLogout(evt);

            EmitSignal(nameof(LoginError), message);
        }

        private void OnLogout(BaseEvent evt)
        {
            GD.Print($"Logout: {_config.Host}:{_config.Port.ToString()}");
            _sfs?.Disconnect();
            _sfs = null;

            EmitSignal(nameof(Logout));
        }

        private void OnRoomJoin(BaseEvent evt)
        {
            var room = (Room) evt.Params[SFSEventParam.Room];
            GD.Print($"RoomJoin: {room.Name}");

            EmitSignal(nameof(RoomJoin));
        }

        private void OnRoomJoinError(BaseEvent evt)
        {
            var message = (string) evt.Params[SFSEventParam.ErrorMessage];
            GD.Print($"RoomJoinError: {message}");

            EmitSignal(nameof(RoomJoinError));
        }
    }
}
