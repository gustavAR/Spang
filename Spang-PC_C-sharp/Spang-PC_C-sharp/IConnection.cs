using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;

namespace Spang_PC_C_sharp
{
    interface IConnection
    {
        /// <summary>
        /// Send a message using the UDP-protocoll.
        /// </summary>
        /// <param name="data">The Message</param>
        void SendUDP(byte[] data);

        /// <summary>
        /// Send a message using the TCP-protocoll.
        /// </summary>
        /// <param name="data">The message</param>
        void SendTCP(byte[] data);

        /// <summary>
        /// Recives a UDP message.
        /// </summary>
        /// <returns>The message recived.</returns>
        byte[] ReciveUDP();

        /// <summary>
        /// Recives a TCP message.
        /// </summary>
        /// <returns>The message recived.</returns>
        byte[] ReciveTCP();

        /// <summary>
        /// Gets or sets the time TCP send/recive will block.
        /// </summary>
        int Timeout { get; set; }

        /// <summary>
        /// Closes the connection.
        /// </summary>
        void Close();

        /// <summary>
        /// Gets the connected status.
        /// </summary>
        bool Connected { get; }

        /// <summary>
        /// The remote endpoint the connection is connected to.
        /// </summary>
        IPEndPoint RemoteEndPoint { get; }

        /// <summary>
        /// The local endpoint the connection is connected from.
        /// </summary>
        IPEndPoint LocalEndPoint { get; }
    }
}

