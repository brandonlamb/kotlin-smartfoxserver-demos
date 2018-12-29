using Godot;

namespace Demo2
{
    public class ConnectionPopup : Popup
    {
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

        [Signal]
        public delegate void ConnectServer();

        [Signal]
        public delegate void DisconnectServer();
    }
}
