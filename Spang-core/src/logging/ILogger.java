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
package logging;

public interface ILogger {
	
	/**
	 * Logs an exception. 
	 * @param exe the exception to log.
	 */
	void logException(Exception exe);
	
	/**
	 * Logs an error.
	 * @param error the error to log.
	 */
	void logError(Error error);
	
	/**
	 * Logs information.
	 * @param info information to log.
	 */
	void logInfo(String info);
	
	/**
	 * Logs assert information.
	 * @param assertInfo the information to log.
	 */
	void logAssert(String assertInfo);
	
	/**
	 * Logs debug information. 
	 * @param debuggInfo logs debug information.
	 */
	void logDebug(String debuggInfo);
}
