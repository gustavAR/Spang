using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.Net.Sockets;

namespace Spang_PC_C_sharp
{
    class Network
    {

        public static IConnection ReciveConnection(int port)
        {
            TcpListener listener = new TcpListener(IPAddress.Any, port);
            listener.Start();
            
            TcpClient tcpClient = listener.AcceptTcpClient();
            UdpClient udpClient = CreateUpdClient((IPEndPoint)tcpClient.Client.LocalEndPoint, (IPEndPoint)tcpClient.Client.RemoteEndPoint);

            listener.Stop();

            return new Connection(tcpClient, udpClient);
        }

        public static IConnection ConnectTo(IPEndPoint endpoint)
        {
            TcpClient tcpClient = new TcpClient();
            tcpClient.Connect(endpoint);
            UdpClient udpClient = CreateUpdClient((IPEndPoint)tcpClient.Client.LocalEndPoint, endpoint);

            return new Connection(tcpClient, udpClient);
        }

        public static IConnection ConnectTo(int port, string host)
        {
            return ConnectTo(new IPEndPoint(IPAddress.Parse(host), port));
        }

        private static UdpClient CreateUpdClient(IPEndPoint localEndPoint, IPEndPoint remoteEndPoint)
        {
            UdpClient udpClient = new UdpClient(new IPEndPoint(IPAddress.Any, localEndPoint.Port));
            udpClient.Connect(remoteEndPoint);
            return udpClient;
        }

        
    }
}