using System;
using System.Collections.Generic;
using System.Linq;
using System.Windows.Forms;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Drawing;
using Spang_PC_C_sharp.Touch_Manager;

namespace Spang_PC_C_sharp
{
    static class Program
    {        
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

            TouchDecoder decoder = new TouchDecoder();

            TouchEventManager em = new TouchEventManager();
            em.Tap += () =>  Console.WriteLine("Just tapped"); 
            em.LongTap += () =>  Console.WriteLine("Just Long Tapped"); 
            em.Up += () => Console.WriteLine("Just Upped"); 
            em.Down += () =>  Console.WriteLine("Just Downed"); 
            em.Move += (x,y) => Console.WriteLine("Just moved: X: {0} , Y: {1}", x, y);

            em.Tap += () => controller.LeftClick();
            em.Up += () => controller.mouseUp();
            em.LongTap += () => controller.RightClick();
            em.Down += () => controller.mouseDown();
            em.Move += (x, y) => controller.MoveMouse(moveSpeed(x), moveSpeed(y));
            em.MulitiMove += (c, x, y) => controller.VerticalScroll(y * 10);


            TouchStateMachine stateMachine = new TouchStateMachine(em);


            int bytesRecived = 0;
            long totalbytesRecived = 0;
            System.Timers.Timer timer = new System.Timers.Timer(1000);
            timer.AutoReset = true;
            timer.Elapsed += (s,e) =>
            {

                Console.Title = "Total Recived: " + totalbytesRecived + " Current ByteRate: " + ((double)bytesRecived) / 1000;
                totalbytesRecived += bytesRecived;
                bytesRecived = 0;

            };
            timer.Start();

            server.Connected += (x, y) => Console.WriteLine("A connection was recived");                  
            server.Recived += (x, message) =>
            {
               // Console.WriteLine("Recived a message of size:{0}", message.Data.Length);

                UnPacker unPacker = new UnPacker(message.Data);
                bytesRecived += unPacker.remaining();
                while(unPacker.remaining() > 0) {
                    int id = unPacker.UnpackByte();
                    if (id == 0)
                    {
                        //  Console.WriteLine("Recived mouse event!");
                        TouchEvent e = decoder.DecodeTouch(unPacker);
                        stateMachine.Update(e);
                    }
                    else if(id == 1)
                    {
                        String s = unPacker.UnpackString();
                        controller.NetworkedText(s);
                    }
                }
            };
            server.Dissconnected += (x, y) => Console.WriteLine("Oh no we dced ;(");
        }

        private static int moveSpeed(int p)
        {
            return p * ((int)Math.Sqrt(Math.Abs(p))) / 2;
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
