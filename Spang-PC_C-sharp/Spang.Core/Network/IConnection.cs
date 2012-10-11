using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;

namespace Spang.Core.Network
{
    public interface IConnection
    {
        /// <summary>
        /// Send a message using the UDP-protocoll.
        /// </summary>
        /// <param name="data">The Message</param>
        void Send(byte[] data);

        /// <summary>
        /// Sends a message using the given protocol.
        /// </summary>
        /// <param name="data">The message.</param>
        /// <param name="protocol">The used protocol.</param>
        void Send(byte[] data, Protocol protocol); 

        /// <summary>
        /// Recives a UDP message.
        /// </summary>
        /// <returns>The message recived.</returns>
        byte[] Receive();

        /// <summary>
        /// Gets or sets the time TCP recive will block.
        /// </summary>
        int ReciveTimeout { get; set; }

        /// <summary>
        /// Gets or sets the time TCP send will block.
        /// </summary>
        int SendTimeout { get; set; }

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

