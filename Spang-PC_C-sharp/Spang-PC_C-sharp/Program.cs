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
            Server server = new Server();
            server.RecivedConnection += (connection) =>
                {
                    new Thread(() =>
                    {
                        Console.WriteLine("Recived connection!");

                        IMessageDecoder messageHandler = new MessageDecoder();
                        Phone phone = new Phone(messageHandler);
                        DesktopController controller = new DesktopController(phone, new OsInterface());

                        phone.AccelerometerChanged += (o, n) => Console.WriteLine(n.ToString());

                        while (true)
                        {
                            byte[] data = connection.reciveUDP();
                            phone.ProcessMessage(data);
                        }

                    }).Start();
                };

            server.Start(PORT);
        }
    }
}
