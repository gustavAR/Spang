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

/**
 * Simple logger class. That can be used to log important things..
 * @author Lukas
 *
 */
public class Logger {
	
	//The logger to do the logging.
	private static ILogger logger;
	
	/**
	 * Sets the logger that will be used to log.
	 */
	public static void setLogger(ILogger logger) {
		Logger.logger = logger;
	}
	
	/**
	 * Log an exception.
	 * @param exe exception to log.
	 */
	public static void logException(Exception exe) {
		if(logger != null)
			logger.logException(exe);
	}
	
	/**
	 * Log an error.
	 * @param error error to log.
	 */
	public static void logError(Error error) {
		if(logger != null)
			logger.logError(error);
	}
	
	/**
	 * Log debugg info.
	 * @param message info.
	 */
	public static void logDebug(String message) {
		if(logger != null)
			logger.logDebug(message);
	}
	
	/**
	 * Log info information.
	 * @param message info.
	 */
	public static void logInfo(String message){
		if(logger != null)
			logger.logInfo(message);
	}
	
	/**
	 * Log assert information.
	 * @param message info.
	 */
	public static void logAssert(String message) {
		if(logger != null)
			logger.logAssert(message);
	}
}