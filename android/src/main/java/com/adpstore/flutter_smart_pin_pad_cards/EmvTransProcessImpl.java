package com.adpstore.flutter_smart_pin_pad_cards;

import android.content.Context;
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.adpstore.flutter_smart_pin_pad_cards.entity.EinputType;
import com.adpstore.flutter_smart_pin_pad_cards.entity.EmvAidParam;
import com.adpstore.flutter_smart_pin_pad_cards.entity.EmvOnlineResp;
import com.adpstore.flutter_smart_pin_pad_cards.entity.EmvPinEnter;
import com.adpstore.flutter_smart_pin_pad_cards.entity.TransData;
import com.adpstore.flutter_smart_pin_pad_cards.entity.TransResult;
import com.adpstore.flutter_smart_pin_pad_cards.enums.ECVMStatus;
import com.adpstore.flutter_smart_pin_pad_cards.enums.EKernelType;
import com.adpstore.flutter_smart_pin_pad_cards.enums.EOnlineResult;
import com.adpstore.flutter_smart_pin_pad_cards.enums.EPinType;
import com.adpstore.flutter_smart_pin_pad_cards.impl.ETransProcessListenerImpl;
import com.adpstore.flutter_smart_pin_pad_cards.param.AidParam;
import com.adpstore.flutter_smart_pin_pad_cards.param.AppCombinationHelper;
import com.adpstore.flutter_smart_pin_pad_cards.param.CapkParam;
import com.adpstore.flutter_smart_pin_pad_cards.transmit.Online;
import com.topwise.cloudpos.aidl.emv.level2.Combination;
import com.topwise.cloudpos.aidl.emv.level2.EmvCapk;
import com.topwise.cloudpos.aidl.pinpad.AidlPinpad;
import com.topwise.cloudpos.aidl.pinpad.GetPinListener;
import com.topwise.cloudpos.aidl.pinpad.PinParam;
import com.topwise.cloudpos.data.PinpadConstant;
import com.topwise.cloudpos.struct.BytesUtil;
import com.topwise.toptool.api.convert.IConvert;
import com.topwise.toptool.impl.TopTool;

import io.flutter.plugin.common.MethodChannel;

import java.util.ArrayList;
import java.util.List;

public class EmvTransProcessImpl extends ETransProcessListenerImpl {
    private static final String TAG = EmvTransProcessImpl.class.getSimpleName();
    public static final int SHOW = 0x99;

    private AidlPinpad mPinpad;
    private Context context;
    private TransData transData;
    private ConditionVariable cv;
    private boolean isConfirm = false;
    private int intResult;
    private IEmv emv;
    private MethodChannel channel;

    private Handler handler;

    private IConvert convert = TopTool.getInstance().getConvert();

    public EmvTransProcessImpl(Context context, TransData transData, IEmv emv, Handler handler) {
        this.context = context;
        this.transData = transData;
        this.emv = emv;
//        this.mPinpad = pinpad;
//        this.channel = channel;
    }

    private void sendMessage(String message) {
        if (channel != null) {
            channel.invokeMethod("showMessage", message);
        }
    }

    @Override
    public int onReqAppAidSelect(final String[] aids) {
        AppLog.d(TAG, "requestAidSelect = ");
        cv = new ConditionVariable();
        intResult = 0;

        if (channel != null) {
            channel.invokeMethod("selectAid", aids, new MethodChannel.Result() {
                @Override
                public void success(Object result) {
                    if (result instanceof Integer) {
                        intResult = (Integer) result;
                    }
                    if (cv != null) {
                        cv.open();
                    }
                }

                @Override
                public void error(String errorCode, String errorMessage, Object errorDetails) {
                    AppLog.e(TAG, "selectAid error: " + errorMessage);
                    if (cv != null) {
                        cv.open();
                    }
                }

                @Override
                public void notImplemented() {
                    AppLog.e(TAG, "selectAid not implemented");
                    if (cv != null) {
                        cv.open();
                    }
                }
            });
        }

        if (cv != null) {
            cv.block();
        }
        AppLog.d(TAG, "intResult = " + intResult);
        return intResult;
    }

    @Override
    public void onUpToAppKernelType(EKernelType kernelType) {
        transData.setKernelType(kernelType.getKernelID());
        sendMessage("Kernel Type: " + kernelType.name());
    }

    @Override
    public boolean onReqFinalAidSelect() {
        AppLog.d(TAG, "finalAidSelect = ");
        byte[] aucAid;
        String aid = null;
        byte[] aucRandom;
        String random = null;
        aucAid = emv.getTlv(0x4F);
        aucRandom = emv.getTlv(0x9f37);
        if (aucAid != null) {
            aid = BytesUtil.bytes2HexString(aucAid);
            random = BytesUtil.bytes2HexString(aucRandom);
            transData.setAid(aid);
            transData.setRandom(random);
            Log.d(TAG, "aid: " + aid);
            return true;
        }
        return false;
    }

    @Override
    public boolean onConfirmCardInfo(String cardNo) {
        transData.setPan(cardNo);
        sendMessage("Card: " + cardNo);

        if (transData.getEnterMode() == EinputType.CTL) {
            return true;
        }

        cv = new ConditionVariable();
        if (channel != null) {
            channel.invokeMethod("confirmCard", cardNo, new MethodChannel.Result() {
                @Override
                public void success(Object result) {
                    isConfirm = (Boolean) result;
                    if (cv != null) {
                        cv.open();
                    }
                }

                @Override
                public void error(String errorCode, String errorMessage, Object errorDetails) {
                    isConfirm = false;
                    if (cv != null) {
                        cv.open();
                    }
                }

                @Override
                public void notImplemented() {
                    isConfirm = true;
                    if (cv != null) {
                        cv.open();
                    }
                }
            });
        }

        if (cv != null) {
            cv.block();
        }
        return isConfirm;
    }

    @Override
    public EmvPinEnter onReqGetPinProc(final EPinType pinType, int pinTryCount) {
        sendMessage("Please Input Pin");
        EmvPinEnter emvPinEnter = new EmvPinEnter();
        cv = new ConditionVariable();

        try {
            mPinpad.setPinKeyboardMode(1);
            PinParam pinParam = new PinParam(
                    ConfiUtils.pinIndex,
                    pinType.getType(),
                    transData.getPan(),
                    PinpadConstant.KeyType.KEYTYPE_PEK,
                    "0,4,5,6");

            mPinpad.getPin(pinParam.getParam(), new GetPinListener.Stub() {
                @Override
                public void onInputKey(int len, String msg) {
                    sendMessage(msg + " Len:" + len);
                }

                @Override
                public void onError(int errorCode) {
                    sendMessage("Pin Error :" + errorCode);
                    emvPinEnter.setEcvmStatus(ECVMStatus.ENTER_CANCEL);
                    cv.open();
                }

                @Override
                public void onConfirmInput(byte[] pin) {
                    if (pin == null || pin.length == 0) {
                        sendMessage("Pin Bypass");
                        emvPinEnter.setEcvmStatus(ECVMStatus.ENTER_BYPASS);
                        transData.setHasPin(false);
                    } else {
                        String pinblock = convert.bcdToStr(pin);
                        sendMessage("PinBlock: " + pinblock);
                        emvPinEnter.setEcvmStatus(ECVMStatus.ENTER_OK);
                        if (pinType == EPinType.ONLINE_PIN_REQ) {
                            transData.setHasPin(true);
                            transData.setPinblock(pinblock);
                        } else {
                            transData.setHasPin(false);
                            emvPinEnter.setPlainTextPin(pinblock);
                        }
                    }
                    cv.open();
                }

                @Override
                public void onCancelKeyPress() {
                    sendMessage("Pin Cancel");
                    emvPinEnter.setEcvmStatus(ECVMStatus.ENTER_CANCEL);
                    cv.open();
                }

                @Override
                public void onStopGetPin() {
                    Log.i(TAG, "onStopGetPin");
                }

                @Override
                public void onTimeout() {
                    Log.i(TAG, "get onTimeout");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            emvPinEnter.setEcvmStatus(ECVMStatus.ENTER_CANCEL);
            cv.open();
        }

        cv.block();
        return emvPinEnter;
    }

    // Rest of your implementation remains the same
    // Including onReqOnlineProc(), onLoadCombinationParam(), etc.

    // Helper methods like saveCardInfoAndCardSeq(), saveTvrTsi(), etc.
}