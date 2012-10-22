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
 * Simple logger class. That can be used to log exceptions and dubug information.
 * @author Lukas
 *
 */
public class Logger {
	
	private static ILogger logger;
	
	public static void setLogger(ILogger logger) {
		Logger.logger = logger;
	}
	
	public static void logException(Exception exe) {
		logger.logException(exe);
	}
	
	public static void logError(Error error) {
		logger.logError(error);
	}
	
	public static void logDebug(String message) {
		logger.logDebug(message);
	}
	
	public static void logInfo(String message){
		logger.logInfo(message);
	}
	
	public static void logAssert(String message) {
		logger.logAssert(message);
	}
}
