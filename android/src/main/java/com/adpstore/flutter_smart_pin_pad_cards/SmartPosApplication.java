package com.adpstore.flutter_smart_pin_pad_cards;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import com.adpstore.flutter_smart_pin_pad_cards.param.AidParam;
import com.adpstore.flutter_smart_pin_pad_cards.param.CapkParam;
import java.util.List;

public class SmartPosApplication extends Application {
    private static final String TAG = "SmartPosApplication";
    private static Context context;
    public static DeviceServiceManagers usdkManage;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

        // Initialize DeviceServiceManagers instance
        usdkManage = DeviceServiceManagers.getInstance();

        // Bind device service
        boolean bindResult = usdkManage.bindDeviceService(this);
        Log.d(TAG, "Device service bind result: " + bindResult);

        // Initialize EMV parameters
        onInitCAPK();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (usdkManage != null) {
            usdkManage.unBindDeviceService();
            usdkManage = null;
        }
    }

    public static Context getContext() {
        return context;
    }

    private void onInitCAPK() {
        try {
            CapkParam capkParam = new CapkParam();
            capkParam.init(this);
            capkParam.saveAll();

            AidParam aidParam = new AidParam();
            aidParam.init(this);
            aidParam.saveAll();
        } catch (Exception e) {
            Log.e(TAG, "Error initializing CAPK: " + e.getMessage());
        }
    }
}