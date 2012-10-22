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