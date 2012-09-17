﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.Net.Sockets;
using System.IO;

namespace Spang_PC_C_sharp
{	
    class Connection : IConnection 
    {
        //The lowest number a port can take without using reserved port numbers.
	    private static int LOW_PORT = 1024;
	    //The largest port number a port can take.
	    private static int HIGH_PORT = 65536;
   
        private UdpClient udpSocket;
        private TcpClient tcpSocket;
        private IPEndPoint udpAddress;
        private IPEndPoint remoteEndPoint;

        public Connection(TcpClient tpcClient, UdpClient udpClient, int port)
        {
            this.tcpSocket = tpcClient;
            this.udpSocket = udpClient;
            this.udpAddress = new IPEndPoint(IPAddress.Any, port);
            this.remoteEndPoint = (IPEndPoint)tcpSocket.Client.RemoteEndPoint;
            udpClient.Connect(this.remoteEndPoint);
        }

        public void sendUDP(byte[] data)
        {
            udpSocket.Send(data, data.Length);
        }
        
        public byte[] reciveUDP()
        {
            return udpSocket.Receive(ref udpAddress);
        }

        public void reconnect()
        {
            this.udpSocket.Connect(remoteEndPoint);
            this.tcpSocket.Connect(remoteEndPoint);
        }

        public void sendTCP(byte[] data)
        {
            Stream stream = tcpSocket.GetStream();
            stream.Write(data, 0, data.Length);
        }

        public byte[] reciveTCP()
        {
            List<byte> bytes = new List<byte>();
            Stream stream = tcpSocket.GetStream();
            int b;
            while ((b = stream.ReadByte()) != -1)
            {
                bytes.Add((byte)b);
            }

            return bytes.ToArray();
        } 
    }
}
