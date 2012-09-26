using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;

namespace Spang_PC_C_sharp
{
    interface IServerConnection : IConnection
    {
        /// <summary>
        /// The id of the connection.
        /// </summary>
        int ID { get; }

        void StartAsyncRecive();
        void StopAsyncRecive();
    }

    class ServerConnection : IServerConnection
    {
        private readonly IConnection connection;
        private readonly Server server;
     
        private UdpWorker uworker;
        private ServerTcpWorker tworker;
        
        public ServerConnection(IConnection connection, Server server, int id)
        {
            this.server = server;
            this.connection = connection;
            this.ID = id;
        }

        public int ID
        {
            get;
            private set;
        }

        public void StartAsyncRecive()
        {
            if (uworker != null && tworker != null)
                return; //We are already receiving.

            //New worker objects are created and started.

            this.uworker = new UdpWorker(this);
            uworker.Recive += this.OnRecived;

            this.tworker = new ServerTcpWorker(this);
            tworker.Recive += this.OnRecived;

            new Thread(uworker.DoWork).Start();
            new Thread(tworker.DoWork).Start();
        }

        public void StopAsyncRecive()
        {
            if (this.tworker != null || this.uworker != null)
            {
                //Stops the worker threads.
                this.uworker.Recive -= this.OnRecived;
                this.tworker.Recive -= this.OnRecived;

                this.tworker.StopWorking();
                this.uworker.StopWorking();
                this.tworker = null;
                this.uworker = null;
            }
        }

        private void OnRecived(byte[] bytes)
        {
            server.OnRecived(this.ID, bytes);
        }

        #region IConnection Delegation

        public void sendUdp(byte[] toSend)
        {
            this.connection.SendUDP(toSend);
        }

        public void sendTcp(byte[] toSend)
        {
            this.connection.SendTCP(toSend);
        }

        public void Close()
        {
            this.connection.Close();
        }

        public void SendUDP(byte[] data)
        {
            this.connection.SendUDP(data);
        }

        public void SendTCP(byte[] data)
        {
            this.connection.SendTCP(data);
        }

        public byte[] ReciveUDP()
        {
            return this.connection.ReciveUDP();
        }

        public byte[] ReciveTCP()
        {
            return this.connection.ReciveTCP();
        }

        public bool Connected
        {
            get { return this.connection.Connected; }
        }

        public System.Net.IPEndPoint RemoteEndPoint
        {
            get { return this.connection.RemoteEndPoint; }
        }

        public System.Net.IPEndPoint LocalEndPoint
        {
            get { return this.connection.LocalEndPoint;  }
        }

        public int ReciveTimeout
        {
            get { return this.connection.ReciveTimeout; }
            set { this.connection.ReciveTimeout = value; }
        }

        public int SendTimeout
        {
            get { return this.connection.SendTimeout; }
            set { this.connection.SendTimeout = value; }
        }

        #endregion



    }
}