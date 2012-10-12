package utils;

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
		logger.logDebugg(message);
	}
	
	public static void logInfo(String message){
		logger.logInfo(message);
	}
	
	public static void logAssert(String message) {
		logger.logAssert(message);
	}
}
