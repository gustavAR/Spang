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
using System.Net.Sockets;

namespace Spang.Core.Network
{
    /// <summary>
    /// A worker class that listens to UDP messages.
    /// </summary>
    class UdpWorker : ContinuousWorker
    {
        //The connection used.
        private readonly IConnection connection;

        /// <summary>
        /// Invoked when a new message arrives.
        /// </summary>
        public event Action<byte[]> Recive;

        /// <summary>
        /// Invoked when a recived message times out.
        /// </summary>
        public event Action Timeout;

        /// <summary>
        /// Creates a UdpWorker.
        /// </summary>
        /// <param name="connection">The connection used.</param>
        public UdpWorker(IConnection connection)
        {
            this.connection = connection;
        }

        protected override void DoWorkInternal()
        {
            try
            {
                //Recives and sends the udp message.
                byte[] bytes = connection.Receive();

                if (this.Recive != null)
                    this.Recive(bytes);
            }
            catch (SocketException)
            {
                if (this.Timeout != null)
                    this.Timeout();

                this.StopWorking();
            }
            catch (Exception exe)
            {
                //Something went wrong while we a recived the message.
                //Since the connection is unusable we exit the thread.
                Console.WriteLine("UDP read failed");
                Console.WriteLine(exe);
                this.StopWorking();
            }
        }
    }
}
