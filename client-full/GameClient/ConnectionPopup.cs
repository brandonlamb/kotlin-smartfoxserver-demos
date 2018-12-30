using Godot;

namespace GameClient
{
    public class ConnectionPopup : Popup
    {
        [Signal]
        public delegate void ConnectServer();

        [Signal]
        public delegate void DisconnectServer();

        public override void _Ready()
        {
        }

        public void OnConnect()
        {
            EmitSignal(nameof(ConnectServer));
        }

        public void OnDisconnect()
        {
            EmitSignal(nameof(DisconnectServer));
        }
    }
}