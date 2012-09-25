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
            /*Server server = new Server();
            server.RecivedConnection += (connection) =>
                {
                    new Thread(() =>
                    {
                        Console.WriteLine("Recived connection!");
                        phone.AccelerometerChanged += (o, n) => Console.WriteLine(n.ToString());

                        while (true)
                        {
                            byte[] data = connection.reciveUDP();
                            phone.ProcessMessage(data);
                        }

                    }).Start();
                };

            server.Start(PORT);*/



        /*  bool reconnectNeeded = false;

            CEndPoint endpoint = new CEndPoint();
            endpoint.Connected += () => Console.WriteLine("Just Conencted!");
            endpoint.Dissconnected += () => 
            {
                Console.WriteLine("Just dissconnected. Reconecting...");
                reconnectNeeded = true;
            };
            endpoint.Recived += (message) =>
            {
                messageHandler.DecodeMessage(message);
            };

            connect(endpoint);
            while (true)
            {
                Thread.Sleep(10);
                if (reconnectNeeded)
                {
                    reconnectNeeded = false;
                    connect(endpoint);
                }
            }*/

            IMessageDecoder messageHandler = new MessageDecoder();
            Phone phone = new Phone(messageHandler);
            DesktopController controller = new DesktopController(phone, new OsInterface());

            IServer server = new Server();

            server.Start(1337);
            server.Timeout = 2000000;

            server.Connected += (x) => Console.WriteLine("A connection was recived");
            server.Recived += (x, message) =>
            {
                Console.WriteLine("Recived message from Connection {0} ", x);
                messageHandler.DecodeMessage(message);
            };

            OpenClient(0);
          //  OpenClient(1);

        }

        private static void OpenClient(int id)
        {
            IClient client = new Client();
            client.Timeout = 5000;

            client.Connected += () => Console.WriteLine("Client connected! " + id);
            client.Dissconnected += () => Console.WriteLine("Client Dced" + id);
            client.Recived += (x) =>
            {
                if (x[0] == 0)
                    Console.WriteLine("Got a heartbeat " + id);
            };

            client.Connect(1337, "192.168.0.12");
            client.Start();
        }
    }
}
