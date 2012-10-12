package utils;

public interface ILogger {
	
	void logException(Exception exe);
	
	void logError(Error error);
	
	void logInfo(String info);
	
	void logAssert(String assertInfo);
	
	void logDebugg(String debuggInfo);
}
