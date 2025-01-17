package com.ochess.edict.print;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.lvrenyang.io.IOCallBack;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 观察者模式
 * 
 * @author Administrator
 * 
 */
public class WorkService extends Service {

	// Service和workThread通信用mHandlerø
	public static WorkThread workThread = null;
	public static IOCallBack cb = null;
    private static final List<Handler> targetsHandler = new ArrayList<>(5);

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
        Handler mHandler = new MHandler(this);
		workThread = new WorkThread(mHandler, cb);
		workThread.start();
		Log.v("WorkService", "onCreate");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v("WorkService", "onStartCommand");
		Message msg = Message.obtain();
		msg.what = Global.MSG_ALLTHREAD_READY;
		notifyHandlers(msg);
		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		workThread.disconnectBt();
		workThread.disconnectBle();
		workThread.disconnectNet();
		workThread.disconnectUsb();
		workThread.quit();
		workThread = null;
		Log.v("DrawerService", "onDestroy");
	}

	static class MHandler extends Handler {

		WeakReference<WorkService> mService;

		MHandler(WorkService service) {
			mService = new WeakReference<>(service);
		}

		@Override
		public void handleMessage(Message msg) {
			notifyHandlers(msg);
		}
	}

	/**
	 * 
	 * @param handler
	 */
	public static void addHandler(Handler handler) {
		if (!targetsHandler.contains(handler)) {
			targetsHandler.add(handler);
		}
	}

	/**
	 * 
	 * @param handler
	 */
	public static void delHandler(Handler handler) {
        targetsHandler.remove(handler);
	}

	/**
	 * 
	 * @param msg
	 */
	public static void notifyHandlers(Message msg) {
		for (int i = 0; i < targetsHandler.size(); i++) {
			Message message = Message.obtain(msg);
			targetsHandler.get(i).sendMessage(message);
		}
	}

	public static void clear() {
		if (targetsHandler != null) {
            targetsHandler.clear();
		}
	}

}
