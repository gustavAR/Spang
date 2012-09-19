using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Spang_PC_C_sharp
{
    class MagneticField : IMessageHandler
    {
        public float north { get; private set; }
        public float east { get; private set; }
        public float down { get; private set; }

        public void Decode(System.IO.BinaryReader reader)
        {
            north = reader.ReadSingle();
            east = reader.ReadSingle();
            down = reader.ReadSingle();
        }
    }
}
