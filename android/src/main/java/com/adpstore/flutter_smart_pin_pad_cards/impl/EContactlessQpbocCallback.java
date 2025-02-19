package com.adpstore.flutter_smart_pin_pad_cards.impl;

import android.os.RemoteException;

import com.adpstore.flutter_smart_pin_pad_cards.AppLog;
import com.topwise.cloudpos.aidl.emv.level2.QpbocCallback;
import com.topwise.cloudpos.struct.BytesUtil;

public class EContactlessQpbocCallback extends QpbocCallback.Stub{
    private static final String TAG = EContactlessQpbocCallback.class.getSimpleName();

    @Override
    public int cCheckExceptionFile(byte[] bytes, int i, byte b) throws RemoteException {
        AppLog.d(TAG,"Call Back cCheckExceptionFile");
        AppLog.d(TAG, "PAN: " + BytesUtil.bytes2HexString(bytes));
        AppLog.d(TAG, "PAN length: " + i);
        AppLog.d(TAG, "PAN sequence no: " + b);
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
}
