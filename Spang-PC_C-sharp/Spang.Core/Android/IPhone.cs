/*  Copyright 2012 Joakim Johansson, Lukas Kurtyan, Gustav Alm Rosenblad and Pontus Pall
 *  
 *  This file is part of Spang.

    Spang is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Spang is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Spang.  If not, see <http://www.gnu.org/licenses/>.
 */
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

        void ProcessMessage(IPhoneMessage message);

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
