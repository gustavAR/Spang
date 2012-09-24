using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Spang_PC_C_sharp
{
    interface IConnection
    {
        /*
         * Sends the supplied data using the UDP-protocol. 
         * @param data the data to be sent.
         */
        void sendUDP(byte[] data);

        /*
         * Sends the supplied data using the TCP-protocol.
         * @param data
         */
        void sendTCP(byte[] data);

        /*
         * Receives an array of byte data from the UDP-protocol.
         * @return the array of byte that was sent over the network.
         */
        byte[] reciveUDP();

        /*
         * Receives an array of byte data from the TCP-protocol.
         * @return the array of byte that was sent over the network.
         */
        byte[] reciveTCP();

        /// <summary>
        /// Gets or sets the time TCP send/recive will block.
        /// </summary>
        int Timeout { get; set; }

        /// <summary>
        /// Closes the connection.
        /// </summary>
        void Close();
    }
}

