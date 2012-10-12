package utils;

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
