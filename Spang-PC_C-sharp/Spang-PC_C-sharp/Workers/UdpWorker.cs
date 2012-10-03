using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net.Sockets;

namespace Spang_PC_C_sharp
{
    /// <summary>
    /// A worker class that listens to UDP messages.
    /// </summary>
    class UdpWorker : ContinuousWorker
    {
        //The connection used.
        private readonly IConnection connection;

        /// <summary>
        /// Invoked when a new message arrives.
        /// </summary>
        public event Action<byte[]> Recive;

        /// <summary>
        /// Invoked when a recived message times out.
        /// </summary>
        public event Action Timeout;

        /// <summary>
        /// Creates a UdpWorker.
        /// </summary>
        /// <param name="connection">The connection used.</param>
        public UdpWorker(IConnection connection)
        {
            this.connection = connection;
        }

        protected override void DoWorkInternal()
        {
            try
            {
                //Recives and sends the udp message.
                byte[] bytes = connection.Receive();

                if (this.Recive != null)
                    this.Recive(bytes);
            }
            catch (SocketException)
            {
                if (this.Timeout != null)
                    this.Timeout();

                this.StopWorking();
            }
            catch (Exception exe)
            {
                //Something went wrong while we a recived the message.
                //Since the connection is unusable we exit the thread.
                Console.WriteLine("UDP read failed");
                Console.WriteLine(exe);
                this.StopWorking();
            }
        }
    }
}
