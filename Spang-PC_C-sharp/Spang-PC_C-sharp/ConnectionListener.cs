using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net.Sockets;
using System.Net;

namespace Spang_PC_C_sharp
{
    class ConnectionListener
    {
        public IConnection ReciveConnection(int port)
        {
            UdpClient listener = new UdpClient(new IPEndPoint(IPAddress.Any, 1337));
            IPEndPoint endpoint = new IPEndPoint(IPAddress.Any, port);
            listener.Receive(ref endpoint);
            listener.Close();

            UdpClient client = new UdpClient(new IPEndPoint(IPAddress.Any, 0));
            client.Connect(endpoint);
            client.Send(BitConverter.GetBytes(((IPEndPoint)client.Client.LocalEndPoint).Port), 4);

            return new Connection(client, endpoint);
        }
    }
}
