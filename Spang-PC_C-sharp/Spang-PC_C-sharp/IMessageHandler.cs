using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;

namespace Spang_PC_C_sharp
{
    interface IMessageHandler
    {
        void Decode(BinaryReader reader);
    }
}
