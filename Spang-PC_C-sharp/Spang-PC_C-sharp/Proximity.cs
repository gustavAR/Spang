using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Spang_PC_C_sharp
{
    class Proximity : IMessageHandler
    {
        public float proximity {get; private set;}

        public void Decode(System.IO.BinaryReader reader)
        {
            this.proximity = reader.ReadSingle();
        }
    }
}
