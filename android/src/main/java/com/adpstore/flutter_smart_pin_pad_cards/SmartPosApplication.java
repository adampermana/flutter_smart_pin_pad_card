package com.adpstore.flutter_smart_pin_pad_cards;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class SmartPosApplication extends Application {
	private static Context context;

	@Override
	public void onCreate() {
		super.onCreate();
		context = this;
		// Bind device service
		boolean bindResult = DeviceServiceManager.getInstance().bindDeviceService(this);
		Log.d("SmartPosApplication", "Device service bind result: " + bindResult);
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		DeviceServiceManager.getInstance().unBindDeviceService();
	}

	public static Context getContext() {
		return context;
	}
}