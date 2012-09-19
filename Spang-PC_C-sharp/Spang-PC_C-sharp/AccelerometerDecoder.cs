using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;

namespace Spang_PC_C_sharp
{
    class Accelerometer : IMessageHandler
    {
        public float X { get; private set; }
        public float Y { get; private set; }
        public float Z { get; private set; }

        public void Decode(BinaryReader reader)
        {
            X = reader.ReadSingle();
            Y = reader.ReadSingle();
            Z = reader.ReadSingle();

            Console.WriteLine(this.ToString());
        }
        public string ToString()
        {
            return string.Format("Accelerometer values: X={0} \t Y={1} \t Z={2}", this.X, this.Y, this.Z);
        }
    }
}
