/*  Copyright 2012 Joakim Johansson, Lukas Kurtyan, Gustav Alm Rosenblad and Pontus Pall
 *  
 *  This file is part of Spang.

    Spang is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Spang is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Spang.  If not, see <http://www.gnu.org/licenses/>.
 */
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

