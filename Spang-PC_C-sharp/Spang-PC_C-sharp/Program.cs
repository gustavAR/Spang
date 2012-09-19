using System;
using System.Collections.Generic;
using System.Linq;
using System.Windows.Forms;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;

namespace Spang_PC_C_sharp
{
    static class Program
    {
        private static int PORT = 1337;
        private static String ADDRESS = "192.168.33.102";
        
        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        static void Main()
        {
            Server server = new Server();
            new Thread(() =>
            {
                Brodcast();
            }).Start();
            
            IConnection connection = server.ReciveConnection(1337);

            var messageHandler = MessageHandlerBuilder.Build();
            
            while(true) 
            {
                Thread.Sleep(1000);

                byte[] data = connection.reciveUDP();
                messageHandler.DecodeMessage(data);   
		    }
        }
        /// <summary>
        /// http://www.java2s.com/Tutorial/CSharp/0580__Network/BroadcastSocketandbroadcastIPaddress.htm Assumed public licence
        /// </summary>
        private static void Brodcast()
        {
            Socket sock = new Socket(AddressFamily.InterNetwork, SocketType.Dgram, ProtocolType.Udp);
            sock.SetSocketOption(SocketOptionLevel.Socket, SocketOptionName.Broadcast, 1);
            IPEndPoint iep = new IPEndPoint(IPAddress.Broadcast, 9999);

            string hostname = Dns.GetHostName();
            byte[] data = Encoding.ASCII.GetBytes(hostname);
            while (true)
            {
                sock.SendTo(data, iep);
                Thread.Sleep(1000);
            }
        }
    }
}
