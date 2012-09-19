using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Spang_PC_C_sharp
{
    class Gyroscope : IMessageHandler
    {
        public float AxisX { get; private set; }
        public float AxisY { get; private set; }
        public float AxisZ { get; private set; }

        public void Decode(System.IO.BinaryReader reader)
        {
            AxisX = reader.ReadSingle();
            AxisY = reader.ReadSingle();
            AxisZ = reader.ReadSingle();
        }
    }
}
