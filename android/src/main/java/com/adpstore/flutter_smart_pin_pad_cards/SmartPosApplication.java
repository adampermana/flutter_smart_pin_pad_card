package com.adpstore.flutter_smart_pin_pad_cards;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import com.adpstore.flutter_smart_pin_pad_cards.param.AidParam;
import com.adpstore.flutter_smart_pin_pad_cards.param.CapkParam;

public class SmartPosApplication extends Application {
    private static final String TAG = "SmartPosApplication";
    private static Context context;
    private static SmartPosApplication instance;
    public static DeviceServiceManagers usdkManage;
    private boolean isInitialized = false;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        context = this;

        Log.d(TAG, "SmartPosApplication onCreate");

        // Initialize in background thread to avoid ANR
        new Thread(this::initializeServices).start();
    }

    private void initializeServices() {
        try {
            // Initialize DeviceServiceManagers instance
            usdkManage = DeviceServiceManagers.getInstance();

            // Bind device service
            boolean bindResult = usdkManage.bindDeviceService(this);
            Log.d(TAG, "Device service bind result: " + bindResult);

            // Wait a bit for service to bind
            Thread.sleep(1000);

            // Initialize EMV parameters
            initializeEMVParameters();

            isInitialized = true;
            Log.d(TAG, "SmartPosApplication initialization completed");
        } catch (Exception e) {
            Log.e(TAG, "Error during initialization: " + e.getMessage(), e);
        }
    }

    private void initializeEMVParameters() {
        try {
            Log.d(TAG, "Initializing EMV parameters");

            CapkParam capkParam = new CapkParam();
            capkParam.init(this);
            capkParam.saveAll();

            AidParam aidParam = new AidParam();
            aidParam.init(this);
            aidParam.saveAll();

            Log.d(TAG, "EMV parameters initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing EMV parameters: " + e.getMessage(), e);
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.d(TAG, "SmartPosApplication onTerminate");

        if (usdkManage != null) {
            try {
                usdkManage.unBindDeviceService();
                usdkManage = null;
            } catch (Exception e) {
                Log.e(TAG, "Error during termination: " + e.getMessage());
            }
        }
    }

    public static Context getContext() {
        return context;
    }

    public static SmartPosApplication getInstance() {
        return instance;
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    /**
     * Wait for initialization to complete
     * @param timeoutMs Maximum time to wait in milliseconds
     * @return true if initialized within timeout, false otherwise
     */
    public boolean waitForInitialization(long timeoutMs) {
        long startTime = System.currentTimeMillis();
        while (!isInitialized && (System.currentTimeMillis() - startTime) < timeoutMs) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return isInitialized;
    }
}