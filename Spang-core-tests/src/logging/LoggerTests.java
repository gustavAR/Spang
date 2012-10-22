package logging;

import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

public class LoggerTests {
	ILogger logger;
	
	@Before
	public void setup() {
		logger = mock(ILogger.class);
		Logger.setLogger(logger);
	}
	
	@Test
	public void testIfCanLogInfo() {
		Logger.logInfo("Hello");
		verify(logger).logInfo("Hello");
	}
	
	@Test
	public void testIfCanLogError() {
		Error error = new Error();
		Logger.logError(error);
		verify(logger).logError(error);
	}
	
	@Test
	public void testIfCanLogException() {
		Exception exception = new Exception();
		Logger.logException(exception);
		verify(logger).logException(exception);
	}
	
	@Test
	public void testIfCanLogAssert() {
		Logger.logAssert("Hello");
		verify(logger).logAssert("Hello");
	}
	
	@Test
	public void testIfCanLogDebugg() {
		Logger.logDebug("Hello");
		verify(logger).logDebug("Hello");
	}
	
	@Test
	public void testIfCanChangeLogger() {
		Logger.setLogger(new LogCatLogger());
	}
}