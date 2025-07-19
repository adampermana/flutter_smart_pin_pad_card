package com.adpstore.flutter_smart_pin_pad_cards;

import androidx.annotation.NonNull;

import android.os.Bundle;
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
    private PinpadManager pinpadManager;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPlugin.FlutterPluginBinding flutterPluginBinding) {
        context = flutterPluginBinding.getApplicationContext();
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "flutter_smart_pin_pad_cards");
        channel.setMethodCallHandler(this);
        mainHandler = new Handler(Looper.getMainLooper());

        // Initialize device service
        boolean bindResult = DeviceServiceManagers.getInstance().bindDeviceService(context);
        Log.d(TAG, "Device service bind result: " + bindResult);

        // Initialize EMV service, Card Reader, and Pinpad Manager
        initializeServices();
    }

    private void initializeServices() {
        try {
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

            // Initialize Pinpad Manager
            pinpadManager = PinpadManager.getInstance();
            if (pinpadManager != null) {
                boolean pinpadInit = pinpadManager.initPinpad();
                Log.d(TAG, "Pinpad initialized: " + pinpadInit);
            } else {
                Log.e(TAG, "Pinpad Manager initialization failed");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error initializing services: " + e.getMessage());
        }
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        switch (call.method) {
            case "getPlatformVersion":
                result.success("Android " + android.os.Build.VERSION.RELEASE);
                break;

            case "startSwipeCardReading":
                if (isCardReading) {
                    result.error("ALREADY_READING", "Card reader is already active", null);
                    return;
                }
                startSwipeCardReading(result);
                break;

            case "stopSwipeCardReading":
                stopSwipeCardReading(result);
                break;

            case "startInsertCardReading":
                if (isCardReading) {
                    result.error("ALREADY_READING", "Card reader is already active", null);
                    return;
                }
                handleStartCardReading(call, result);
                break;

            case "stopInsertCardReading":
                handleStopCardReading(result);
                break;

            // PIN Block methods
            case "createPinBlock":
                handleCreatePinBlock(call, result);
                break;

            case "verifyPin":
                handleVerifyPin(call, result);
                break;

            case "initPinpad":
                handleInitPinpad(result);
                break;

            case "closePinpad":
                handleClosePinpad(result);
                break;

            case "getPinpadStatus":
                handleGetPinpadStatus(result);
                break;

            default:
                result.notImplemented();
        }
    }

    // ... existing card reading methods remain the same ...

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

        cardReader.startFindCard(enableMag, enableIcc, enableRf, timeout, new CardReader.onReadCardListener() {
            @Override
            public void getReadState(final CardData cardData) {
                mainHandler.post(() -> {
                    if (cardData != null && cardData.getEreturnType() == CardData.EReturnType.OK) {
                        Map<String, Object> resultMap = new HashMap<>();

                        // Pastikan data kartu dimasukkan ke map
                        resultMap.put("cardType", cardData.getEcardType().toString());
                        resultMap.put("pan", cardData.getPan());
                        resultMap.put("expiryDate", cardData.getExpiryDate());
                        resultMap.put("serviceCode", cardData.getServiceCode());

                        if (cardData.getTrack1() != null) {
                            resultMap.put("track1", cardData.getTrack1());
                        }
                        if (cardData.getTrack2() != null) {
                            resultMap.put("track2", cardData.getTrack2());
                        }
                        if (cardData.getTrack3() != null) {
                            resultMap.put("track3", cardData.getTrack3());
                        }

                        Log.d(TAG, "Card data retrieved: " + resultMap.toString());

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
                });
            }

            @Override
            public void onNotification(CardData.EReturnType eReturnType) {
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
        } else {
            result.error("STOP_ERROR", "Card reader not active", null);
        }
    }

    private void startSwipeCardReading(final Result result) {
        if (aidlEmvL2 == null) {
            result.error("START_ERROR", "Card reader hardware not available", null);
            return;
        }

        try {
            isCardReading = true;
            pendingResult = result;

            aidlEmvL2.checkCard(true, false, false, TIME_OUT, new AidlCheckCardListener.Stub() {
                @Override
                public void onFindMagCard(TrackData trackData) throws RemoteException {
                    Map<String, String> cardData = new HashMap<>();
                    cardData.put("firstTrack", trackData.getFirstTrackData());
                    cardData.put("secondTrack", trackData.getSecondTrackData());
                    cardData.put("thirdTrack", trackData.getThirdTrackData());
                    cardData.put("cardNumber", trackData.getCardno());
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
                    sendError("SWIPE_FAILED", "Failed to read card");
                }

                @Override
                public void onFindICCard() throws RemoteException {
                    // Not implemented in this version
                }

                @Override
                public void onFindRFCard() throws RemoteException {
                    // Not implemented in this version
                }

                @Override
                public void onTimeout() throws RemoteException {
                    sendError("TIMEOUT", "Card reading timed out");
                }

                @Override
                public void onCanceled() throws RemoteException {
                    sendError("CANCELLED", "Card reading was cancelled");
                }

                @Override
                public void onError(int error) throws RemoteException {
                    sendError("READER_ERROR", "Card reader error: " + error);
                }
            });

        } catch (RemoteException e) {
            Log.e(TAG, "Remote exception during card reading: " + e.getMessage());
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
            } catch (RemoteException e) {
                result.error("STOP_ERROR", "Failed to stop card reader: " + e.getMessage(), null);
            }
        } else {
            result.success(null);
        }
    }

    // PIN Block related methods
    private void handleCreatePinBlock(MethodCall call, Result result) {
        try {
            if (pinpadManager == null) {
                result.error("PINPAD_ERROR", "Pinpad manager not initialized", null);
                return;
            }

            Map<String, Object> arguments = call.arguments();
            String pin = (String) arguments.get("pin");
            String cardNumber = (String) arguments.get("cardNumber");
            Integer format = (Integer) arguments.get("format");
            Integer keyIndex = (Integer) arguments.get("keyIndex");
            Integer encryptionType = (Integer) arguments.get("encryptionType");

            // Validate parameters
            if (pin == null || cardNumber == null) {
                result.error("INVALID_PARAMS", "PIN and card number are required", null);
                return;
            }

            // Set default values if not provided
            if (format == null) format = PinpadManager.PIN_BLOCK_FORMAT_0;
            if (keyIndex == null) keyIndex = 0;
            if (encryptionType == null) encryptionType = PinpadManager.ENCRYPT_3DES;

            Map<String, Object> pinBlockResult = pinpadManager.createPinBlock(
                    pin, cardNumber, format, keyIndex, encryptionType);

            if ((Boolean) pinBlockResult.get("success")) {
                result.success(pinBlockResult);
            } else {
                result.error("PINBLOCK_ERROR",
                        (String) pinBlockResult.get("error"),
                        pinBlockResult);
            }

        } catch (Exception e) {
            Log.e(TAG, "Exception in handleCreatePinBlock: " + e.getMessage());
            result.error("PINBLOCK_EXCEPTION", "Exception: " + e.getMessage(), null);
        }
    }

    private void handleVerifyPin(MethodCall call, Result result) {
        try {
            if (pinpadManager == null) {
                result.error("PINPAD_ERROR", "Pinpad manager not initialized", null);
                return;
            }

            Map<String, Object> arguments = call.arguments();
            String pinBlock = (String) arguments.get("pinBlock");
            String cardNumber = (String) arguments.get("cardNumber");
            Integer format = (Integer) arguments.get("format");
            Integer keyIndex = (Integer) arguments.get("keyIndex");
            Integer encryptionType = (Integer) arguments.get("encryptionType");

            // Validate parameters
            if (pinBlock == null || cardNumber == null) {
                result.error("INVALID_PARAMS", "PIN block and card number are required", null);
                return;
            }

            // Set default values if not provided
            if (format == null) format = PinpadManager.PIN_BLOCK_FORMAT_0;
            if (keyIndex == null) keyIndex = 0;
            if (encryptionType == null) encryptionType = PinpadManager.ENCRYPT_3DES;

            Map<String, Object> verifyResult = pinpadManager.verifyPin(
                    pinBlock, cardNumber, format, keyIndex, encryptionType);

            if ((Boolean) verifyResult.get("success")) {
                result.success(verifyResult);
            } else {
                result.error("VERIFY_ERROR",
                        (String) verifyResult.get("error"),
                        verifyResult);
            }

        } catch (Exception e) {
            Log.e(TAG, "Exception in handleVerifyPin: " + e.getMessage());
            result.error("VERIFY_EXCEPTION", "Exception: " + e.getMessage(), null);
        }
    }

    private void handleInitPinpad(Result result) {
        try {
            if (pinpadManager == null) {
                result.error("PINPAD_ERROR", "Pinpad manager not available", null);
                return;
            }

            boolean initResult = pinpadManager.initPinpad();
            if (initResult) {
                result.success(true);
            } else {
                result.error("INIT_ERROR", "Failed to initialize pinpad", null);
            }

        } catch (Exception e) {
            Log.e(TAG, "Exception in handleInitPinpad: " + e.getMessage());
            result.error("INIT_EXCEPTION", "Exception: " + e.getMessage(), null);
        }
    }

    private void handleClosePinpad(Result result) {
        try {
            if (pinpadManager == null) {
                result.error("PINPAD_ERROR", "Pinpad manager not available", null);
                return;
            }

            pinpadManager.closePinpad();
            result.success(null);

        } catch (Exception e) {
            Log.e(TAG, "Exception in handleClosePinpad: " + e.getMessage());
            result.error("CLOSE_EXCEPTION", "Exception: " + e.getMessage(), null);
        }
    }

    private void handleGetPinpadStatus(Result result) {
        try {
            if (pinpadManager == null) {
                result.error("PINPAD_ERROR", "Pinpad manager not available", null);
                return;
            }

            Map<String, Object> status = pinpadManager.getPinpadStatus();
            result.success(status);

        } catch (Exception e) {
            Log.e(TAG, "Exception in handleGetPinpadStatus: " + e.getMessage());
            result.error("STATUS_EXCEPTION", "Exception: " + e.getMessage(), null);
        }
    }

    // Additional PIN block utility methods
    private void handleLoadMainKey(MethodCall call, Result result) {
        try {
            if (pinpadManager == null) {
                result.error("PINPAD_ERROR", "Pinpad manager not initialized", null);
                return;
            }

            Map<String, Object> arguments = call.arguments();
            Integer keyIndex = (Integer) arguments.get("keyIndex");
            String keyDataHex = (String) arguments.get("keyData");
            String checkValueHex = (String) arguments.get("checkValue");

            if (keyIndex == null || keyDataHex == null) {
                result.error("INVALID_PARAMS", "Key index and key data are required", null);
                return;
            }

            byte[] keyData = hexToBytes(keyDataHex);
            byte[] checkValue = checkValueHex != null ? hexToBytes(checkValueHex) : null;

            boolean loadResult = pinpadManager.loadMainKey(keyIndex, keyData, checkValue);
            result.success(loadResult);

        } catch (Exception e) {
            Log.e(TAG, "Exception in handleLoadMainKey: " + e.getMessage());
            result.error("LOAD_KEY_EXCEPTION", "Exception: " + e.getMessage(), null);
        }
    }

    private void handleLoadWorkKey(MethodCall call, Result result) {
        try {
            if (pinpadManager == null) {
                result.error("PINPAD_ERROR", "Pinpad manager not initialized", null);
                return;
            }

            Map<String, Object> arguments = call.arguments();
            Integer keyType = (Integer) arguments.get("keyType");
            Integer masterKeyId = (Integer) arguments.get("masterKeyId");
            Integer workKeyId = (Integer) arguments.get("workKeyId");
            String keyDataHex = (String) arguments.get("keyData");
            String checkValueHex = (String) arguments.get("checkValue");

            if (keyType == null || masterKeyId == null || workKeyId == null || keyDataHex == null) {
                result.error("INVALID_PARAMS", "All key parameters are required", null);
                return;
            }

            byte[] keyData = hexToBytes(keyDataHex);
            byte[] checkValue = checkValueHex != null ? hexToBytes(checkValueHex) : null;

            boolean loadResult = pinpadManager.loadWorkKey(keyType, masterKeyId, workKeyId, keyData, checkValue);
            result.success(loadResult);

        } catch (Exception e) {
            Log.e(TAG, "Exception in handleLoadWorkKey: " + e.getMessage());
            result.error("LOAD_WORK_KEY_EXCEPTION", "Exception: " + e.getMessage(), null);
        }
    }

    private void handleGetKeyState(MethodCall call, Result result) {
        try {
            if (pinpadManager == null) {
                result.error("PINPAD_ERROR", "Pinpad manager not initialized", null);
                return;
            }

            Map<String, Object> arguments = call.arguments();
            Integer keyType = (Integer) arguments.get("keyType");
            Integer keyIndex = (Integer) arguments.get("keyIndex");

            if (keyType == null || keyIndex == null) {
                result.error("INVALID_PARAMS", "Key type and key index are required", null);
                return;
            }

            boolean keyState = pinpadManager.getKeyState(keyType, keyIndex);
            result.success(keyState);

        } catch (Exception e) {
            Log.e(TAG, "Exception in handleGetKeyState: " + e.getMessage());
            result.error("KEY_STATE_EXCEPTION", "Exception: " + e.getMessage(), null);
        }
    }

    private void handleGetMac(MethodCall call, Result result) {
        try {
            if (pinpadManager == null) {
                result.error("PINPAD_ERROR", "Pinpad manager not initialized", null);
                return;
            }

            Map<String, Object> arguments = call.arguments();
            // Convert arguments to Bundle for MAC calculation
            Bundle param = new Bundle();
            for (Map.Entry<String, Object> entry : arguments.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (value instanceof String) {
                    param.putString(key, (String) value);
                } else if (value instanceof Integer) {
                    param.putInt(key, (Integer) value);
                } else if (value instanceof Boolean) {
                    param.putBoolean(key, (Boolean) value);
                }
            }

            Map<String, Object> macResult = pinpadManager.getMac(param);
            if ((Boolean) macResult.get("success")) {
                result.success(macResult);
            } else {
                result.error("MAC_ERROR", (String) macResult.get("error"), macResult);
            }

        } catch (Exception e) {
            Log.e(TAG, "Exception in handleGetMac: " + e.getMessage());
            result.error("MAC_EXCEPTION", "Exception: " + e.getMessage(), null);
        }
    }

    private void handleGetRandom(Result result) {
        try {
            if (pinpadManager == null) {
                result.error("PINPAD_ERROR", "Pinpad manager not initialized", null);
                return;
            }

            byte[] random = pinpadManager.getRandom();
            if (random != null) {
                result.success(bytesToHex(random));
            } else {
                result.error("RANDOM_ERROR", "Failed to generate random number", null);
            }

        } catch (Exception e) {
            Log.e(TAG, "Exception in handleGetRandom: " + e.getMessage());
            result.error("RANDOM_EXCEPTION", "Exception: " + e.getMessage(), null);
        }
    }

    // Utility methods
    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02X", b));
        }
        return result.toString();
    }

    private byte[] hexToBytes(String hex) {
        if (hex == null || hex.isEmpty()) {
            return new byte[0];
        }

        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }

    private void sendError(String code, String message) {
        isCardReading = false;
        if (pendingResult != null) {
            pendingResult.error(code, message, null);
            pendingResult = null;
        }
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPlugin.FlutterPluginBinding binding) {
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

        if (pinpadManager != null) {
            pinpadManager.closePinpad();
        }

        DeviceServiceManagers.getInstance().unBindDeviceService();
        channel.setMethodCallHandler(null);
        aidlEmvL2 = null;
        cardReader = null;
        pinpadManager = null;
        context = null;
        mainHandler = null;
    }
}