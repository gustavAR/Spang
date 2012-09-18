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
        [STAThread]
        static void Main()
        {
          //  Application.EnableVisualStyles();
          //  Application.SetCompatibleTextRenderingDefault(false);
          //  Application.Run(new Form1());

            Server server = new Server();
            IConnection connection = server.ReciveConnection(1337);

            // connection.connect(ADDRESS, PORT);

            new Thread(() =>
            {
                while (true)
                {
                    connection.sendTCP(Encoding.UTF8.GetBytes(Console.ReadLine()));
                }
            }).Start();
            
            while(true) 
            {
                byte[] data = connection.reciveTCP();	
			    string message = Encoding.UTF8.GetString(data, 0, data.Length);
                Console.WriteLine(message);
                if (message == "move")
                {
                    Cursor.Position = new System.Drawing.Point(Cursor.Position.X + 10, Cursor.Position.Y - 10);
                }
		    }
        }
    }
}
