using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Text;
using System.Net.Sockets;

namespace Spang_PC_C_sharp
{
    class Server
    {
        public IConnection ReciveConnection(int port)
        {
            IPAddress addr = IPAddress.Parse("129.16.184.28");
            TcpListener listener = new TcpListener(addr, 1337);
            listener.Start();
            
            TcpClient tpcClient = listener.AcceptTcpClient();
            IPEndPoint localEndPoint = (IPEndPoint)tpcClient.Client.LocalEndPoint;
            UdpClient udpClient = new UdpClient(new IPEndPoint(IPAddress.Any, localEndPoint.Port));


            Connection connection = new Connection(tpcClient, udpClient, localEndPoint.Port);
            return connection;
        }
    }
}
