using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.Net.Sockets;
using System.IO;

namespace Spang_PC_C_sharp
{	
    /// <summary>
    /// Syncronized implementation of IConnection.
    /// </summary>
    sealed class Connection : IConnection 
    {
        //The default timeout.
        private const int DEFAULT_TIMEOUT = 5000;

        //Sockets used.
        private readonly UdpClient udpSocket;
        private readonly TcpClient tcpSocket;

        //Helper variables for udp sending and receiving.
        private IPEndPoint udpAddress;

        /// <summary>
        /// Creates a connection.
        /// </summary>
        /// <param name="tcpClient">The TcpClient used.</param>
        /// <param name="udpClient">The UdpClient used.</param>
        internal Connection(TcpClient tcpClient, UdpClient udpClient)
        {
            //This connection assumes that the udp and tcp messges arrives on the same port.
            if (((IPEndPoint)tcpClient.Client.LocalEndPoint).Port !=
               ((IPEndPoint)udpClient.Client.LocalEndPoint).Port)
                throw new ArgumentException("The tcpClient and udpClient must be bound on the same port!");

            this.tcpSocket = tcpClient;
            this.udpSocket = udpClient;
            this.Timeout = DEFAULT_TIMEOUT;

            this.udpAddress = new IPEndPoint(IPAddress.Any, this.LocalEndPoint.Port);
        }

        /// <summary>
        /// <see cref="IConnection.SendUDP(byte[])"/>
        /// </summary>
        public void SendUDP(byte[] data)
        {
            udpSocket.Send(data, data.Length);
        }


        /// <summary>
        /// <see cref="IConnection.ReciveUDP()"/>
        /// </summary>
        public byte[] ReciveUDP()
        {                 
            return udpSocket.Receive(ref udpAddress);
        }
        
        /// <summary>
        /// <see cref="IConnection.SendTCP"/>
        /// </summary>
        public void SendTCP(byte[] data)
        {
            Stream stream = tcpSocket.GetStream();

            //Tcp messages are prefixed with the length.
            //Writes the length befoure sending the message.
            stream.WriteByte((byte)((data.Length >> 8) & 0xFF));
            stream.WriteByte((byte)(data.Length & 0xFF));
            
            stream.Write(data, 0, data.Length);
            stream.Flush();
        }

        /// <summary>
        /// <see cref="IConnection.ReciveTCP"/>
        /// </summary>
        public byte[] ReciveTCP()
        {
            Stream stream = tcpSocket.GetStream();
            int length = this.ReadLength(stream);

            byte[] data = new byte[length];
            stream.Read(data, 0, length);

            return data;
        }

        private int ReadLength(Stream stream)
        {

            int b1 = stream.ReadByte();
            int b2 = stream.ReadByte();
            //If the stream cant be read from -1 is read.
            //when this happens we can no longer read data from the socket
            if (b1 == -1 || b2 == -1)
                throw new IOException("The socket cannot be read from");
            
            return (b1 << 8) | b2;
        }

        /// <summary>
        /// <see cref="IConnection.Timeout"/>
        /// </summary>
        public int Timeout
        {
            get
            {
                return this.tcpSocket.ReceiveTimeout;
            }
            set
            {
                this.tcpSocket.ReceiveTimeout = value;
                this.tcpSocket.SendTimeout = value;
            }
        }

        /// <summary>
        /// <see cref="IConnection.Close"/>
        /// </summary>
        public void Close()
        {
            this.udpSocket.Close();
            this.tcpSocket.Close();
        }

        /// <summary>
        /// <see cref="IConnection.Connected"/>
        /// </summary>
        public bool Connected
        {
            get 
            {
                return this.tcpSocket.Connected;
            }
        }

        /// <summary>
        /// <see cref="IConnection.RemoteEndPoint"/>
        /// </summary>
        public IPEndPoint RemoteEndPoint
        {
            get { return (IPEndPoint)this.tcpSocket.Client.RemoteEndPoint; }
        }

        /// <summary>
        /// <see cref="IConnection.LocalEndPoint"/>
        /// </summary>
        public IPEndPoint LocalEndPoint
        {
            get { return (IPEndPoint)this.tcpSocket.Client.LocalEndPoint; }
        }

    }
}
