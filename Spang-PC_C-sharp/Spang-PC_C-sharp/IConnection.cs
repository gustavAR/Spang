﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Spang_PC_C_sharp
{
    interface IConnection
    {
            /*
             * Connects a user to the default address and port. 
             */
            void reconnect();

            /*
             * Connects the IConnection to the given address and port.
             * @param hostName the host to connect to.
             * @param port the port to connect to.
             * Note: This has to be called before data can be sent or recived across the network.
             */
            void connect(String hostName, int port);

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

