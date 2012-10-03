using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.Threading;
using System.Net.Sockets;
using System.IO;

namespace Spang_PC_C_sharp
{

    /// <summary>
    /// A client is a class that can connect over a network
    /// and recives messages from that connection.
    /// </summary>
    interface IClient
    {
        /// <summary>
        /// Gets the connection status of the client.
        /// </summary>
        bool IsConnected { get; }

        /// <summary>
        /// Gets or Sets the time a connection will wait for a 
        /// message without dissconnecting (milliseconds).
        /// <remarks>
        /// If timeout is 0 it never disconnection.
        /// However then it cannot detect connection faliures.
        /// 5-15 sec is a good Timeout range.
        /// </remarks>
        /// </summary>
        int ConnectionTimeout { get; set; }

        /// <summary>
        /// Connects to the endpoint.
        /// </summary>
        /// <param name="endpoint">The remote endpoint to connect to.</param>
        void Connect(IPEndPoint endpoint);
        
        /// <summary>
        /// Connects to the specified port and address.
        /// </summary>
        /// <param name="port">The port to connect to.</param>
        /// <param name="address">The address to connect to.</param>
        void Connect(int port, string address);

        /// <summary>
        /// Reconnects to the last endpoint used.
        /// </summary>
        /// <param name="retries">number of retries</param>
        void Reconnect(int retries);

        /// <summary>
        /// Disconnects the connection
        /// </summary>
        void Disconnect();

        /// <summary>
        /// Sends a message using the UDP-protocol.
        /// </summary>
        /// <remarks>The client must be connected to Send messages</remarks>
        /// <exception cref="ArgumentException">Thrown if the client is not connected.</exception>
        /// <param name="toSend"></param>
        void SendUDP(byte[] toSend);

        /// <summary>
        /// Sends a message using the TCP-protocol.
        /// </summary>
        /// <remarks>The client must be connected to Send messages.</remarks>
        /// <exception cref="ArgumentException">Thrown if the client is not connected.</exception>
        /// <param name="toSend"></param>
        void SendTCP(byte[] toSend);
        
        /// <summary>
        /// Invoked when the IClient is connected.
        /// </summary>
        event Action<IClient, bool> Connected;

        /// <summary>
        /// Invoked when the IClient dissconnects.
        /// </summary>
        event Action<IClient, DisconnectCause> Dissconnected;

        /// <summary>
        /// Invoked when the client recived a message.
        /// </summary>
        event Action<IClient, byte[]> Recived;
    }

    enum DisconnectCause
    {
        Graceful,
        Unexpected
    }

    /// <summary>
    /// Multithreaded implementation of IClient.
    /// </summary>
    class Client : IClient
    {
        //The connection to send/recive messages from.
        private IConnection connection;
        //Worker that listens to udp messages.
        private UdpWorker uworker;
        //Worker that listens to tcp messages.
        private TcpWorker tworker;

        #region Connect
        /// <summary>
        /// <see cref="IClient.Connect(IPEndPoint)"/>
        /// </summary>
        private void Connect(IPEndPoint endpoint, bool reconnected)
        {
            if (connection != null)
                throw new ArgumentException("We are already connected. Can't connect while connected");
            //TODO implement IConnector.
            //this.connection = Connection.ConnectTo(endpoint);

            this.OnConnected(reconnected);
        }

        /// <summary>
        /// <see cref="IClient.Connect(IPEndPoint)"/>
        /// </summary>
        public void Connect(IPEndPoint endpoint)
        {
            this.Connect(endpoint, false);
        }

        /// <summary>
        ///  <see cref="IClient.Connect(int,string)"/>
        /// </summary>
        public void Connect(int port, string host)
        {
            this.Connect(new IPEndPoint(IPAddress.Parse(host), port));
        }

        /// <summary>
        ///  <see cref="IClient.Reconnect"/>
        /// </summary>
        public void Reconnect(int retries)
        {
            if (this.connection == null)
            {
                //If we have never been connected to anything we cannot reconnect.
                throw new ArgumentException("Never been connected to anything so cannot reconnect");
            }
            else if (this.connection.Connected)
            {
                //We are already connected so no need to reconnect.
                return;
            }
            else
            {
                ReconnectInternal(retries);
            }
        }

        private void ReconnectInternal(int retries)
        {
            for (int i = 0; i < retries; i++)
            {
                try
                {
                    Console.WriteLine("Trying to recconntect to " + this.connection.RemoteEndPoint);
                    //Connect to the last open connection.
                    this.Connect(this.connection.RemoteEndPoint, true);
                    return;
                }
                catch (Exception)
                {
                    Console.WriteLine("Failed to reconnect {0} retrying...",i);
                }
            }

            throw new Exception("Could not reconnect");
        }

        public void Disconnect()
        {
            this.OnDissconnected(DisconnectCause.Graceful);
        }

        #endregion

        #region Recive

        /// <summary>
        ///  <see cref="IClient.Start"/>
        /// </summary>
        private void Start()
        {
            if (uworker != null && tworker != null)
                return; //We are already receiving.

            //Creates new workers and threads using them.

            this.uworker = new UdpWorker(this.connection);
            uworker.Recive += this.OnRecived;

            this.tworker = new TcpWorker(this.connection);
            tworker.Recive += this.OnRecived;
            tworker.TimedOut += this.OnTimeout;

            new Thread(uworker.DoWork).Start();
            new Thread(tworker.DoWork).Start();
        }

        /// <summary>
        ///  <see cref="IClient.Stop"/>
        /// </summary>
        private void Stop()
        {
            if (this.tworker != null || this.uworker != null)
            {
                
                //Stops the threads reciveing messages.
                this.tworker.Recive -= this.OnRecived;
                this.tworker.TimedOut -= this.OnTimeout;
                this.tworker.StopWorking();
                this.uworker.Recive -= this.OnRecived;
                this.uworker.StopWorking();
                this.tworker = null;
                this.uworker = null;
            }
        }
        
        #endregion

        #region Events

        /// <summary>
        ///  <see cref="IClient.Connected"/>
        /// </summary>
        public event Action<IClient, bool> Connected;
        
        /// <summary>
        ///  <see cref="IClient.Dissconnected"/>
        /// </summary>
        public event Action<IClient, DisconnectCause> Dissconnected;

        /// <summary>
        ///  <see cref="IClient.Recived"/>
        /// </summary>
        public event Action<IClient, byte[]> Recived;

        private void OnConnected(bool reconnected)
        {
            //Invokes Connected.
            if (this.Connected != null)
                this.Connected(this, reconnected);
            this.Start();
        }

        private void OnRecived(byte[] recived)
        {
            //Invokes Recived.
            if (IsHeartbeat(recived))
            {
                return;
            }
            if (IsSystemMessage(recived))
            {
                HandleSystemMessage(recived);
                return;
            }

            if (this.Recived != null)
                this.Recived(this, recived);
        }

        private bool IsHeartbeat(byte[] recived)
        {
            return recived.Length == 0;
        }

        private void HandleSystemMessage(byte[] recived)
        {
            Console.WriteLine("Just recieved a system message of length " + (recived.Length - 1));
        }

        private bool IsSystemMessage(byte[] recived)
        {
            return recived[0] == 0;
        }

        private void OnDissconnected(DisconnectCause cause)
        {
            this.Stop();
            //Closes the dissconnected connection.
            this.connection.Close();
            //Invokes Dissconnected.
            if (this.Dissconnected != null)
                this.Dissconnected(this, cause);
        }

        private void OnTimeout()
        {
            this.OnDissconnected(DisconnectCause.Unexpected);
        }

        #endregion

        #region Fields and Properties

        /// <summary>
        /// <see cref="IEndpoint.IsConnected"/>
        /// </summary>
        public bool IsConnected
        {
            get 
            {
                return this.connection != null &&
                       this.connection.Connected;
            }
        }

        private int timeout;
        /// <summary>
        /// <see cref="IEndpoint.Timeout"/>
        /// </summary>
        public int ConnectionTimeout
        {
            get
            {
                return this.timeout;
            }
            set
            {
                this.timeout = value;
                if (this.connection != null)
                    this.connection.ReciveTimeout = value;
            }
        }

        #endregion

        #region Send

        /// <summary>
        /// <see cref="IClient.SendUDP"/>
        /// </summary>
        public void SendUDP(byte[] toSend)
        {
            this.connection.Send(toSend);
        }

        /// <summary>
        /// <see cref="IClient.SendTCP"/>
        /// </summary>
        public void SendTCP(byte[] toSend)
        {
            this.connection.Send(toSend, Protocol.Reliable);
        }

        #endregion

    }
}
