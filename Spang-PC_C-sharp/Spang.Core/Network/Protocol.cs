using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Spang.Core.Network
{
    [Flags]
    public enum Protocol
    {
        Unordered = 0x01,
        Ordered = 0x02,
        Reliable = 0x04,
        OrderedReliable = 0x08
    }
}
