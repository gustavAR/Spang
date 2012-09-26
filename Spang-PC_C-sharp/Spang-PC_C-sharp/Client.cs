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
    interface IClient : IEndpoint
    {
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
        void Reconnect();

        /// <summary>
        /// Starts reciving messages.
        /// </summary>
        void Start();

        /// <summary>
        /// Stops reciving messages.
        /// </summary>
        void Stop();

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
        event Action Connected;

        /// <summary>
        /// Invoked when the IClient dissconnects.
        /// </summary>
        event Action Dissconnected;

        /// <summary>
        /// Invoked when the client recived a message.
        /// </summary>
        event Action<byte[]> Recived;
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
        private ClientTcpWorker tworker;

        #region Connect

        /// <summary>
        /// <see cref="IClient.Connect(IPEndPoint)"/>
        /// </summary>
        public void Connect(IPEndPoint endpoint)
        {
            //If we are connected we need to terminate the current connection
            //so that we can connect to a new one.
            if (connection != null && connection.Connected)
                connection.Close();

            this.connection = Network.ConnectTo(endpoint);

            this.OnConnected();
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
        public void Reconnect()
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
                //Connect to the last open connection.
                this.Connect(this.connection.RemoteEndPoint);
            }
        }

        #endregion

        #region Recive

        /// <summary>
        ///  <see cref="IClient.Start"/>
        /// </summary>
        public void Start()
        {
            if (uworker != null && tworker != null)
                return; //We are already receiving.

            //Creates new workers and threads using them.

            this.uworker = new UdpWorker(this.connection);
            uworker.Recive += this.OnRecived;

            this.tworker = new ClientTcpWorker(this.connection);
            tworker.Recive += this.OnRecived;
            tworker.TimedOut += this.OnDissconnected;

            new Thread(uworker.DoWork).Start();
            new Thread(tworker.DoWork).Start();
        }

        /// <summary>
        ///  <see cref="IClient.Stop"/>
        /// </summary>
        public void Stop()
        {
            if (this.tworker != null || this.uworker != null)
            {
                //Stops the threads reciveing messages.
                this.tworker.StopWorking();
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
        public event Action Connected;
        
        /// <summary>
        ///  <see cref="IClient.Dissconnected"/>
        /// </summary>
        public event Action Dissconnected;

        /// <summary>
        ///  <see cref="IClient.Recived"/>
        /// </summary>
        public event Action<byte[]> Recived;

        private void OnConnected()
        {
            //Invokes Connected.
            if (this.Connected != null)
                this.Connected();
        }

        private void OnRecived(byte[] recived)
        {
            //Invokes Recived.
            if (this.Recived != null)
                this.Recived(recived);
        }

        private void OnDissconnected()
        {
            //Closes the dissconnected connection.
            this.connection.Close();
            //Invokes Dissconnected.
            if (this.Dissconnected != null)
                this.Dissconnected();
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
        public int Timeout
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
            this.connection.SendUDP(toSend);
        }

        /// <summary>
        /// <see cref="IClient.SendTCP"/>
        /// </summary>
        public void SendTCP(byte[] toSend)
        {
            this.connection.SendTCP(toSend);
        }

        #endregion

    }
}
