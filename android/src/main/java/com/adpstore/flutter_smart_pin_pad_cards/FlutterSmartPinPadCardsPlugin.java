package com.adpstore.flutter_smart_pin_pad_cards;

import androidx.annotation.NonNull;

import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import android.content.Context;

import com.adpstore.flutter_smart_pin_pad_cards.entity.EmvAidParam;
import com.adpstore.flutter_smart_pin_pad_cards.entity.EmvOnlineResp;
import com.adpstore.flutter_smart_pin_pad_cards.entity.EmvPinEnter;
import com.adpstore.flutter_smart_pin_pad_cards.enums.ECVMStatus;
import com.adpstore.flutter_smart_pin_pad_cards.enums.EKernelType;
import com.adpstore.flutter_smart_pin_pad_cards.enums.EOnlineResult;
import com.adpstore.flutter_smart_pin_pad_cards.enums.EPinType;
import com.adpstore.flutter_smart_pin_pad_cards.param.AidParam;
import com.adpstore.flutter_smart_pin_pad_cards.param.CapkParam;
import com.topwise.cloudpos.aidl.emv.AidlCheckCardListener;
import com.topwise.cloudpos.aidl.emv.level2.AidlEmvL2;
import com.topwise.cloudpos.aidl.emv.level2.Combination;
import com.topwise.cloudpos.aidl.emv.level2.EmvCandidateItem;
import com.topwise.cloudpos.aidl.emv.level2.EmvCapk;
import com.topwise.cloudpos.aidl.magcard.TrackData;
import com.adpstore.flutter_smart_pin_pad_cards.entity.CardData;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    @Override
    public void onAttachedToEngine(@NonNull FlutterPlugin.FlutterPluginBinding flutterPluginBinding) {
        context = flutterPluginBinding.getApplicationContext();
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "flutter_smart_pin_pad_cards");
        channel.setMethodCallHandler(this);
        mainHandler = new Handler(Looper.getMainLooper());
        // Initialize device service
        boolean bindResult = DeviceServiceManagers.getInstance().bindDeviceService(context);
        Log.d(TAG, "Device service bind result: " + bindResult);
        // Initialize EMV service and Card Reader
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
                startInsertCardReading(call, result);
                break;
            case "stopInsertCardReading":
                handleStopCardReading(result);
                break;
            default:
                result.notImplemented();
        }
    }


    private void startInsertCardReading(MethodCall call, final Result result) {
        if (cardReader == null) {
            result.error("INIT_ERROR", "Card reader not initialized", null);
            return;
        }

        // Set context dan inisialisasi
        CardReader cardReaderInstance = CardReader.getInstance();
        cardReaderInstance.setContext(context);

        // Initialize transaction process listener
        ITransProcessListener processListener = new ITransProcessListener() {
            @Override
            public int onReqAppAidSelect(String[] aids) {
                // Default: return index 0 if available
                if (aids != null && aids.length > 0) {
                    Log.d(TAG, "Selecting first available AID");
                    return 0;
                }
                return -1;
            }

            @Override
            public void onUpToAppEmvCandidateItem(EmvCandidateItem emvCandidateItem) {
                Log.d(TAG, "Selected candidate item: " + emvCandidateItem.toString());
            }

            @Override
            public void onUpToAppKernelType(EKernelType eKernelType) {
                Log.d(TAG, "Kernel type: " + eKernelType.toString());
            }

            @Override
            public boolean onReqFinalAidSelect() {
                return true;
            }

            @Override
            public EmvOnlineResp onReqOnlineProc() {
                EmvOnlineResp resp = new EmvOnlineResp();
                resp.seteOnlineResult(EOnlineResult.ONLINE_APPROVE);

                // Set auth response code "00" for approval
                byte[] authRespCode = new byte[]{0x30, 0x30}; // "00" in ASCII
                resp.setAuthRespCode(authRespCode);
                resp.setExistAuthRespCode(true);
                return resp;
            }

            @Override
            public List<Combination> onLoadCombinationParam() {
                List<Combination> combinations = new ArrayList<>();
                try {
                    // Add Visa combination
                    Combination visa = new Combination();
                    visa.setUcAidLen((byte) 7);
                    visa.setAucAID(new byte[]{(byte) 0xA0, 0x00, 0x00, 0x00, 0x03, 0x10, 0x10});
                    visa.setUcKernIDLen((byte) 1);
                    visa.setAucKernelID(new byte[]{(byte) 0x03});
                    combinations.add(visa);

                    // Add Mastercard combination
                    Combination mc = new Combination();
                    mc.setUcAidLen((byte) 7);
                    mc.setAucAID(new byte[]{(byte) 0xA0, 0x00, 0x00, 0x00, 0x04, 0x10, 0x10});
                    mc.setUcKernIDLen((byte) 1);
                    mc.setAucKernelID(new byte[]{(byte) 0x02});
                    combinations.add(mc);

                } catch (Exception e) {
                    Log.e(TAG, "Error creating combinations: " + e.getMessage());
                }
                return combinations;
            }

            @Override
            public EmvAidParam onFindCurAidParamProc(String sAid) {
                return AidParam.getCurrentAidParam(sAid);
            }

            @Override
            public boolean onConfirmCardInfo(String cardNo) {
                Log.d(TAG, "Confirming card number: " + cardNo);
                return true;
            }

            @Override
            public EmvPinEnter onReqGetPinProc(EPinType pinType, int leftTimes) {
                EmvPinEnter pinEnter = new EmvPinEnter();
                pinEnter.setEcvmStatus(ECVMStatus.ENTER_BYPASS);
                return pinEnter;
            }

            @Override
            public boolean onDisplayPinVerifyStatus(int PinTryCounter) {
                Log.d(TAG, "PIN try counter: " + PinTryCounter);
                return true;
            }

            @Override
            public boolean onReqUserAuthProc(int certype, String certnumber) {
                return true;
            }

            @Override
            public boolean onSecCheckCardProc() {
                return false;
            }

            @Override
            public void onRemoveCardProc() {
                Log.d(TAG, "Card removal requested");
            }

            @Override
            public EmvCapk onFindIssCapkParamProc(String sAid, byte bCapkIndex) {
                try {
                    CapkParam capkParam = new CapkParam();
                    capkParam.init(context);
                    capkParam.saveAll();
                    // Menggunakan getEmvCapkParam yang benar
                    return CapkParam.getEmvCapkParam(sAid, bCapkIndex);
                } catch (Exception e) {
                    Log.e(TAG, "Error loading CAPK: " + e.getMessage());
                    return null;
                }
            }
        };

        // Set listener ke emv helper
        IEmv emvHelper = DeviceServiceManagers.getInstance().getEmvHelper();
        emvHelper.setProcessListener(processListener);


        Map<String, Object> arguments = call.arguments();
        boolean enableMag = (boolean) arguments.get("enableMag");
        boolean enableIcc = (boolean) arguments.get("enableIcc");
        boolean enableRf = (boolean) arguments.get("enableRf");
        int timeout = (int) arguments.get("timeout");
        isCardReading = true;
        pendingResult = result;
        cardReader.startFindCard(enableMag, enableIcc, enableRf, timeout, new CardReader.onReadCardListener() {
            @Override
            public void getReadState(CardData cardData) {
                if (cardData.getEreturnType() == CardData.EReturnType.OK) {
                    Map<String, Object> resultMap = new HashMap<>();
                    resultMap.put("cardType", cardData.getEcardType().toString());
                    switch (cardData.getEcardType()) {
                        case MAG:
                            resultMap.put("track1", cardData.getTrack1());
                            resultMap.put("track2", cardData.getTrack2());
                            resultMap.put("track3", cardData.getTrack3());
                            resultMap.put("pan", cardData.getPan());
                            resultMap.put("expiryDate", cardData.getExpiryDate());
                            resultMap.put("serviceCode", cardData.getServiceCode());
                            break;
                        case IC:
                            // Handle IC card specific data if needed
                            resultMap.put("pan", cardData.getPan());
                            resultMap.put("expiryDate", cardData.getExpiryDate());
                            resultMap.put("serviceCode", cardData.getServiceCode());
                            resultMap.put("track1", cardData.getTrack1());
                            resultMap.put("track2", cardData.getTrack2());
                            break;
                        case RF:
                            // Handle RF card specific data if needed
                            break;
                    }
                    mainHandler.post(() -> {
                        isCardReading = false;
                        if (pendingResult != null) {
                            pendingResult.success(resultMap);
                            pendingResult = null;
                        }
                    });
                } else {
                    mainHandler.post(() -> {
                        sendError("READ_ERROR", "Failed to read card: " + cardData.getEreturnType().toString());
                    });
                }
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
            initializeServices();
            if (aidlEmvL2 == null) {
                result.error("START_ERROR", "Card reader hardware not available", null);
                return;
            }
        }
        try {
            isCardReading = true;
            pendingResult = result;
            // Swipe Card
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
        DeviceServiceManagers.getInstance().unBindDeviceService();
        channel.setMethodCallHandler(null);
        aidlEmvL2 = null;
        cardReader = null;
        context = null;
        mainHandler = null;
    }
}