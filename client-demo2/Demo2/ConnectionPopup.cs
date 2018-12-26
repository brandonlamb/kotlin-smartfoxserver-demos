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
            EmitSignal(nameof(Connect));
        }

        public void OnDisconnect()
        {
            EmitSignal(nameof(Disconnect));
        }

        [Signal]
        public delegate void Connect();

        [Signal]
        public delegate void Disconnect();
    }
}
