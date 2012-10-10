using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Spang_PC_C_sharp
{
    [Flags]
    enum Protocol
    {
        Unordered = 0x01,
        Ordered = 0x02,
        Reliable = 0x04,
        OrderedReliable = 0x08
    }
}
