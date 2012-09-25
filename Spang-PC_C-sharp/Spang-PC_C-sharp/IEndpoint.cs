using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.Net.Sockets;
using System.Threading;

namespace Spang_PC_C_sharp
{
    interface IEndpoint
    {
        /// <summary>
        /// Gets the connection status of the IEndpoint.
        /// </summary>
        bool IsConnected { get; }

        /// <summary>
        /// Gets or Sets the time a connection will wait for a 
        /// message without dissconnecting.
        /// <remarks>
        /// If timeout is 0 it never crashes.
        /// However then it cannot detect connection faliours.
        /// 5-15 sec is a good Timeout range.
        /// </remarks>
        /// </summary>
        int Timeout { get; set; }
    }
}
