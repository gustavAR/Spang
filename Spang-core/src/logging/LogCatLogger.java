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

public class LogCatLogger implements ILogger {

	public void logException(Exception exe) {
		Log.e("Exception", exe.getMessage(), exe);
	}

	public void logError(Error error) {
		Log.e("Error", error.getMessage(), error);
	}

	public void logInfo(String info) {
		Log.i("Info", info);
	}

	public void logAssert(String assertInfo) {
		Log.wtf("Assert", assertInfo);
	}

	public void logDebugg(String debuggInfo) {
		Log.d("Debug", debuggInfo);
	}
}
