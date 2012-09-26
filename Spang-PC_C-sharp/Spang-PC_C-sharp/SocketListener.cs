using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net.Sockets;
using System.Net;

namespace Spang_PC_C_sharp
{
    class SocketListener
    {
        private Socket socket;

        public void Start(int port)
        {
            IPEndPoint endPoint = new IPEndPoint(IPAddress.Any, port);
            socket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);

            socket.Bind(endPoint);
            socket.Listen(5);
        }

        public TcpClient AcceptTcpClient()
        {
            TcpClient client = new TcpClient();
            client.Client = this.AcceptSocket();

            return client;
        }

        public Socket AcceptSocket()
        {
            return socket.Accept();
        }

        public void Stop()
        {
            socket.Close();
        }

    }
}
