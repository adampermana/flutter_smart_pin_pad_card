package com.adpstore.flutter_smart_pin_pad_cards;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.adpstore.flutter_smart_pin_pad_cards.entity.CardData;
import com.adpstore.flutter_smart_pin_pad_cards.entity.EinputType;
import com.adpstore.flutter_smart_pin_pad_cards.entity.EmvOutCome;
import com.adpstore.flutter_smart_pin_pad_cards.entity.EmvTransPraram;
import com.adpstore.flutter_smart_pin_pad_cards.entity.TransData;
import com.adpstore.flutter_smart_pin_pad_cards.impl.TransProcess;
import com.adpstore.flutter_smart_pin_pad_cards.param.AppCombinationHelper;
import com.adpstore.flutter_smart_pin_pad_cards.param.SysParam;
import com.topwise.cloudpos.aidl.emv.level2.EmvKernelConfig;
import com.topwise.cloudpos.aidl.emv.level2.EmvTerminalInfo;
import com.topwise.cloudpos.aidl.pinpad.AidlPinpad;
import java.util.Map;
import java.util.HashMap;

public class InsertCardManager {
    private static final String TAG = "InsertCardManager";
    private static final int SEARCH_CARD_TIME = 60000;

    private final Context context;
    private final Handler handler;
    private ICardReader cardReader;
    private TransData transData;
    private IEmv emv;
    private CardReaderCallback callback;

    public interface CardReaderCallback {
        void onMessage(String message);
        void onCardDetected(Map<String, Object> cardData);
        void onTransactionResult(boolean success, String message, Map<String, Object> data);
    }

    public InsertCardManager(Context context, CardReaderCallback callback) {
        this.context = context;
        this.callback = callback;
        this.handler = new Handler(Looper.getMainLooper());
    }

    public void startCardReader(boolean enableIcc, int timeout) {
        Log.d(TAG, "startCardReader() Please INSERT CARD");
        initializeTransaction();
        TransProcess.getInstance().preInit(AppCombinationHelper.getInstance().getAppCombinationList());
        cardReader = CardReader.getInstance();

        // Start card reader with only ICC enabled
        cardReader.startFindCard(false, enableIcc, false, timeout, new CardReader.onReadCardListener() {
            @Override
            public void getReadState(CardData cardData) {
                if (cardData != null && CardData.EReturnType.OK == cardData.getEreturnType()) {
                    handleCardDetected(cardData);
                } else {
                    cancelCardReader();
                    sendMessage("Card reading failed");
                }
            }

            @Override
            public void onNotification(CardData.EReturnType eReturnType) {
                sendMessage("Card reader notification: " + eReturnType);
            }
        });
    }

    private void handleCardDetected(CardData cardData) {
        Map<String, Object> resultData = new HashMap<>();

        if (cardData.getEcardType() == CardData.ECardType.IC) {
            transData.setEnterMode(EinputType.CT);
            resultData.put("entryMode", "CT");
            resultData.put("cardType", "IC");
            callback.onCardDetected(resultData);
            startEmvTransaction();
        } else {
            cancelCardReader();
            sendMessage("Invalid card type. Please use a chip card.");
        }
    }

    private void startEmvTransaction() {
        EmvTerminalInfo terminalInfo = EmvResultUtlis.setEmvTerminalInfo();
        setupTerminalInfo(terminalInfo);

        EmvTransPraram transParam = createTransactionParameters();
        EmvKernelConfig kernelConfig = EmvResultUtlis.setEmvKernelConfig();

        emv = SmartPosApplication.usdkManage.getEmvHelper();
        emv.init(transData.getEnterMode());
        emv.setProcessListener(new EmvTransProcessImpl(context, transData, emv, handler));
        emv.setTerminalInfo(terminalInfo);
        emv.setTransPraram(transParam);
        emv.setKernelConfig(kernelConfig);

        EmvOutCome outcome = emv.StartEmvProcess();
        handleEmvOutcome(outcome);
    }

    private void setupTerminalInfo(EmvTerminalInfo terminalInfo) {
        terminalInfo.setUcTerminalEntryMode((byte) 0x05); // Contact IC card mode
    }

    private EmvTransPraram createTransactionParameters() {
        EmvTransPraram param = new EmvTransPraram(EmvTags.checkKernelTransType(transData));
        // Set transaction parameters like date, time, amount, etc.
        param.setTransNo(transData.getTransNo());
        param.setAmount(Long.parseLong(transData.getAmount()));
        param.setAucTransCurCode(SysParam.COUNTRY_CODE);
        return param;
    }

    private void handleEmvOutcome(EmvOutCome outcome) {
        Map<String, Object> resultData = new HashMap<>();
        resultData.put("status", outcome.geteTransStatus().name());

        switch (outcome.geteTransStatus()) {
            case ONLINE_APPROVE:
            case OFFLINE_APPROVE:
                callback.onTransactionResult(true, "Transaction approved", resultData);
                break;
            case ONLINE_REQUEST:
                callback.onTransactionResult(true, "Online authorization required", resultData);
                break;
            default:
                callback.onTransactionResult(false, "Transaction failed", resultData);
                break;
        }
    }

    public void cancelCardReader() {
        if (cardReader != null) {
            cardReader.cancel();
        }
    }

    private void initializeTransaction() {
        transData = new TransData();
        // Set default transaction parameters
        transData.setMerchID("123456789012345");
        transData.setTermID("12345678");
        transData.setTransNo(123456);
        transData.setBatchNo(1);
        transData.setAmount("10000"); // Example amount
    }

    private void sendMessage(String message) {
        handler.post(() -> callback.onMessage(message));
    }
}