using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Spang_PC_C_sharp
{
    class Pressure : IMessageHandler
    {
        public float PressureValue { get; private set; }

        public void Decode(System.IO.BinaryReader reader)
        {
            this.PressureValue = reader.ReadSingle();
        }
    }
}
