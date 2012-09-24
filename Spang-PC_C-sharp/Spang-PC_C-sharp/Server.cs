using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Text;
using System.Net.Sockets;
using System.Threading;

namespace Spang_PC_C_sharp
{
    class Server
    {
        public event Action<IConnection> RecivedConnection;


        /// <summary>
        /// Starts listening for incoming 
        /// </summary>
        /// <param name="port"></param>
        public void Start(int port)
        {
            new Thread(() => 
            {
                IPAddress addr = IPAddress.Any;
                TcpListener listener = new TcpListener(addr, port);
                listener.Start();

                while (true)
                {
                    TcpClient tpcClient = listener.AcceptTcpClient();
                    IPEndPoint localEndPoint = (IPEndPoint)tpcClient.Client.LocalEndPoint;
                    UdpClient udpClient = new UdpClient(new IPEndPoint(IPAddress.Any, localEndPoint.Port));
                    Connection connection = new Connection(tpcClient, udpClient, localEndPoint.Port);

                    tpcClient.ReceiveTimeout = 3000;
                   

                    if (this.RecivedConnection != null)
                        this.RecivedConnection(connection);
                }

            }).Start();
        }

        public IConnection ReciveConnection(int port)
        {
            IPAddress addr = IPAddress.Any; 
            TcpListener listener = new TcpListener(addr, port);
            listener.Start();
            
            TcpClient tpcClient = listener.AcceptTcpClient();
            IPEndPoint localEndPoint = (IPEndPoint)tpcClient.Client.LocalEndPoint;
            UdpClient udpClient = new UdpClient(new IPEndPoint(IPAddress.Any, localEndPoint.Port));
            listener.Stop();

            Connection connection = new Connection(tpcClient, udpClient, localEndPoint.Port);
            return connection;
        }
    }
}
