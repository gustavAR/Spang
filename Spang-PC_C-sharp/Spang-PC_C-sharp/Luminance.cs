using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Spang_PC_C_sharp
{
    class Luminance : IMessageHandler
    {
        public float Value {get; private set;}

        public void Decode(System.IO.BinaryReader reader)
        {
            Value = reader.ReadSingle();
        }
    }
}
