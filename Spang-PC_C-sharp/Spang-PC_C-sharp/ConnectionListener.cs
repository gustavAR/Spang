using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net.Sockets;
using System.Net;
using System.IO;

namespace Spang_PC_C_sharp
{
    /// <summary>
    /// A class that can listen for incomming connections on a specified port.
    /// </summary>
    class ConnectionListener
    {
        //The listener to use for low level listening.
        private TcpListener listener;

        /// <summary>
        /// Starts listening for connections.
        /// </summary>
        /// <param name="port">The port to listen on.</param>
        public void Start(int port)
        {
            listener = new TcpListener(IPAddress.Any, port);
            listener.Start();
        }
        
        /// <summary>
        /// Accepts a connection.
        /// <remarks>This method blocks untill a Connection is recived.</remarks>
        /// </summary>
        /// <returns></returns>
        public IConnection AcceptConnection()
        {
            //Connects the tcpClient.
            TcpClient tcpClient = this.listener.AcceptTcpClient();
                        
            //Creates an udpClient on a random port. And connects it to the remote point.
            UdpClient udpClient = new UdpClient(new IPEndPoint(IPAddress.Any, 0));
            udpClient.Connect((IPEndPoint)tcpClient.Client.RemoteEndPoint);

            //Writes the local udp port to the connection.
            //This is done so that the other end can configure it's Udp socket.
            BinaryWriter writer = new BinaryWriter(tcpClient.GetStream());
            writer.Write(((IPEndPoint)udpClient.Client.LocalEndPoint).Port);
            writer.Flush();

            return new Connection(tcpClient, udpClient);            
        }

        /// <summary>
        /// Checks if any connections are pending.
        /// </summary>
        /// <returns>The result.</returns>
        public bool Pending()
        {
            if (this.listener != null)
                return this.listener.Pending();

            return false;

        }

        /// <summary>
        /// Stops listening for incomming connections.
        /// </summary>
        public void Stop()
        {
            listener.Stop();
        }
    }
}
