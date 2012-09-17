using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Spang_PC_C_sharp
{
    interface IConnection
    {
            

            /// <summary>
            /// Reconnects the user to a given port.
            /// </summary>
        void reconnect();

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
        }
}

