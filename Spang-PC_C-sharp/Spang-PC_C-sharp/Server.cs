using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net.Sockets;
using System.Net;
using System.Threading;

namespace Spang_PC_C_sharp
{
    /// <summary>
    /// A server is a type of endpoint that can have multiple connections.
    /// </summary>
    interface IServer : IEndpoint
    {
        /// <summary>
        /// Starts the server. This will make the 
        /// server able to recive incomming connections and
        /// manage these connections.
        /// </summary>
        /// <remarks>This method needs to be called befoure send methods can be used.</remarks>
        /// <param name="port">The port to listen for conenctions.</param>
        void Start(int port);

        /// <summary>
        /// Stops the server and ends all active connections.
        /// </summary>
        void Stop();

        /// <summary>
        /// Invoked when a new connection is recived.
        /// The parameter is the ID of the new connection.
        /// </summary>
        event Action<int> Connected;
        
        /// <summary>
        /// Invoked when a message is recived on 
        /// the supplied connectionID.
        /// </summary>
        event Action<int, byte[]> Recived;

        /// <summary>
        /// Invoked when a connection dissconnects.
        /// </summary>
        event Action<int> Dissconnected;
        
        /// <summary>
        /// Sends a message to a client using the UDP-protocol.
        /// </summary>
        /// <param name="connectionID">ID of the client to send to.</param>
        /// <param name="toSend">The message to send.</param>
        void sendUdp(int connectionID, byte[] toSend);

        /// <summary>
        /// Sends a message to a client using the TCP-protocol.
        /// </summary>
        /// <param name="connectionID">ID of the client to send to.</param>
        /// <param name="toSend">The message to send.</param>
        void sendTcp(int connectionID, byte[] toSend);
        
        /// <summary>
        /// Sends a message to all connected clients. Using UDP-Protocol.
        /// </summary>
        /// <param name="toSend">The message to send.</param>
        void sendToAllUdp(byte[] toSend);

        /// <summary>
        /// Sends a message to all connected clients. Using TCP-Protocol.
        /// </summary>
        /// <param name="toSend">The message to send.</param>
        void sendToAllTcp(byte[] toSend);
    }

    class Server : IServer
    {
        #region Connection Reciver class
        
        /// <summary>
        /// Worker class that manages the reciving of new connections.
        /// </summary>
        private class ReciverWorker : ContinuousWorker
        {
            public readonly int Port;
            private readonly Server server;

            public ReciverWorker(int port, Server server)
            {
                this.Port = port;
                this.server = server;
            }
            
            protected override void DoWorkInternal()
            {
                //Recives a connection and adds it to the server.
                IConnection connection = Network.ReciveConnection(Port);
                this.server.AddConnection(connection);
            }
        }

        #endregion

        private const int HeatbeatIntevall = 2000;
        private ReciverWorker worker;
        private readonly Dictionary<int, IServerConnection> connections;
        private volatile bool running;
        
        public Server()
        {
            this.connections = new Dictionary<int, IServerConnection>();
        }

        public void Start(int port)
        {
            if (worker != null)
                throw new ArgumentException(string.Format("Already listening to {0}", worker.Port));

            worker = new ReciverWorker(port, this);
            new Thread(worker.DoWork).Start();

            running = true;
            new Thread(this.KeepAlive).Start();
        }

        public void Stop()
        {
            if (worker != null)
            {
                worker.StopWorking();
                worker = null;
            }

            this.running = false;

            lock (this.connections)
            {
                foreach (var item in this.connections.Values)
                {
                    item.StopAsyncRecive();
                }
            }
        }

        internal void AddConnection(IConnection connection)
        {
            ServerConnection sConnection = null;
            lock (this.connections)
            {
                int id = GetAvaibleID();
                sConnection = new ServerConnection(connection, this, id);
                this.connections.Add(id, sConnection);
            }

            sConnection.ReciveTimeout = this.timeout;
            sConnection.SendTimeout = this.timeout;

            sConnection.StartAsyncRecive();  

            this.OnConnected(sConnection.ID);
        }
               
        internal void RemoveConnection(IServerConnection connection)
        {
            lock (this.connections)
            {
                connection.Close();
                this.connections.Remove(connection.ID);
            }
        }

        private int GetAvaibleID()
        {
            for (int i = 0; ; i++)
            {
                if (!this.connections.ContainsKey(i))
                    return i;
            }
        }

        public bool IsConnected
        {
            get 
            {
                return this.connections.Count > 0;
            }
        }

        private int timeout;
        public int Timeout
        {
            get
            {
                return this.timeout;
            }
            set
            {
                this.timeout = value;
                lock (connections)
                {
                    foreach (var connection in this.connections.Values)
                    {
                        connection.SendTimeout = value;
                        connection.ReciveTimeout = value;
                    }
                }
            }
        }

        #region Keep Alive

        private void KeepAlive()
        {
            while (running)
            {
                lock (connections)
                {
                    List<IServerConnection> toRemove = new List<IServerConnection>();
                    foreach (var item in this.connections.Values)
                    {
                        try
                        {
                            //Sends a heartbeat
                            item.SendTCP(new byte[0]);
                        }
                        catch
                        {
                            //HB send failed. We assume that the connection is no longer valid.
                            toRemove.Add(item);
                        }
                    }
                    toRemove.ForEach((x) => this.RemoveConnection(x));
                }

                Thread.Sleep(HeatbeatIntevall);
            }

        }

        #endregion

        #region Events

        public event Action<int> Connected;
        public event Action<int> Dissconnected;
        public event Action<int, byte[]> Recived;

        internal void OnRecived(int connectionID, byte[] bytes)
        {
            if (this.Recived != null)
                this.Recived(connectionID, bytes);
        }

        internal void OnDissconnected(int connectionID)
        {
            this.RemoveConnection(this.connections[connectionID]);

            if (this.Dissconnected != null)
                this.Dissconnected(connectionID);
        }

        internal void OnConnected(int connectionID)
        {
            if (this.Connected != null)
                this.Connected(connectionID);
        }


        #endregion

        #region Send

        public void sendUdp(int connectionID, byte[] toSend)
        {
            lock (connections)
            {
                IServerConnection connection = this.connections[connectionID];
                connection.SendUDP(toSend);
            }
        }

        public void sendTcp(int connectionID, byte[] toSend)
        {
            lock (connections)
            {
                IServerConnection connection = this.connections[connectionID];
                connection.SendTCP(toSend);
            }
        }

        public void sendToAllUdp(byte[] toSend)
        {
            lock (connections)
            {
                foreach (var connection in this.connections.Values)
                {
                    connection.SendUDP(toSend);
                }
            }
        }

        public void sendToAllTcp(byte[] toSend)
        {
            lock (connections)
            {
                foreach (var connection in this.connections.Values)
                {
                    connection.SendTCP(toSend);
                }
            }
        }

        #endregion
    }
}
