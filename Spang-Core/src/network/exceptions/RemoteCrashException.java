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
package network.exceptions;


@SuppressWarnings("serial")
public class RemoteCrashException extends NetworkException {

	public RemoteCrashException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public RemoteCrashException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public RemoteCrashException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public RemoteCrashException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
