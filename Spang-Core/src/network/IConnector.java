/*  Copyright 2012 Joakim Johansson, Lukas Kurtyan, Gustav Alm Rosenblad and Pontus Pall
 *  
 *  This file is part of Spang.

    Spang is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Spang is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Spang.  If not, see <http://www.gnu.org/licenses/>.
 */
package network;
import java.net.InetSocketAddress;

/**
 * Interface used to connect IConnections.
 * @author LukasFiddle
 *
 */
public interface IConnector {
	
	/**
	 * Connects to a remote endpoint.
	 * @param address the remote endpoint to connect to.
	 * @param timeout the time the connector will try to connect.
	 * @return a new IConnection object that connects the two remote points.
	 */
	IConnection connect(InetSocketAddress address, int timeout);
}
