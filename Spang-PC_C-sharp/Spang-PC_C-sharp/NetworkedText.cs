using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Spang_PC_C_sharp
{
    class NetworkedText : IMessageHandler
    {
        public string text { get; private set; }

        public void Decode(System.IO.BinaryReader reader)
        {
            int length = reader.ReadInt16();
            byte[] textValue = reader.ReadBytes(length);
            this.text = Encoding.UTF8.GetString(textValue);
        }
    }
}
