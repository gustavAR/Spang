using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net.Sockets;
using System.Net;

namespace Spang_PC_C_sharp
{
    class Connection : IConnection
    {
        private readonly int port;
        private readonly IPEndPoint remote;
        private readonly UdpClient client;

        public Connection(UdpClient client, IPEndPoint remote)
        {
            this.client = client;
            this.port = ((IPEndPoint)client.Client.LocalEndPoint).Port;
            this.remote = remote;
        }

        public void Send(byte[] data)
        {
            this.Send(data, Protocol.Fast);
        }

        public void Send(byte[] data, Protocol protocol)
        {
            //TODO add protocol stuff that changes the data here!

            client.Send(data, data.Length);
        }

        public byte[] Receive()
        {
            IPEndPoint ep = new IPEndPoint(IPAddress.Any, port);
            return client.Receive(ref ep);            
        }

        public int ReciveTimeout
        {
            get
            {
                return this.client.Client.ReceiveTimeout;
            }
            set
            {
                this.client.Client.ReceiveTimeout = value;
            }
        }

        public int SendTimeout
        {
            get
            {
                return this.client.Client.SendTimeout;
            }
            set
            {
                this.client.Client.SendTimeout = value;
            }
        }

        public void Close()
        {
            this.client.Close();
        }

        public bool Connected
        {
            get { return this.client.Client.Connected; }
        }

        public System.Net.IPEndPoint RemoteEndPoint
        {
            get { return this.remote; }
        }

        public System.Net.IPEndPoint LocalEndPoint
        {
            get { return (IPEndPoint)this.client.Client.RemoteEndPoint; }
        }
    }
}