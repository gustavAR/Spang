using System;
using Spang.Core.Utils;
namespace Spang.Core.Android
{
    public interface IPhone
    {
        Vector3 Accelerometer { get;}
        event Action<Vector3, Vector3> AccelerometerChanged;
        Vector2 GPSLocation { get; }
        event Action<Vector2, Vector2> GPSLocationChanged;
        Vector3 Gyroscope { get; }
        event Action<Vector3, Vector3> GyroscopeChanged;
        event Action<int> Horizontalscroll;
        event Action LongTapped;
        float Luminance { get; }
        event Action<float, float> LuminanceChanged;
        Vector3 MagneticField { get;}
        event Action<Vector3, Vector3> MagneticFieldChanged;
        event Action<string> NetworkedText;
        Vector3 Orientation { get; }
        event Action<Vector3, Vector3> OrientationChanged;
        float Pressure { get; }
        event Action<float, float> PressureChanged;
        void ProcessMessage(byte[] data);
        float Proximity { get; }
        event Action<float, float> ProximityChanged;
        event Action Tapped;
        event Action<int, int> TouchMoved;
        event Action<int> VerticalScroll;
        event Action VolumeDown;
        event Action VolumeUp;
    }
}
