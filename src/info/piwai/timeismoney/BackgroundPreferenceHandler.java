package info.piwai.timeismoney;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;

/**
 * Saves preferences serially, in a background thread. Saving order is
 * guaranteed.
 */
public class BackgroundPreferenceHandler {

	private static class BackgroundHandler extends Handler {

		private final SharedPreferences preferences;

		public BackgroundHandler(Looper backgroundThreadLooper, SharedPreferences preferences) {
			super(backgroundThreadLooper);
			this.preferences = preferences;
		}

		@Override
		public void handleMessage(Message msg) {
			String key = (String) msg.obj;
			int value = msg.arg1;
			preferences.edit() //
					.putInt(key, value) //
					.commit();
		}
	}

	private final Handler backgroundHandler;

	public BackgroundPreferenceHandler(SharedPreferences preferences) {
		HandlerThread backgroundThread = new HandlerThread("Background thread saving preferences", Process.THREAD_PRIORITY_MORE_FAVORABLE);
		backgroundThread.start();

		backgroundHandler = new BackgroundHandler(backgroundThread.getLooper(), preferences);
	}

	public void putIntAsync(String key, int value) {
		Message message = Message.obtain();
		message.obj = key;
		message.arg1 = value;
		backgroundHandler.sendMessage(message);
	}
}
