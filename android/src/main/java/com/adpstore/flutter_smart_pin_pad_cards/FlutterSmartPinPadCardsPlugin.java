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
    private DynamicPinBlockManager dynamicPinBlockManager;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPlugin.FlutterPluginBinding flutterPluginBinding) {
        context = flutterPluginBinding.getApplicationContext();
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "flutter_smart_pin_pad_cards");
        channel.setMethodCallHandler(this);
        mainHandler = new Handler(Looper.getMainLooper());

        // Initialize device service
        boolean bindResult = DeviceServiceManagers.getInstance().bindDeviceService(context);
        Log.d(TAG, "Device service bind result: " + bindResult);

        // Initialize services
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

            // Initialize Dynamic Pinpad Manager
            dynamicPinBlockManager = DynamicPinBlockManager.getInstance();
            if (dynamicPinBlockManager != null) {
                boolean pinpadInit = dynamicPinBlockManager.initPinpad();
                Log.d(TAG, "Dynamic Pinpad initialized: " + pinpadInit);
            } else {
                Log.e(TAG, "Dynamic Pinpad Manager initialization failed");
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

            // Card reading methods
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

            // Dynamic PIN Block methods
            case "createDynamicPinBlock":
                handleCreateDynamicPinBlock(call, result);
                break;

            case "createPinDynamic":
                handleCreatePinDynamic(call, result);
                break;

            case "changePinDynamic":
                handleChangePinDynamic(call, result);
                break;

            case "authorizePinDynamic":
                handleAuthorizePinDynamic(call, result);
                break;

            case "testAllPinBlockFormats":
                handleTestAllPinBlockFormats(call, result);
                break;

            // Legacy PIN Block methods for backward compatibility
            case "createPinBlock":
                handleLegacyCreatePinBlock(call, result);
                break;

            // Pinpad management methods
            case "initPinpad":
                handleInitPinpad(result);
                break;

            case "closePinpad":
                handleClosePinpad(result);
                break;

            case "getPinpadStatus":
                handleGetPinpadStatus(result);
                break;

            case "loadMainKey":
                handleLoadMainKey(call, result);
                break;

            case "loadWorkKey":
                handleLoadWorkKey(call, result);
                break;

            case "getKeyState":
                handleGetKeyState(call, result);
                break;

            case "getMac":
                handleGetMac(call, result);
                break;

            case "getRandom":
                handleGetRandom(result);
                break;

            case "getMasterKeyInfo":
                handleGetMasterKeyInfo(result);
                break;

            default:
                result.notImplemented();
        }
    }

    // Card reading methods (existing implementation)
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
//                        sendError("READ_ERROR", "Failed to read card: " + error); // Hide Dulu guys
                    }
                });
            }

            @Override
            public void onNotification(CardData.EReturnType eReturnType) {
                switch (eReturnType) {
                    case RF_MULTI_CARD:
                        mainHandler.post(() -> sendError("MULTI_CARD", "Multiple RF cards detected"));
                        break;
                    case OPEN_MAG_ERR:
                        mainHandler.post(() -> sendError("MAG_ERROR", "Failed to open magnetic card reader"));
                        break;
                    case OPEN_IC_ERR:
                        mainHandler.post(() -> sendError("IC_ERROR", "Failed to open IC card reader"));
                        break;
                    case OPEN_RF_ERR:
                        mainHandler.post(() -> sendError("RF_ERROR", "Failed to open RF card reader"));
                        break;
                    default:
                        mainHandler.post(() -> sendError("UNKNOWN_ERROR", "Unknown error occurred: " + eReturnType.toString()));
                        break;
                }
            }
        });
    }

    private void handleGetMasterKeyInfo(Result result) {
        try {
            if (dynamicPinBlockManager == null) {
                result.error("PINPAD_ERROR", "Pinpad manager not available", null);
                return;
            }

            Map<String, Object> masterKeyInfo = dynamicPinBlockManager.getMasterKeyInfo();
            result.success(masterKeyInfo);

        } catch (Exception e) {
            Log.e(TAG, "Exception in handleGetMasterKeyInfo: " + e.getMessage());
            result.error("MASTER_KEY_INFO_EXCEPTION", "Exception: " + e.getMessage(), null);
        }
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

    // Legacy PIN Block method for backward compatibility
    private void handleLegacyCreatePinBlock(MethodCall call, Result result) {
        try {
            if (dynamicPinBlockManager == null) {
                result.error("PINPAD_ERROR", "Pinpad manager not available", null);
                return;
            }

            Map<String, Object> arguments = call.arguments();
            String pin = (String) arguments.get("pin");
            String cardNumber = (String) arguments.get("cardNumber");
            Integer format = (Integer) arguments.get("format");
            Integer keyIndex = (Integer) arguments.get("keyIndex");
            Integer encryptionType = (Integer) arguments.get("encryptionType");
            String encryptionKey = (String) arguments.get("encryptionKey");


            // Validate required parameters
            if (pin == null || cardNumber == null) {
                result.error("INVALID_PARAMS", "PIN and card number are required", null);
                return;
            }

            // Set defaults
            if (format == null) format = DynamicPinBlockManager.PIN_BLOCK_FORMAT_0;
            if (keyIndex == null) keyIndex = 0;
            if (encryptionType == null) encryptionType = DynamicPinBlockManager.ENCRYPT_3DES;

            // Try legacy method first
            Map<String, Object> legacyResult = dynamicPinBlockManager.createPinBlock(
                    pin, cardNumber, format, keyIndex, encryptionType, encryptionKey);

            result.success(legacyResult);

        } catch (Exception e) {
            Log.e(TAG, "Exception in handleLegacyCreatePinBlock: " + e.getMessage());
            result.error("LEGACY_PINBLOCK_EXCEPTION", "Exception: " + e.getMessage(), null);
        }
    }

    // Dynamic PIN Block methods
    private void handleCreateDynamicPinBlock(MethodCall call, Result result) {
        try {
            if (dynamicPinBlockManager == null) {
                result.error("PINPAD_ERROR", "Pinpad manager not available", null);
                return;
            }

            Map<String, Object> arguments = call.arguments();
            String pin = (String) arguments.get("pin");
            String cardNumber = (String) arguments.get("cardNumber");
            Integer format = (Integer) arguments.get("format");
            String encryptionKey = (String) arguments.get("encryptionKey");
            Integer encryptionType = (Integer) arguments.get("encryptionType");
            String fillerChar = (String) arguments.get("fillerChar");
            Boolean useHardwareEncryption = (Boolean) arguments.get("useHardwareEncryption");

            // Validate required parameters
            if (pin == null || cardNumber == null) {
                result.error("INVALID_PARAMS", "PIN and card number are required", null);
                return;
            }

            // Set defaults
            if (format == null) format = DynamicPinBlockManager.PIN_BLOCK_FORMAT_0;
            if (encryptionKey == null) encryptionKey = "404142434445464748494A4B4C4D4E4F";
            if (encryptionType == null) encryptionType = DynamicPinBlockManager.ENCRYPT_3DES;
            if (fillerChar == null) fillerChar = "F";
            if (useHardwareEncryption == null) useHardwareEncryption = true;

            Map<String, Object> pinBlockResult = dynamicPinBlockManager.createDynamicPinBlock(
                    pin, cardNumber, format, encryptionKey, encryptionType, fillerChar, useHardwareEncryption);

            result.success(pinBlockResult);

        } catch (Exception e) {
            Log.e(TAG, "Exception in handleCreateDynamicPinBlock: " + e.getMessage());
            result.error("DYNAMIC_PINBLOCK_EXCEPTION", "Exception: " + e.getMessage(), null);
        }
    }

    private void handleCreatePinDynamic(MethodCall call, Result result) {
        try {
            if (dynamicPinBlockManager == null) {
                result.error("PINPAD_ERROR", "Pinpad manager not available", null);
                return;
            }

            Map<String, Object> arguments = call.arguments();
            String newPin = (String) arguments.get("newPin");
            String cardNumber = (String) arguments.get("cardNumber");
            Integer format = (Integer) arguments.get("format");
            String encryptionKey = (String) arguments.get("encryptionKey");
            Integer encryptionType = (Integer) arguments.get("encryptionType");
            Boolean useHardwareEncryption = (Boolean) arguments.get("useHardwareEncryption");

            // Validate required parameters
            if (newPin == null || cardNumber == null) {
                result.error("INVALID_PARAMS", "New PIN and card number are required", null);
                return;
            }

            // Set defaults
            if (format == null) format = DynamicPinBlockManager.PIN_BLOCK_FORMAT_0;
            if (encryptionKey == null) encryptionKey = "404142434445464748494A4B4C4D4E4F";
            if (encryptionType == null) encryptionType = DynamicPinBlockManager.ENCRYPT_3DES;
            if (useHardwareEncryption == null) useHardwareEncryption = true;

            Map<String, Object> createPinResult = dynamicPinBlockManager.createPin(
                    newPin, cardNumber, format, encryptionKey, encryptionType, useHardwareEncryption);

            result.success(createPinResult);

        } catch (Exception e) {
            Log.e(TAG, "Exception in handleCreatePinDynamic: " + e.getMessage());
            result.error("CREATE_PIN_DYNAMIC_EXCEPTION", "Exception: " + e.getMessage(), null);
        }
    }

    private void handleChangePinDynamic(MethodCall call, Result result) {
        try {
            if (dynamicPinBlockManager == null) {
                result.error("PINPAD_ERROR", "Pinpad manager not available", null);
                return;
            }

            Map<String, Object> arguments = call.arguments();
            String currentPin = (String) arguments.get("currentPin");
            String newPin = (String) arguments.get("newPin");
            String cardNumber = (String) arguments.get("cardNumber");
            Integer format = (Integer) arguments.get("format");
            String encryptionKey = (String) arguments.get("encryptionKey");
            Integer encryptionType = (Integer) arguments.get("encryptionType");
            Boolean useHardwareEncryption = (Boolean) arguments.get("useHardwareEncryption");

            // Validate required parameters
            if (currentPin == null || newPin == null || cardNumber == null) {
                result.error("INVALID_PARAMS", "Current PIN, new PIN, and card number are required", null);
                return;
            }

            // Set defaults
            if (format == null) format = DynamicPinBlockManager.PIN_BLOCK_FORMAT_0;
            if (encryptionKey == null) encryptionKey = "404142434445464748494A4B4C4D4E4F";
            if (encryptionType == null) encryptionType = DynamicPinBlockManager.ENCRYPT_3DES;
            if (useHardwareEncryption == null) useHardwareEncryption = true;

            Map<String, Object> changePinResult = dynamicPinBlockManager.changePin(
                    currentPin, newPin, cardNumber, format, encryptionKey, encryptionType, useHardwareEncryption);

            result.success(changePinResult);

        } catch (Exception e) {
            Log.e(TAG, "Exception in handleChangePinDynamic: " + e.getMessage());
            result.error("CHANGE_PIN_DYNAMIC_EXCEPTION", "Exception: " + e.getMessage(), null);
        }
    }

    private void handleAuthorizePinDynamic(MethodCall call, Result result) {
        try {
            if (dynamicPinBlockManager == null) {
                result.error("PINPAD_ERROR", "Pinpad manager not available", null);
                return;
            }

            Map<String, Object> arguments = call.arguments();
            String pin = (String) arguments.get("pin");
            String cardNumber = (String) arguments.get("cardNumber");
            Object transactionAmountObj = arguments.get("transactionAmount");
            Integer format = (Integer) arguments.get("format");
            String encryptionKey = (String) arguments.get("encryptionKey");
            Integer encryptionType = (Integer) arguments.get("encryptionType");
            Boolean useHardwareEncryption = (Boolean) arguments.get("useHardwareEncryption");

            // Validate required parameters
            if (pin == null || cardNumber == null) {
                result.error("INVALID_PARAMS", "PIN and card number are required", null);
                return;
            }

            // Convert transaction amount
            Long transactionAmount = null;
            if (transactionAmountObj != null) {
                if (transactionAmountObj instanceof Integer) {
                    transactionAmount = ((Integer) transactionAmountObj).longValue();
                } else if (transactionAmountObj instanceof Long) {
                    transactionAmount = (Long) transactionAmountObj;
                }
            }

            // Set defaults
            if (format == null) format = DynamicPinBlockManager.PIN_BLOCK_FORMAT_0;
            if (encryptionKey == null) encryptionKey = "404142434445464748494A4B4C4D4E4F";
            if (encryptionType == null) encryptionType = DynamicPinBlockManager.ENCRYPT_3DES;
            if (useHardwareEncryption == null) useHardwareEncryption = true;

            Map<String, Object> authorizePinResult = dynamicPinBlockManager.authorizePin(
                    pin, cardNumber, transactionAmount, format, encryptionKey, encryptionType, useHardwareEncryption);

            result.success(authorizePinResult);

        } catch (Exception e) {
            Log.e(TAG, "Exception in handleAuthorizePinDynamic: " + e.getMessage());
            result.error("AUTHORIZE_PIN_DYNAMIC_EXCEPTION", "Exception: " + e.getMessage(), null);
        }
    }

    private void handleTestAllPinBlockFormats(MethodCall call, Result result) {
        try {
            if (dynamicPinBlockManager == null) {
                result.error("PINPAD_ERROR", "Pinpad manager not available", null);
                return;
            }

            Map<String, Object> arguments = call.arguments();
            String pin = (String) arguments.get("pin");
            String cardNumber = (String) arguments.get("cardNumber");
            String encryptionKey = (String) arguments.get("encryptionKey");

            // Validate required parameters
            if (pin == null || cardNumber == null) {
                result.error("INVALID_PARAMS", "PIN and card number are required", null);
                return;
            }

            // Set default encryption key if not provided
            if (encryptionKey == null) {
                encryptionKey = "404142434445464748494A4B4C4D4E4F";
            }

            Map<String, Object> testResults = dynamicPinBlockManager.testAllFormats(pin, cardNumber, encryptionKey);
            result.success(testResults);

        } catch (Exception e) {
            Log.e(TAG, "Exception in handleTestAllPinBlockFormats: " + e.getMessage());
            result.error("TEST_FORMATS_EXCEPTION", "Exception: " + e.getMessage(), null);
        }
    }

    // Pinpad management methods
    private void handleInitPinpad(Result result) {
        try {
            if (dynamicPinBlockManager == null) {
                result.error("PINPAD_ERROR", "Pinpad manager not available", null);
                return;
            }

            boolean initResult = dynamicPinBlockManager.initPinpad();
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
            if (dynamicPinBlockManager == null) {
                result.error("PINPAD_ERROR", "Pinpad manager not available", null);
                return;
            }

            dynamicPinBlockManager.closePinpad();
            result.success(null);

        } catch (Exception e) {
            Log.e(TAG, "Exception in handleClosePinpad: " + e.getMessage());
            result.error("CLOSE_EXCEPTION", "Exception: " + e.getMessage(), null);
        }
    }

    private void handleGetPinpadStatus(Result result) {
        try {
            if (dynamicPinBlockManager == null) {
                result.error("PINPAD_ERROR", "Pinpad manager not available", null);
                return;
            }

            Map<String, Object> status = dynamicPinBlockManager.getPinpadStatus();
            result.success(status);

        } catch (Exception e) {
            Log.e(TAG, "Exception in handleGetPinpadStatus: " + e.getMessage());
            result.error("STATUS_EXCEPTION", "Exception: " + e.getMessage(), null);
        }
    }

    private void handleLoadMainKey(MethodCall call, Result result) {
        try {
            if (dynamicPinBlockManager == null) {
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

            boolean loadResult = dynamicPinBlockManager.loadMainKey(keyIndex, keyData, checkValue);
            result.success(loadResult);

        } catch (Exception e) {
            Log.e(TAG, "Exception in handleLoadMainKey: " + e.getMessage());
            result.error("LOAD_KEY_EXCEPTION", "Exception: " + e.getMessage(), null);
        }
    }

    private void handleLoadWorkKey(MethodCall call, Result result) {
        try {
            if (dynamicPinBlockManager == null) {
                result.error("PINPAD_ERROR", "Pinpad manager not initialized", null);
                return;
            }

            Map<String, Object> arguments = call.arguments();
            Integer keyType = (Integer) arguments.get("keyType");
            Integer masterKeyId = (Integer) arguments.get("masterKeyId");
            Integer workKeyId = (Integer) arguments.get("workKeyId");
            String keyDataHex = (String) arguments.get("keyData");
            String checkValueHex = (String) arguments.get("checkValue");
            String masterKey = (String) arguments.get("masterKey");

            if (keyType == null || masterKeyId == null || workKeyId == null || keyDataHex == null) {
                result.error("INVALID_PARAMS", "All key parameters are required", null);
                return;
            }

            byte[] keyData = hexToBytes(keyDataHex);
            byte[] checkValue = checkValueHex != null ? hexToBytes(checkValueHex) : null;

            boolean loadResult = dynamicPinBlockManager.loadWorkKey(keyType, masterKeyId, workKeyId, keyData, checkValue, masterKey);
            result.success(loadResult);

        } catch (Exception e) {
            Log.e(TAG, "Exception in handleLoadWorkKey: " + e.getMessage());
            result.error("LOAD_WORK_KEY_EXCEPTION", "Exception: " + e.getMessage(), null);
        }
    }

    private void handleGetKeyState(MethodCall call, Result result) {
        try {
            if (dynamicPinBlockManager == null) {
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

            boolean keyState = dynamicPinBlockManager.getKeyState(keyType, keyIndex);
            result.success(keyState);

        } catch (Exception e) {
            Log.e(TAG, "Exception in handleGetKeyState: " + e.getMessage());
            result.error("KEY_STATE_EXCEPTION", "Exception: " + e.getMessage(), null);
        }
    }

    private void handleGetMac(MethodCall call, Result result) {
        try {
            if (dynamicPinBlockManager == null) {
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

            Map<String, Object> macResult = dynamicPinBlockManager.getMac(param);
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
            if (dynamicPinBlockManager == null) {
                result.error("PINPAD_ERROR", "Pinpad manager not initialized", null);
                return;
            }

            byte[] random = dynamicPinBlockManager.getRandom();
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

        hex = hex.replaceAll("\\s+", "").toUpperCase();
        if (hex.length() % 2 != 0) {
            hex = "0" + hex;
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
        // Stop card reading if active
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

        // Close pinpad
        if (dynamicPinBlockManager != null) {
            dynamicPinBlockManager.closePinpad();
        }

        // Cleanup
        DeviceServiceManagers.getInstance().unBindDeviceService();
        channel.setMethodCallHandler(null);

        // Clear references
        aidlEmvL2 = null;
        cardReader = null;
        dynamicPinBlockManager = null;
        context = null;
        mainHandler = null;
    }
}