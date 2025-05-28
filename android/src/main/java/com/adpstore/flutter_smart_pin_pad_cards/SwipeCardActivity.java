package com.adpstore.flutter_smart_pin_pad_cards;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;

import com.topwise.cloudpos.aidl.emv.AidlCheckCardListener;
import com.topwise.cloudpos.aidl.emv.level2.AidlEmvL2;
import com.topwise.cloudpos.aidl.magcard.TrackData;
import com.topwise.cloudpos.service.DeviceServiceManager;

import org.jetbrains.annotations.Nullable;

import io.flutter.plugin.common.MethodChannel;

public class SwipeCardActivity extends Service {
    private static final String TAG = "SwipeCardActivity";
    private AidlEmvL2 aidlEmvL2;
    private final static int TIME_OUT = 60 * 1000;
    private MethodChannel.Result flutterResult;
    private boolean isReading = false;
    private boolean isInitialized = false;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private CardReadCallback callback;
    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        SwipeCardActivity getService() {
            return SwipeCardActivity.this;
        }
    }

    public interface CardReadCallback {
        void onCardRead(TrackData trackData);

        void onCardReadError(String errorCode, String errorMessage);
    }

    // Required public empty constructor
    public SwipeCardActivity() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initializeDeviceService();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private void initializeDeviceService() {
        mainHandler.post(() -> {
            try {
                DeviceServiceManager.getInstance().init(this);
                // Wait for service to initialize
                mainHandler.postDelayed(this::initializeEmvService, 2000);
            } catch (Exception e) {
                Log.e(TAG, "Error initializing device service: " + e.getMessage());
                sendSingleError("INIT_ERROR", "Failed to initialize device service");
            }
        });
    }

    private void initializeEmvService() {
        try {
            aidlEmvL2 = DeviceServiceManager.getInstance().getEmvL2();
            if (aidlEmvL2 != null) {
                isInitialized = true;
                if (isReading) {
                    startSwipeCardReading();
                }
            } else {
                // If EMV service is null, retry after delay
                mainHandler.postDelayed(() -> {
                    try {
                        aidlEmvL2 = DeviceServiceManager.getInstance().getEmvL2();
                        if (aidlEmvL2 != null) {
                            isInitialized = true;
                            if (isReading) {
                                startSwipeCardReading();
                            }
                        } else {
                            sendSingleError("EMV_ERROR", "Failed to initialize EMV service after retry");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error getting EMV service on retry: " + e.getMessage());
                        sendSingleError("EMV_ERROR", "Failed to get EMV service");
                    }
                }, 2000);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting EMV service: " + e.getMessage());
            sendSingleError("EMV_ERROR", "Failed to get EMV service");
        }
    }

    public void setCallback(CardReadCallback callback) {
        this.callback = callback;
    }

    public void setFlutterResult(MethodChannel.Result result) {
        this.flutterResult = result;
    }

    public void startSwipeCardReading() {
        if (!isInitialized) {
            isReading = true;
            return;
        }

        if (aidlEmvL2 == null) {
            sendSingleError("HARDWARE_ERROR", "Card reader not available");
            return;
        }

        try {
            isReading = true;
            aidlEmvL2.checkCard(true, false, false, TIME_OUT, new AidlCheckCardListener.Stub() {
                @Override
                public void onFindMagCard(TrackData trackData) throws RemoteException {
                    isReading = false;
                    if (callback != null) {
                        mainHandler.post(() -> callback.onCardRead(trackData));
                    }
                    sendSingleSuccess(trackData);
                }

                @Override
                public void onSwipeCardFail() {
                    sendSingleError("SWIPE_FAILED", "Failed to read card");
                }

                @Override
                public void onFindICCard() {
                    // Not implemented
                }

                @Override
                public void onFindRFCard() {
                    // Not implemented
                }

                @Override
                public void onTimeout() {
                    sendSingleError("TIMEOUT", "Card reading timed out");
                }

                @Override
                public void onCanceled() {
                    sendSingleError("CANCELLED", "Card reading was cancelled");
                }

                @Override
                public void onError(int error) {
                    sendSingleError("READER_ERROR", "Card reader error: " + error);
                }
            });
        } catch (RemoteException e) {
            Log.e(TAG, "Remote exception during card reading: " + e.getMessage());
            sendSingleError("REMOTE_ERROR", "Failed to start card reader");
        }
    }

    private synchronized void sendSingleSuccess(TrackData trackData) {
        mainHandler.post(() -> {
            if (flutterResult != null) {
                Bundle result = new Bundle();
                try {
                    result.putString("firstTrack", trackData.getFirstTrackData());
                    result.putString("secondTrack", trackData.getSecondTrackData());
                    result.putString("thirdTrack", trackData.getThirdTrackData());
                    result.putString("cardNumber", trackData.getCardno());
                    result.putString("expiryDate", trackData.getExpiryDate());
                    result.putString("serviceCode", trackData.getServiceCode());

                    MethodChannel.Result localResult = flutterResult;
                    flutterResult = null;
                    localResult.success(result);
                } catch (Exception e) {
                    Log.e(TAG, "Error processing track data: " + e.getMessage());
                    sendSingleError("DATA_ERROR", "Error processing card data");
                }
            }
        });
    }

    private synchronized void sendSingleError(String code, String message) {
        mainHandler.post(() -> {
            if (flutterResult != null) {
                MethodChannel.Result localResult = flutterResult;
                flutterResult = null;
                if (callback != null) {
                    callback.onCardReadError(code, message);
                }
                localResult.error(code, message, null);
            }
        });
        isReading = false;
    }

    public void stopSwipeCardReading() {
        if (aidlEmvL2 != null && isReading) {
            try {
                aidlEmvL2.cancelCheckCard();
                isReading = false;
            } catch (RemoteException e) {
                Log.e(TAG, "Error canceling card read: " + e.getMessage());
            }
        }
    }


//    @Override
//    protected void onPause() {
//        super.onPause();
//        stopSwipeCardReading();
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSwipeCardReading();
        aidlEmvL2 = null;
        callback = null;
        flutterResult = null;
    }
}