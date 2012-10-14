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
using System.Net;
using Spang.Core.Utils;

namespace Spang.Core.Network
{
    public class ConnectionListener : Spang.Core.Network.IConnectionListener
    {
        private const int DEFAULT_CONNECTION_TIMEOUT = 1000;

        public int ConnectTimeout
        {
            get;
            set; 
        }

        public ConnectionListener()
        {
            this.ConnectTimeout = DEFAULT_CONNECTION_TIMEOUT;
        }

        public IConnection ReciveConnection(int port)
        {

            Connection connection;
            while (true)
            {
                IPEndPoint endpoint = ReciveConnectionRequest(port);
                UdpClient client = SendConnectionAck(endpoint);

                if (RecivedConnectionAck(endpoint, client))
                {
                    connection = new Connection(client, endpoint);
                    break;
                }
                else
                {
                    client.Close();
                }
            }

            return connection;
        }

        private bool RecivedConnectionAck(IPEndPoint endpoint, UdpClient client)
        {
            try
            {
                //Recive any message and we know that we have connected.
                int reciveTimeout = client.Client.ReceiveTimeout;
                client.Client.ReceiveTimeout = this.ConnectTimeout;
                client.Receive(ref endpoint);
                client.Client.ReceiveTimeout = reciveTimeout;
                return true; 
            }
            catch (Exception)
            {
                return false;
            }
        }

        private UdpClient SendConnectionAck(IPEndPoint endpoint)
        {
            UdpClient client = new UdpClient(new IPEndPoint(IPAddress.Any, 0));
            client.Connect(endpoint);

            Packer packer = new Packer(4);
            packer.Pack(((IPEndPoint)client.Client.LocalEndPoint).Port);      
            client.Send(packer.GetPackedData(), 4);
            return client;
        }

        private IPEndPoint ReciveConnectionRequest(int port)
        {
            UdpClient listener = new UdpClient(new IPEndPoint(IPAddress.Any, port));
            IPEndPoint endpoint = new IPEndPoint(IPAddress.Any, port);
            listener.Receive(ref endpoint);
            listener.Close();
            return endpoint;
        }
    }
}
