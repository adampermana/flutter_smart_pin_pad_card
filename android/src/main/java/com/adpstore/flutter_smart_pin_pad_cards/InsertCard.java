//package com.adpstore.flutter_smart_pin_pad_cards;
//
//import android.app.Activity;
//import android.content.Context;
//import android.os.Handler;
//import android.os.Looper;
//import android.text.TextUtils;
//import android.util.Log;
//
//import com.adpstore.flutter_smart_pin_pad_cards.entity.CardData;
//import com.adpstore.flutter_smart_pin_pad_cards.entity.EinputType;
//import com.adpstore.flutter_smart_pin_pad_cards.entity.EmvOutCome;
//import com.adpstore.flutter_smart_pin_pad_cards.entity.EmvTransPraram;
//import com.adpstore.flutter_smart_pin_pad_cards.entity.TransData;
//import com.adpstore.flutter_smart_pin_pad_cards.impl.TransProcess;
//import com.adpstore.flutter_smart_pin_pad_cards.param.AppCombinationHelper;
//import com.adpstore.flutter_smart_pin_pad_cards.param.SysParam;
//import com.topwise.cloudpos.aidl.emv.level2.EmvKernelConfig;
//import com.topwise.cloudpos.aidl.emv.level2.EmvTerminalInfo;
//import com.topwise.cloudpos.aidl.pinpad.AidlPinpad;
//
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//
//public class InsertCard extends Activity {
//    private static final String TAG = "InsertCard";
//    private static final int SEARCH_CARD_TIME = 60000;
//
//    private final Context context;
//    private final Handler handler;
//    private ICardReader cardReader;
//    private TransData transData;
//    private IEmv emv;
//
//    private AidlPinpad pinpad;     // Add this as a class field
//
//    private CardReaderCallback callback;
//
//    public interface CardReaderCallback {
//        void onMessage(String message);
//
//        void onCardDetected(Map<String, Object> cardData);
//
//        void onTransactionResult(boolean success, String message, Map<String, Object> data);
//    }
//
//    public InsertCard(Context context, CardReaderCallback callback) {
//        this.context = context;
//        this.callback = callback;
//        this.handler = new Handler(Looper.getMainLooper());
//    }
//
//    public void startCardReader(boolean enableMag, boolean enableIcc, boolean enableRf, int timeout) {
//        Log.d(TAG, "startCardReader() Please INSERT / TAP CARD");
//        initializeTransaction();
//        TransProcess.getInstance().preInit(AppCombinationHelper.getInstance().getAppCombinationList());
//        cardReader = CardReader.getInstance();
//
//        cardReader.startFindCard(enableMag, enableIcc, enableRf, timeout, new CardReader.onReadCardListener() {
//            @Override
//            public void getReadState(CardData cardData) {
//                if (cardData != null && CardData.EReturnType.OK == cardData.getEreturnType()) {
//                    handleCardDetected(cardData);
//                } else {
//                    cancelCardReader();
//                    sendMessage("Card reading failed");
//                }
//            }
//
//            @Override
//            public void onNotification(CardData.EReturnType eReturnType) {
//                sendMessage("Card reader notification: " + eReturnType);
//            }
//        });
//    }
//
//    private void handleCardDetected(CardData cardData) {
//        Map<String, Object> resultData = new HashMap<>();
//        resultData.put("cardType", cardData.getEcardType().name());
//
//        switch (cardData.getEcardType()) {
//            case IC:
//                transData.setEnterMode(EinputType.CT);
//                resultData.put("entryMode", "CT");
//                callback.onCardDetected(resultData);
//                startEmvTransaction();
//                break;
//            case RF:
//                transData.setEnterMode(EinputType.CTL);
//                resultData.put("entryMode", "CTL");
//                callback.onCardDetected(resultData);
//                startEmvTransaction();
//                break;
//            case MAG:
//                resultData.put("entryMode", "MAG");
//                callback.onCardDetected(resultData);
//                // Handle magnetic card processing
//                break;
//        }
//    }
//
//    private void startEmvTransaction() {
//        EmvTerminalInfo terminalInfo = EmvResultUtlis.setEmvTerminalInfo();
//        setupTerminalInfo(terminalInfo);
//
//        EmvTransPraram transParam = createTransactionParameters();
//        EmvKernelConfig kernelConfig = EmvResultUtlis.setEmvKernelConfig();
//
//        emv = SmartPosApplication.usdkManage.getEmvHelper();
//        emv.init(transData.getEnterMode());
//        emv.setProcessListener(new EmvTransProcessImpl(context, transData, emv, handler));
//        emv.setTerminalInfo(terminalInfo);
//        emv.setTransPraram(transParam);
//        emv.setKernelConfig(kernelConfig);
//
//        EmvOutCome outcome = emv.StartEmvProcess();
//        handleEmvOutcome(outcome);
//    }
//
//    private void setupTerminalInfo(EmvTerminalInfo terminalInfo) {
//        if (transData.getEnterMode() == EinputType.CT) {
//            terminalInfo.setUcTerminalEntryMode((byte) 0x05);
//        } else {
//            terminalInfo.setUcTerminalEntryMode((byte) 0x07);
//        }
//    }
//
//    private EmvTransPraram createTransactionParameters() {
//        EmvTransPraram param = new EmvTransPraram(EmvTags.checkKernelTransType(transData));
//
//        Calendar calendar = Calendar.getInstance();
//        String year = String.format("%04d", calendar.get(Calendar.YEAR));
//        param.setAucTransDate(year.substring(2) + transData.getDate());
//        param.setAucTransTime(transData.getTime());
//        param.setTransNo(transData.getTransNo());
//
//        String amount = TextUtils.isEmpty(transData.getAmount()) ? "0" : transData.getAmount();
//        String amountOther = TextUtils.isEmpty(transData.getCashAmount()) ? "0" : transData.getCashAmount();
//
//        param.setAmount(Long.parseLong(amount));
//        param.setAmountOther(Long.parseLong(amountOther));
//        param.setAucTransCurCode(SysParam.COUNTRY_CODE);
//
//        return param;
//    }
//
//    private void handleEmvOutcome(EmvOutCome outcome) {
//        Map<String, Object> resultData = new HashMap<>();
//        resultData.put("status", outcome.geteTransStatus().name());
//
//        switch (outcome.geteTransStatus()) {
//            case ONLINE_APPROVE:
//            case OFFLINE_APPROVE:
//                callback.onTransactionResult(true, "Transaction approved", resultData);
//                break;
//            case ONLINE_REQUEST:
//                callback.onTransactionResult(true, "Online authorization required", resultData);
//                break;
//            default:
//                callback.onTransactionResult(false, "Transaction failed", resultData);
//                break;
//        }
//    }
//
//    public void cancelCardReader() {
//        if (cardReader != null) {
//            cardReader.cancel();
//        }
//    }
//
//    private void initializeTransaction() {
//        transData = new TransData();
//        // Set default transaction parameters
//        transData.setMerchID("123456789012345");
//        transData.setTermID("12345678");
//        transData.setTransNo(123456);
//        transData.setBatchNo(1);
//        transData.setDate(getDate().substring(4));
//        transData.setTime(getTime());
//        transData.setAmount("10000");
//        transData.setDatetime(getDatetime());
//    }
//
//    private void sendMessage(String message) {
//        handler.post(() -> callback.onMessage(message));
//    }
//
//    // Utility methods for date/time
//    private String getDatetime() {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//        return dateFormat.format(new Date());
//    }
//
//    private static String getDate() {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
//        return dateFormat.format(new Date());
//    }
//
//    private static String getTime() {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("HHmmss");
//        return dateFormat.format(new Date());
//    }
//}