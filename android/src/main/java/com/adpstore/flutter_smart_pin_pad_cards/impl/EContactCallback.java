package com.adpstore.flutter_smart_pin_pad_cards.impl;

import android.os.RemoteException;
import android.text.TextUtils;

import com.adpstore.flutter_smart_pin_pad_cards.AppLog;
import com.adpstore.flutter_smart_pin_pad_cards.DeviceServiceManagers;
import com.adpstore.flutter_smart_pin_pad_cards.ITransProcessListener;
import com.adpstore.flutter_smart_pin_pad_cards.entity.EmvPinEnter;
import com.adpstore.flutter_smart_pin_pad_cards.enums.ECVMStatus;
import com.adpstore.flutter_smart_pin_pad_cards.enums.EPinType;
import com.topwise.cloudpos.aidl.emv.level2.AidlEmvL2;
import com.topwise.cloudpos.aidl.emv.level2.EmvCallback;
import com.topwise.cloudpos.struct.BytesUtil;

/**
 * Contact emv process callback
 */
public class EContactCallback extends EmvCallback.Stub {
    private static final String TAG = EContactCallback.class.getSimpleName();
    private AidlEmvL2 emvL2 = DeviceServiceManagers.getInstance().getEmvL2();
    private ITransProcessListener emvProcessListener;
    private int pinRetryTimes;

    public EContactCallback(ITransProcessListener emvProcessListener) {
        this.emvProcessListener = emvProcessListener;
    }

    /***
     *  Require online PIN processing
     * @param b : [IN] Is allow PIN entry bypass ?
     * @param bytes : [IN] PAN, The value of 5A
     * @param i : [IN ] PAN length,  The length of 5A
     * @param booleans : [OUT]  PIN entry bypassed ?
     * @return 1: get pin ok
     *         0: get pin err
     * @throws RemoteException
     */
    @Override
    public int cGetOnlinePin(boolean b, byte[] bytes, int i, boolean[] booleans) throws RemoteException {
        AppLog.d(TAG, "cGetOnlinePin Is allow PIN entry bypass ?: " + b);
        AppLog.d(TAG, "cGetOnlinePin PAN: " + BytesUtil.bytes2HexString(bytes));
        AppLog.d(TAG, "cGetOnlinePin PAN length: " + i);
        int nRet = 0x00;
        //EmvErrorCode.PINTYPE_ONLINE
        EmvPinEnter emvPinEnter = emvProcessListener.onReqGetPinProc(EPinType.ONLINE_PIN_REQ, 0x00);
        AppLog.d(TAG, "cGetOnlinePin requestImportPin: " + emvPinEnter.toString());
        if (ECVMStatus.ENTER_BYPASS == emvPinEnter.getEcvmStatus() && b) {
            booleans[0] = true;  //Bypassed
            nRet = 0x01;
        } else if (ECVMStatus.ENTER_OK == emvPinEnter.getEcvmStatus()) {
            booleans[0] = false;
            nRet = 0x01;
        } else {
            booleans[0] = false;
            nRet = 0x00;
        }
        return nRet;
    }

    /***
     * Require offline PIN processing
     * @param b :  [IN] Is allow PIN entry bypass ?
     * @param bytes : [OUT] The plaintext offline PIN block
     * @param i : [IN] PIN block buffer size
     * @param booleans : [OUT]  PIN entry bypassed ?
     * @return 1: Bypassed or Successfully entered
     *         0: No entry PIN or PIN pad is malfunctioning
     * @throws RemoteException
     */
    @Override
    public int cGetPlainTextPin(boolean b, byte[] bytes, int i, boolean[] booleans) throws RemoteException {
        AppLog.emvd(TAG,"cGetPlainTextPin kernel sup bypass ?= "+b);
        int nRet = 0;
        byte bPCIPINEntry = 0;
        int nLastTime = 0;
        bPCIPINEntry = emvL2.EMV_GetKernelConfig().getbPCIPINEntry();
        AppLog.d(TAG, "bPCIPINEntry: " + bPCIPINEntry);

        AppLog.emvd(TAG, "cGetPlainTextPin pinRetryTimes = " + pinRetryTimes);
        if (pinRetryTimes == 0) {
            byte[] pinTryCnt = emvL2.EMV_GetTLVData(0x9F17);
            AppLog.emvd(TAG, "cGetPlainTextPin TAG 9F17 = " + pinTryCnt[0]);
            nLastTime = pinTryCnt[0];
        } else {
            nLastTime =pinRetryTimes;
        }
        AppLog.emvd(TAG, "cGetPlainTextPin nLastTime = " + nLastTime);

        EmvPinEnter emvPinEnter = emvProcessListener.onReqGetPinProc(EPinType.OFFLINE_PLAIN_TEXT_PIN_REQ, nLastTime);
        AppLog.d(TAG, "cGetPlainTextPin requestImportPin: " + emvPinEnter.toString());
        if (ECVMStatus.ENTER_BYPASS == emvPinEnter.getEcvmStatus() && b) {
            // kernel sup bypass and app bypass
            booleans[0] = true;
            nRet = 0x01;
        } else if (ECVMStatus.ENTER_OK == emvPinEnter.getEcvmStatus() && !TextUtils.isEmpty(emvPinEnter.getPlainTextPin())) {
            booleans[0] = false;
            nRet = 0x01;

            // 非PIN模式才需要计算OfflinePinBlock.  -by wxz 20230418
            if (bPCIPINEntry == 0) {
                byte[] offlinePinBlock = getOfflinePinBlock(emvPinEnter.getPlainTextPin());
                System.arraycopy(offlinePinBlock, 0, bytes, 0, offlinePinBlock.length);
                AppLog.d(TAG, "cGetPlainTextPin getOfflinePinBlock(): " + BytesUtil.bytes2HexString(offlinePinBlock));
            }
        } else {
            booleans[0] = false;
            nRet = 0x00;
        }
        AppLog.d(TAG, "cGetPlainTextPin nRet: " + nRet + " bypass: " + booleans[0]);
        return nRet;
    }

    @Override
    public int cDisplayPinVerifyStatus(int i) throws RemoteException {
        AppLog.d(TAG,"cDisplayPinVerifyStatus");
        AppLog.d(TAG, "The number of remaining PIN tries: " + i);
        int nRet = 0;
        if (emvProcessListener != null) {
            boolean isContinue = emvProcessListener.onDisplayPinVerifyStatus(i);
            AppLog.e(TAG, "onDisplayPinVerifyStatus isContinue: " + isContinue);
            if (isContinue) {
                nRet = 0x01;
            } else {
                nRet = 0x00;
            }
        }
        pinRetryTimes = i;
        AppLog.e(TAG, "cDisplayPinVerifyStatus nRet: " + nRet);
        return nRet;
    }

    @Override
    public int cCheckCredentials(int i, byte[] bytes, int i1, boolean[] booleans) throws RemoteException {
        return 0;
    }

    @Override
    public int cIssuerReferral(byte[] bytes, int i) throws RemoteException {
        return 0;
    }

    @Override
    public int cGetTransLogAmount(byte[] bytes, int i, int i1) throws RemoteException {
        return 0;
    }

    @Override
    public int cCheckExceptionFile(byte[] bytes, int i, int i1) throws RemoteException {
        return 0;
    }

    @Override
    public int cRFU1() throws RemoteException {
        return 0;
    }

    @Override
    public int cRFU2() throws RemoteException {
        return 0;
    }

    @Override
    public int cRFU3() throws RemoteException {
        return 0;
    }

    @Override
    public int cRFU4() throws RemoteException {
        return 0;
    }

    /**
     *
     * @param pin
     * @return
     */
    private byte[] getOfflinePinBlock(String pin) {
        AppLog.d(TAG, "Into getOfflinePinBlock()");
        String pinTemp = "";

        if (pin == null) {
            return null;
        }

        AppLog.d(TAG, "pin: " + pin);
        if ((pin.length() == 0) || (pin.length() > 14)) {
            return null;
        }

        pinTemp = "";
        for (int i = 0 ; i < pin.length(); i++) {
            if ((pin.charAt(i) >= '0') && (pin.charAt(i) <= '9')) {
                pinTemp += pin.charAt(i);
            }
        }
        AppLog.d(TAG, "pinTemp: " + pinTemp);

        String strBlock = new String();
        strBlock += "2";
        strBlock += String.format("%X", pinTemp.length());
        strBlock += pinTemp;
        AppLog.emvd(TAG, "strBlock: " + strBlock);

        while (strBlock.length() < 16) {
            strBlock = strBlock.concat("F");
        }

        AppLog.d(TAG, "strBlock: " + strBlock);
        return BytesUtil.hexString2Bytes(strBlock);
    }
}
