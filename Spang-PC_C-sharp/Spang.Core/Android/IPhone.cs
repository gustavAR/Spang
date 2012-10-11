using System;
using Spang.Core.Utils;
using System.Collections.Generic;

namespace Spang.Core.Android
{
    public interface IPhone
    {
        Vector2 GPSLocation { get; }
        Vector3 Accelerometer { get; }
        Vector3 Gyroscope { get; }
        Vector3 MagneticField { get; }
        Vector3 Orientation { get; }

        float Proximity { get; }
        float Luminance { get; }
        float Pressure { get; }

        List<Touch> Touches { get; }

        void ProcessMessage(UnPacker unpacker);

        event Action<Vector3, Vector3> AccelerometerChanged;
        event Action<Vector2, Vector2> GPSLocationChanged;
        event Action<Vector3, Vector3> GyroscopeChanged;
        event Action<Vector3, Vector3> MagneticFieldChanged;
        event Action<Vector3, Vector3> OrientationChanged;
        
        event Action<float, float> LuminanceChanged;
        event Action<float, float> PressureChanged;
        event Action<float, float> ProximityChanged;

        event Action VolumeDown;
        event Action VolumeUp;

        event Action Tap;
        event Action LongTap;
        event Action Down;
        event Action Up;
        event Action<int, int> Move;
        event Action<int> MultiTap;
        event Action<int, int, int> MulitiMove;
        event Action<int> Pinch;
    }
}
