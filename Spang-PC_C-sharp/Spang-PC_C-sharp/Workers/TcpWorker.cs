using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net.Sockets;

namespace Spang_PC_C_sharp
{
    class TcpWorker : ContinuousWorker
    {
        //The connection used.
        protected readonly IConnection connection;

        /// <summary>
        /// Invoked when a message arrives.
        /// </summary>
        public event Action<byte[]> Recive;

        /// <summary>
        /// Invoked when the tcp connection times out.
        /// <remarks>After this is invoked the calling thread exits.</remarks>
        /// </summary>
        public event Action TimedOut;

        /// <summary>
        /// Creates a new TcpWorker.
        /// </summary>
        /// <param name="connection">The connection used by the worker.</param>
        public TcpWorker(IConnection connection)
        {
            this.connection = connection;
        }
        
        protected override void DoWorkInternal()
        {
            try
            {
                //Recives and sends the tcp message.
                byte[] bytes = this.connection.ReciveTCP();

                if (this.Recive != null)
                    this.Recive(bytes);
            }
            catch (Exception ex)
            {
                Console.WriteLine("TCPRecive timed out.");
                Console.WriteLine(ex.Message);
                //When the connection times out it is unusable so 
                //the worker thread exits.
                this.StopWorking();

                if (this.TimedOut != null)
                    this.TimedOut();
            }
        }    
    }
}
