package com.adpstore.flutter_smart_pin_pad_cards;

import androidx.annotation.NonNull;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import android.content.Context;

import com.topwise.cloudpos.aidl.emv.AidlCheckCardListener;
import com.topwise.cloudpos.aidl.emv.level2.AidlEmvL2;
import com.topwise.cloudpos.aidl.magcard.TrackData;
import com.adpstore.flutter_smart_pin_pad_cards.entity.CardData;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

import java.util.HashMap;
import java.util.Map;

public class FlutterSmartPinPadCardsPlugin implements FlutterPlugin, MethodCallHandler {
    private static final String TAG = "SmartPinPadCards";
    private MethodChannel channel;
    private AidlEmvL2 aidlEmvL2;
    private Context context;
    private final static int TIME_OUT = 60 * 1000;
    private Result pendingResult;
    private boolean isCardReading = false;
    private ICardReader cardReader;
    private Handler mainHandler;
    private boolean isInitialized = false;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPlugin.FlutterPluginBinding flutterPluginBinding) {
        context = flutterPluginBinding.getApplicationContext();
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "flutter_smart_pin_pad_cards");
        channel.setMethodCallHandler(this);
        mainHandler = new Handler(Looper.getMainLooper());

        Log.d(TAG, "Plugin attached to engine");

        // Initialize services in background thread
        new Thread(this::initializeServices).start();
    }

    private void initializeServices() {
        try {
            Log.d(TAG, "Initializing services...");

            // Wait for application to initialize if needed
            SmartPosApplication app = SmartPosApplication.getInstance();
            if (app != null) {
                app.waitForInitialization(5000); // Wait up to 5 seconds
            }

            // Initialize device service if not already done
            boolean bindResult = DeviceServiceManagers.getInstance().bindDeviceService(context);
            Log.d(TAG, "Device service bind result: " + bindResult);

            // Wait a bit for service to bind
            Thread.sleep(1000);

            // Initialize EMV service
            aidlEmvL2 = DeviceServiceManagers.getInstance().getEmvL2();
            if (aidlEmvL2 == null) {
                Log.e(TAG, "EMV service initialization failed");
            } else {
                Log.d(TAG, "EMV service initialized successfully");
            }

            // Initialize Card Reader
            cardReader = DeviceServiceManagers.getInstance().getCardReader();
            if (cardReader == null) {
                Log.e(TAG, "Card Reader initialization failed");
            } else {
                Log.d(TAG, "Card Reader initialized successfully");
            }

            isInitialized = true;
            Log.d(TAG, "All services initialized successfully");

        } catch (Exception e) {
            Log.e(TAG, "Error initializing services: " + e.getMessage(), e);
        }
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        // Check if services are initialized
        if (!isInitialized) {
            result.error("NOT_INITIALIZED", "Plugin services are not yet initialized. Please wait and try again.", null);
            return;
        }

        switch (call.method) {
            case "getPlatformVersion":
                result.success("Android " + android.os.Build.VERSION.RELEASE);
                break;

            case "startSwipeCardReading":
                if (isCardReading) {
                    Log.i(TAG, "startSwipeCardReading: Card reader is already active");
                    result.error("ALREADY_READING", "Card reader is already active", null);
                    return;
                }
                startSwipeCardReading(result);
                break;

            case "stopSwipeCardReading":
                stopSwipeCardReading(result);
                Log.i(TAG, "stopSwipeCardReading called");
                break;

            case "startInsertCardReading":
                if (isCardReading) {
                    Log.i(TAG, "startInsertCardReading: Card reader is already active");
                    result.error("ALREADY_READING", "Card reader is already active", null);
                    return;
                }
                handleStartCardReading(call, result);
                break;

            case "stopInsertCardReading":
                handleStopCardReading(result);
                break;

            default:
                result.notImplemented();
        }
    }

    private void handleStartCardReading(MethodCall call, final Result result) {
        if (cardReader == null) {
            result.error("INIT_ERROR", "Card reader not initialized", null);
            return;
        }

        Map<String, Object> arguments = call.arguments();
        boolean enableMag = (boolean) arguments.get("enableMag");
        boolean enableIcc = (boolean) arguments.get("enableIcc");
        boolean enableRf = (boolean) arguments.get("enableRf");
        int timeout = (int) arguments.get("timeout");

        isCardReading = true;
        pendingResult = result;

        Log.d(TAG, String.format("Starting card reading - Mag: %s, IC: %s, RF: %s, Timeout: %d",
                enableMag, enableIcc, enableRf, timeout));

        cardReader.startFindCard(enableMag, enableIcc, enableRf, timeout, new CardReader.onReadCardListener() {
            @Override
            public void getReadState(final CardData cardData) {
                mainHandler.post(() -> {
                    try {
                        if (cardData != null && cardData.getEreturnType() == CardData.EReturnType.OK) {
                            Map<String, Object> resultMap = new HashMap<>();

                            // Ensure card data is properly mapped
                            resultMap.put("cardType", cardData.getEcardType().toString());
                            resultMap.put("pan", cardData.getPan());
                            resultMap.put("cardNumber", cardData.getPan()); // For compatibility
                            resultMap.put("expiryDate", cardData.getExpiryDate());
                            resultMap.put("serviceCode", cardData.getServiceCode());

                            if (cardData.getTrack1() != null && !cardData.getTrack1().isEmpty()) {
                                resultMap.put("track1", cardData.getTrack1());
                            }
                            if (cardData.getTrack2() != null && !cardData.getTrack2().isEmpty()) {
                                resultMap.put("track2", cardData.getTrack2());
                            }
                            if (cardData.getTrack3() != null && !cardData.getTrack3().isEmpty()) {
                                resultMap.put("track3", cardData.getTrack3());
                            }

                            Log.d(TAG, "Card data retrieved successfully: " + resultMap.toString());

                            isCardReading = false;
                            if (pendingResult != null) {
                                pendingResult.success(resultMap);
                                pendingResult = null;
                            }
                        } else {
                            String error = (cardData != null) ?
                                    cardData.getEreturnType().toString() : "Unknown error";
                            sendError("READ_ERROR", "Failed to read card: " + error);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error processing card data: " + e.getMessage(), e);
                        sendError("PROCESSING_ERROR", "Error processing card data: " + e.getMessage());
                    }
                });
            }

            @Override
            public void onNotification(CardData.EReturnType eReturnType) {
                Log.d(TAG, "Card reader notification: " + eReturnType.toString());

                switch (eReturnType) {
                    case RF_MULTI_CARD:
                        mainHandler.post(() -> {
                            sendError("MULTI_CARD", "Multiple RF cards detected");
                        });
                        break;

                    case OPEN_MAG_ERR:
                        mainHandler.post(() -> {
                            sendError("MAG_ERROR", "Failed to open magnetic card reader");
                        });
                        break;

                    case OPEN_IC_ERR:
                        mainHandler.post(() -> {
                            sendError("IC_ERROR", "Failed to open IC card reader");
                        });
                        break;

                    case OPEN_RF_ERR:
                        mainHandler.post(() -> {
                            sendError("RF_ERROR", "Failed to open RF card reader");
                        });
                        break;

                    default:
                        mainHandler.post(() -> {
                            sendError("UNKNOWN_ERROR", "Unknown error occurred: " + eReturnType.toString());
                        });
                        break;
                }
            }
        });
    }

    private void handleStopCardReading(Result result) {
        if (cardReader != null && isCardReading) {
            cardReader.cancel();
            isCardReading = false;
            result.success(null);
            Log.i(TAG, "stopInsertCardReading completed");
        } else {
            result.error("STOP_ERROR", "Card reader not active", null);
        }
    }

    private void startSwipeCardReading(final Result result) {
        if (aidlEmvL2 == null) {
            result.error("START_ERROR", "EMV service not available", null);
            return;
        }

        try {
            isCardReading = true;
            pendingResult = result;

            Log.d(TAG, "Starting swipe card reading...");

            aidlEmvL2.checkCard(true, false, false, TIME_OUT, new AidlCheckCardListener.Stub() {
                @Override
                public void onFindMagCard(TrackData trackData) throws RemoteException {
                    Log.d(TAG, "Magnetic card found");

                    Map<String, Object> cardData = new HashMap<>();
                    cardData.put("cardType", "MAG");
                    cardData.put("track1", trackData.getFirstTrackData());
                    cardData.put("track2", trackData.getSecondTrackData());
                    cardData.put("track3", trackData.getThirdTrackData());
                    cardData.put("cardNumber", trackData.getCardno());
                    cardData.put("pan", trackData.getCardno()); // For compatibility
                    cardData.put("expiryDate", trackData.getExpiryDate());
                    cardData.put("serviceCode", trackData.getServiceCode());

                    isCardReading = false;
                    if (pendingResult != null) {
                        pendingResult.success(cardData);
                        pendingResult = null;
                    }
                }

                @Override
                public void onSwipeCardFail() throws RemoteException {
                    Log.e(TAG, "Swipe card failed");
                    sendError("SWIPE_FAILED", "Failed to read card");
                }

                @Override
                public void onFindICCard() throws RemoteException {
                    Log.d(TAG, "IC card detected in swipe mode (not implemented)");
                }

                @Override
                public void onFindRFCard() throws RemoteException {
                    Log.d(TAG, "RF card detected in swipe mode (not implemented)");
                }

                @Override
                public void onTimeout() throws RemoteException {
                    Log.e(TAG, "Card reading timeout");
                    sendError("TIMEOUT", "Card reading timed out");
                }

                @Override
                public void onCanceled() throws RemoteException {
                    Log.d(TAG, "Card reading cancelled");
                    sendError("CANCELLED", "Card reading was cancelled");
                }

                @Override
                public void onError(int error) throws RemoteException {
                    Log.e(TAG, "Card reader error: " + error);
                    sendError("READER_ERROR", "Card reader error: " + error);
                }
            });

        } catch (RemoteException e) {
            Log.e(TAG, "Remote exception during card reading: " + e.getMessage(), e);
            result.error("START_ERROR", "Failed to start card reading: " + e.getMessage(), null);
            isCardReading = false;
        }
    }

    private void stopSwipeCardReading(Result result) {
        if (aidlEmvL2 != null && isCardReading) {
            try {
                aidlEmvL2.cancelCheckCard();
                isCardReading = false;
                result.success(null);
                Log.d(TAG, "Swipe card reading stopped");
            } catch (RemoteException e) {
                Log.e(TAG, "Error stopping swipe card reading: " + e.getMessage());
                result.error("STOP_ERROR", "Failed to stop card reader: " + e.getMessage(), null);
            }
        } else {
            result.success(null);
        }
    }

    private void sendError(String code, String message) {
        Log.e(TAG, "Sending error: " + code + " - " + message);
        isCardReading = false;
        if (pendingResult != null) {
            pendingResult.error(code, message, null);
            pendingResult = null;
        }
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPlugin.FlutterPluginBinding binding) {
        Log.d(TAG, "Plugin detached from engine");

        if (isCardReading) {
            if (aidlEmvL2 != null) {
                try {
                    aidlEmvL2.cancelCheckCard();
                } catch (RemoteException ignored) {
                }
            }

            if (cardReader != null) {
                cardReader.cancel();
            }
        }

        channel.setMethodCallHandler(null);
        aidlEmvL2 = null;
        cardReader = null;
        context = null;
        mainHandler = null;
        isInitialized = false;
    }
}