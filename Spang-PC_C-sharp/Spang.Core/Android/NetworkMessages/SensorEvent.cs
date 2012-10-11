using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Spang.Core.Android.NetworkMessages
{
    class SensorEvent : IPhoneMessage
    {
        public int SensorID;
        public float[] SensorData;

        public SensorEvent(int id, float[] data)
        {
            // TODO: Complete member initialization
            this.SensorID = id;
            this.SensorData = data;
        }
    }
}
