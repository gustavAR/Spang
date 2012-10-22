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

import android.util.Log;

/**
 * Logs to logcat. 
 * @author LukasFiddle
 *
 */
public class LogCatLogger implements ILogger {

	/**
	 * {@inheritDoc}
	 */
	public void logException(Exception exe) {
		Log.e("Exception", exe.getMessage(), exe);
	}

	/**
	 * {@inheritDoc}
	 */
	public void logError(Error error) {
		Log.e("Error", error.getMessage(), error);
	}

	/**
	 * {@inheritDoc}
	 */
	public void logInfo(String info) {
		Log.i("Info", info);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void logAssert(String assertInfo) {
		Log.wtf("Assert", assertInfo);
	}

	/**
	 * {@inheritDoc}
	 */
	public void logDebug(String debuggInfo) {
		Log.d("Debug", debuggInfo);
	}
}
