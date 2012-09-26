using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Spang_PC_C_sharp
{
    class ConnectionEventArgs
    {
        public readonly int ID;
        public ConnectionEventArgs(int id) { this.ID = id; }
    }

    class RecivedEventArgs : ConnectionEventArgs
    {
        public readonly byte[] Data;
        public RecivedEventArgs(int id, byte[] Data) : base(id) { this.Data = Data; }
    }

    class DisconnectionEventArgs : ConnectionEventArgs
    {
        public readonly DisconnectCause Cause;
        public DisconnectionEventArgs(int id, DisconnectCause Cause) : base(id) { this.Cause = Cause; }
    }
}
