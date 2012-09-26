using System;
using System.Collections.Generic;
using System.Linq;
using System.Windows.Forms;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Drawing;

namespace Spang_PC_C_sharp
{
    static class Program
    {
        private static int PORT = 1337;
        
        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        static void Main()
        {

            IMessageDecoder messageHandler = new MessageDecoder();
            Phone phone = new Phone(messageHandler);
            DesktopController controller = new DesktopController(phone, new OsInterface());

            IServer server = new Server();
            server.ConnectionTimeout = 5000;

            server.Start(1337);

            server.Connected += (x, y) => Console.WriteLine("A connection was recived");
            server.Recived += (x, message) =>
            {
                Console.WriteLine("Recived message from {0} of length {1}", message.ID, message.Data.Length);
                messageHandler.DecodeMessage(message.Data);
            };
            server.Dissconnected += (x, y) => Console.WriteLine("Oh no we dced ;(");

/*
            Thread.Sleep(2000);

            IClient client0 = OpenClient(0);
            IClient client1 = OpenClient(1);

           // client1.SendUDP(new byte[] { 123, 10, 231 });

            while (true)
            {
               client0.SendUDP(new byte[] { 0, 2, 21, 1 });
               client1.SendUDP(new byte[] { 0, 1, 2, 3, 4 });
                Thread.Sleep(100);
            } */

            
            

        }

        private static IClient OpenClient(int id)
        {
            IClient client = new Client();
            client.ConnectionTimeout = 1;

            client.Connected += (x, y) => Console.WriteLine("Client connected! " + id);
            client.Dissconnected += (x, y) => Console.WriteLine("Client Dced" + id);
            client.Recived += (x, y) =>
            {
                if (y[0] == 0)
                    Console.WriteLine("Got a heartbeat " + id);
                else
                    Console.WriteLine(":O jsut got udp message");
            };

            client.Connect(1337, "192.168.0.12");

            return client;
        }
    }
}
