﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net.Sockets;
using System.Net;
using System.Threading;
using System.Collections.Concurrent;
using Spang.Core.Serialization;
using Spang.Core.Utils;

namespace Spang.Core.Network
{
    /// <summary>
    /// A server is a type of endpoint that can have multiple connections.
    /// </summary>
    public interface IServer
    {
        /// <summary>
        /// Gets the connection status of the IEndpoint.
        /// </summary>
        bool IsConnected { get; }

        /// <summary>
        /// Gets or Sets the time a connection will wait for a 
        /// message without dissconnecting (milliseconds).
        /// <remarks>
        /// If timeout is 0 it never disconnects.
        /// However then it cannot detect connection faliures.
        /// 5-15 sec is a good Timeout range.
        /// </remarks>
        /// </summary>
        int ConnectionTimeout { get; set; }

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
        event Action<IServer, ConnectionEventArgs> Connected;
        
        /// <summary>
        /// Invoked when a message is recived on 
        /// the supplied connectionID.
        /// </summary>
        event Action<IServer, RecivedEventArgs> Recived;

        /// <summary>
        /// Invoked when a connection dissconnects.
        /// </summary>
        event Action<IServer, DisconnectionEventArgs> Dissconnected;
        
        /// <summary>
        /// Sends a message to a client using the UDP-protocol.
        /// </summary>
        /// <param name="connectionID">ID of the client to send to.</param>
        /// <param name="toSend">The message to send.</param>
        void Send(int connectionID, object toSend);

        /// <summary>
        /// Sends a message to a client using the TCP-protocol.
        /// </summary>
        /// <param name="connectionID">ID of the client to send to.</param>
        /// <param name="toSend">The message to send.</param>
        void Send(int connectionID, object toSend, Protocol protocol);
        
        /// <summary>
        /// Sends a message to all connected clients. Using UDP-Protocol.
        /// </summary>
        /// <param name="toSend">The message to send.</param>
        void SendToAll(object toSend);

        /// <summary>
        /// Sends a message to all connected clients. Using TCP-Protocol.
        /// </summary>
        /// <param name="toSend">The message to send.</param>
        void SendToAll(object toSend, Protocol protocol);

        /// <summary>
        /// Sends a message to all connected client exept the client with theid provided.
        /// </summary>
        void SendToAllExcept(int id, object toSend, Protocol protocol);
    }

    public class Server : IServer
    {
        #region Connection Reciver class
        
        /// <summary>
        /// Worker class that manages the reciving of new connections.
        /// </summary>
        private class ReciverWorker : ContinuousWorker
        {
            public readonly int Port;
            private readonly Server server;
            private readonly ConnectionListener listener;

            public ReciverWorker(int port, Server server)
            {
                this.Port = port;
                this.server = server;
                this.listener = new ConnectionListener();
            }
            
            protected override void DoWorkInternal()
            {
                //Recives a connection and adds it to the server.
                var connection = listener.ReciveConnection(Port);
                int id = this.server.GetAvaibleID();
                var sConnection =  new ServerConnection(connection, this.server, id);

                sConnection.ReciveTimeout = this.server.timeout;
                sConnection.SendTimeout = this.server.timeout;
                this.server.OnConnected(sConnection);
            }
        }

        /// <summary>
        /// Worker that keeps active connections alive.
        /// </summary>
        private class KeepAliveWorker : ContinuousWorker
        {
            private readonly Server server;

            public KeepAliveWorker(Server server)
            {
                this.server = server;
            }

            protected override void DoWorkInternal()
            {
                foreach (var item in this.server.connections.Values)
                {
                    try
                    {
                        //Sends a heartbeat
                        item.Send(new byte[0], Protocol.Unordered);
                    }
                    catch
                    {
                        //If the sends fails the connection is no longer valid and will be disconnected shortly
                    }
                }

                Thread.Sleep(this.server.HeartbeatInterval);
            }
        }

        #endregion

        private volatile int heartbeatInterval;
        public int HeartbeatInterval
        {
            get { return this.heartbeatInterval; }
            set { this.heartbeatInterval = value; }
        }
        private ReciverWorker reciverWorker;
        private KeepAliveWorker keepAliveWorker;
        private readonly IDictionary<int, IServerConnection> connections;
        private readonly SerializeManager serializeManager;

        public Server(SerializeManager serializeManager)
        {
            this.connections = new ConcurrentDictionary<int, IServerConnection>();
            this.serializeManager = serializeManager;
            this.heartbeatInterval = 1000;
        }

        public void Start(int port)
        {
            if (reciverWorker != null)
                throw new ArgumentException(string.Format("Already listening to {0}", reciverWorker.Port));

            reciverWorker = new ReciverWorker(port, this);
            new Thread(reciverWorker.DoWork).Start();

            keepAliveWorker = new KeepAliveWorker(this);
            new Thread(keepAliveWorker.DoWork).Start();
        }

        public void Stop()
        {
            if (reciverWorker != null)
            {
                reciverWorker.StopWorking();
                reciverWorker = null;
            }

            if (keepAliveWorker != null)
            {
                keepAliveWorker.StopWorking();
                keepAliveWorker = null;
            }

            TerminateAllConnections();
        }

        private void TerminateAllConnections()
        {
            var tmp = new List<IServerConnection>();
            foreach (var item in this.connections.Values)
            {
                tmp.Add(item);
            }
            tmp.ForEach((x) => this.OnDissconnected(x, DisconnectCause.Graceful));
        }

        internal void AddConnection(IServerConnection connection)
        {
            this.connections.Add(connection.ID, connection);
            connection.StartAsyncRecive();  
        }
               
        internal void RemoveConnection(IServerConnection connection)
        {
            connection.StopAsyncRecive();
            connection.Close();
            this.connections.Remove(connection.ID);
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
        public int ConnectionTimeout
        {
            get
            {
                return this.timeout;
            }
            set
            {
                this.timeout = value;
                foreach (var connection in this.connections.Values)
                {
                    connection.SendTimeout = value;
                    connection.ReciveTimeout = value;
                }
            }
        }

        #region Events

        public event Action<IServer, ConnectionEventArgs> Connected;
        public event Action<IServer, DisconnectionEventArgs> Dissconnected;
        public event Action<IServer, RecivedEventArgs> Recived;

        internal void OnRecived(int connectionID, byte[] bytes)
        {
            if (IsHeartbeat(bytes))
            {
                ConsoleColor c = Console.ForegroundColor;
                Console.ForegroundColor = ConsoleColor.DarkRed;
                Console.WriteLine("Server got a heartbeat from {0}", connectionID);
                Console.ForegroundColor = c;
                return;
            }

            UnPacker unPacker = new UnPacker(bytes);
            while (unPacker.remaining() > 0)
            {
                RecivedEventArgs eventArgs = new RecivedEventArgs(connectionID, this.DeserializeMessage(unPacker));
                if (this.Recived != null)
                    this.Recived(this, eventArgs);
            }
        }

        private object DeserializeMessage(UnPacker unpacker)
        {
            return this.serializeManager.Deserialize(unpacker);
        }

        private bool IsHeartbeat(byte[] recived)
        {
            return recived.Length == 0;
        }

        internal void OnDissconnected(IServerConnection connection, DisconnectCause cause)
        {
            DisconnectionEventArgs eventArgs = new DisconnectionEventArgs(connection.ID, cause);

            this.RemoveConnection(connection);

            if (this.Dissconnected != null)
                this.Dissconnected(this, eventArgs);
        }

        internal void OnConnected(IServerConnection connection)
        {
            ConnectionEventArgs eventArgs = new ConnectionEventArgs(connection.ID);
            this.AddConnection(connection);
            if (this.Connected != null)
                this.Connected(this, eventArgs);
        }


        #endregion

        #region Send

        public void Send(int connectionID, object toSend)
        {
            byte[] message = this.Serialize(toSend);
            IServerConnection connection = this.connections[connectionID];
            connection.Send(this.Serialize(toSend));
        }

        private byte[] Serialize(object toSend)
        {
            Packer packer = new Packer();
            this.serializeManager.Serialize(packer, toSend);
            return packer.GetPackedData();
        }

        public void Send(int connectionID, object toSend, Protocol protocol)
        {

            IServerConnection connection = this.connections[connectionID];
            connection.Send(this.Serialize(toSend), protocol);
        }

        public void SendToAll(object toSend, Protocol protocol)
        {
            byte[] message = this.Serialize(toSend);
            foreach (var connection in this.connections.Values)
            {
                connection.Send(message, protocol);
            }
        }


        public void SendToAllExcept(int id, object toSend, Protocol protocol)
        {
            byte[] message = this.Serialize(toSend);
            foreach (var connection in this.connections.Values)
            {
                if (connection.ID != id)
                    connection.Send(message);
            }
        }



        public void SendToAll(object toSend)
        {
            this.SendToAll(toSend, Protocol.Unordered);
        }

        #endregion


    }
}
