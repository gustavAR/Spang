using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.Net.Sockets;
using System.IO;

namespace Spang_PC_C_sharp
{
    class Network
    {

        public static IConnection ReciveConnection(int port)
        {

            SocketListener listener = new SocketListener();
            listener.Start(port);

            TcpClient tcpClient = listener.AcceptTcpClient();
            UdpClient udpClient = CreateUpdClient((IPEndPoint)tcpClient.Client.LocalEndPoint, (IPEndPoint)tcpClient.Client.RemoteEndPoint);
            /*
            UdpClient udpClient = CreateUpdClient(0, (IPEndPoint)tcpClient.Client.RemoteEndPoint);
                     
            BinaryWriter writer = new BinaryWriter(tcpClient.GetStream());
            writer.Write(((IPEndPoint)udpClient.Client.LocalEndPoint).Port);
            */

            listener.Stop();


            return new Connection(tcpClient, udpClient);
        }

        public static IConnection ConnectTo(IPEndPoint endpoint)
        {
            TcpClient tcpClient = new TcpClient();
            tcpClient.Connect(endpoint);

            NetworkStream stream = tcpClient.GetStream();
            BinaryReader reader = new BinaryReader(stream);
            int udpPort = reader.ReadInt32();

            IPEndPoint udpEndPoint = new IPEndPoint(endpoint.Address, udpPort);
            UdpClient udpClient = CreateUpdClient(((IPEndPoint)tcpClient.Client.LocalEndPoint).Port ,udpEndPoint);


            return new Connection(tcpClient, udpClient);
        }

        public static IConnection ConnectTo(int port, string host)
        {
            return ConnectTo(new IPEndPoint(IPAddress.Parse(host), port));
        }

        //Creates 
        private static UdpClient CreateUpdClient(int local, IPEndPoint remoteEndPoint)
        {
            UdpClient udpClient = new UdpClient(new IPEndPoint(IPAddress.Any, local));
            udpClient.Connect(remoteEndPoint);
            return udpClient;
        }

        [Obsolete]
        private static UdpClient CreateUpdClient(IPEndPoint localEP, IPEndPoint remoteEP)
        {
            UdpClient udpClient = new UdpClient(new IPEndPoint(IPAddress.Any, localEP.Port));
            udpClient.Connect(remoteEP);

            return udpClient;
        }
    }
}