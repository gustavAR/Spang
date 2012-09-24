using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.Net.Sockets;
using System.Threading;

namespace Spang_PC_C_sharp
{
    interface IEndpoint
    {
        void Connect(IPEndPoint endpoint);
        void Connect(int port, string host);

        void ReviceConnection(int port);
        void Reconnect();

        void StartReceiving();
        void StopReceiving();

        event Action Connected;
        event Action Dissconnected;
        event Action<byte[]> Recived;

        bool IsConnected { get; }
        int Timeout { get; set; }
    }

    class CEndPoint : IEndpoint
    {
        private IConnection connection;
        private Object _lock = new Object();

        private bool connected;
        public bool IsConnected
        {
            get
            {
                lock (_lock)
                {
                    return connected;
                }
            }
            private set
            {
                lock (_lock)
                {
                    connected = value;
                }
            }
        }

        public int Timeout
        {
            get { return this.connection.Timeout;  }
            set { this.connection.Timeout = value; }
        }

        public void Connect(IPEndPoint endpoint)
        {
            if (connection != null)
                connection.Close();

            TcpClient tcpClient = new TcpClient();
            tcpClient.Connect(endpoint);
            this.CreateConnection(tcpClient);

            this.OnConnected();
        }

        public void Connect(int port, string host)
        {
            this.Connect(new IPEndPoint(IPAddress.Parse(host), port));
        }

        private void CreateConnection(TcpClient tcpClient)
        {
            IPEndPoint localEndPoint = (IPEndPoint)tcpClient.Client.LocalEndPoint;
            UdpClient udpClient = new UdpClient(new IPEndPoint(IPAddress.Any, localEndPoint.Port));
            this.connection = new Connection(tcpClient, udpClient, localEndPoint.Port);
        }
        
        public void ReviceConnection(int port)
        {
            if (this.connection != null)
                this.connection.Close();

            TcpListener listener = new TcpListener(IPAddress.Any, port);
            listener.Start();

            TcpClient tcpClient = listener.AcceptTcpClient();
            this.CreateConnection(tcpClient);

            listener.Stop();

            this.OnConnected();
        }

        public void Reconnect()
        {
            if (this.connection == null)
                throw new ArgumentException("We have never had a connection so we cannot reconnect");
            
        }

        public void StartReceiving()
        {
            new Thread(ReadUDP).Start();
            new Thread(ReadTCP).Start();
        }

        public void StopReceiving()
        {
            this.OnDissconnected();
        }

        private void ReadTCP()
        {
            while (this.IsConnected)
            {
                try
                {
                    byte[] bytes = this.connection.reciveTCP();
                    this.OnRecived(bytes);
                }
                catch (Exception ex)
                {
                    Console.WriteLine("Exiting tcp");
                    Console.WriteLine(ex.Message);
                    this.OnDissconnected();

                }
            }
        }

        private void ReadUDP()
        {
            while (this.IsConnected)
            {
                try
                {
                    byte[] bytes = this.connection.reciveUDP();
                    this.OnRecived(bytes);
                }
                catch (Exception ex)
                {
                    Console.WriteLine("Exiting UDP");
                    //Console.WriteLine(ex.Message);
                    //this.OnDissconnected();
                    //break;
                }
            }
        }

        private void OnConnected()
        {
            this.IsConnected = true;
            if (this.Connected != null)
                this.Connected();
        }

        private void OnRecived(byte[] recived)
        {
            if (this.Recived != null)
                this.Recived(recived);
        }

        private void OnDissconnected()
        {
            this.IsConnected = false;
            this.connection.Close();
            if (this.Dissconnected != null)
                this.Dissconnected();
        }

        public event Action Connected;
        public event Action Dissconnected;
        public event Action<byte[]> Recived;
    }
}
