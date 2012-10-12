using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Spang.Core.Network
{
    public class ConnectionEventArgs
    {
        public readonly int ID;
        public ConnectionEventArgs(int id) { this.ID = id; }
    }

    public class RecivedEventArgs : ConnectionEventArgs
    {
        public readonly Object Message;
        public RecivedEventArgs(int id, Object message) : base(id) 
        {
            this.Message = message; 
        }
    }

    public class DisconnectionEventArgs : ConnectionEventArgs
    {
        public readonly DisconnectCause Cause;
        public DisconnectionEventArgs(int id, DisconnectCause Cause) : base(id) 
        { 
            this.Cause = Cause; 
        }
    }
}
