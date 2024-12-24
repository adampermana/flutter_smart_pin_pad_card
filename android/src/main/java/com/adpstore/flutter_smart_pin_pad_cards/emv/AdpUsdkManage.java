/*============================================================
 Module Name       : AdpUsdkManage.java
 Date of Creation  : 18/12/2024
 Name of Creator   : Adam Permana
 History of Modifications:
 18/12/2024- Lorem Ipsum

 Summary           :


 Functions         :
 -

 Variables         :
 -

 ============================================================*/

package com.adpstore.flutter_smart_pin_pad_cards.emv;

import android.content.Context;

import com.topwise.cloudpos.aidl.card.AidlCheckCard;
import com.topwise.cloudpos.aidl.cpucard.AidlCPUCard;
import com.topwise.cloudpos.aidl.emv.level2.AidlAmex;
import com.topwise.cloudpos.aidl.emv.level2.AidlDpas;
import com.topwise.cloudpos.aidl.emv.level2.AidlEmvL2;
import com.topwise.cloudpos.aidl.emv.level2.AidlEntry;
import com.topwise.cloudpos.aidl.emv.level2.AidlJcb;
import com.topwise.cloudpos.aidl.emv.level2.AidlMir;
import com.topwise.cloudpos.aidl.emv.level2.AidlPaypass;
import com.topwise.cloudpos.aidl.emv.level2.AidlPaywave;
import com.topwise.cloudpos.aidl.emv.level2.AidlPure;
import com.topwise.cloudpos.aidl.emv.level2.AidlQpboc;
import com.topwise.cloudpos.aidl.emv.level2.AidlRupay;
import com.topwise.cloudpos.aidl.iccard.AidlICCard;
import com.topwise.cloudpos.aidl.magcard.AidlMagCard;
import com.topwise.cloudpos.aidl.pinpad.AidlPinpad;
import com.topwise.cloudpos.aidl.psam.AidlPsam;
import com.topwise.cloudpos.aidl.rfcard.AidlRFCard;
import com.topwise.cloudpos.aidl.serialport.AidlSerialport;
import com.topwise.cloudpos.aidl.shellmonitor.AidlShellMonitor;
import com.topwise.cloudpos.aidl.system.AidlSystem;

public class AdpUsdkManage implements IAdpUsdk {
    private static IAdpUsdk mService;
    private static AdpUsdkManage adpUsdkManage;
    /******* Mode = 0  Pos Mode; Mode = 1  BT  Mode;Mode = 2  USB Mode; **********/
    private static int mMode = 0;

    public static AdpUsdkManage getInstance() {
        if (adpUsdkManage == null) {
            synchronized (AdpUsdkManage.class) {
                if (adpUsdkManage == null) {
                    adpUsdkManage = new AdpUsdkManage();
                }
            }
        }
        return adpUsdkManage;
    }

    @Override
    public String getVersion() {
        return mService.getVersion();
    }

    @Override
    public void init(Context mContext, AdpUsdkManage.InitListener initListener) {
        switch (mMode) {
            case 0:
            default:
                mService = AdpLocalManage.getInstance();
                break;
        }
        mService.init(mContext, initListener);
    }

    @Override
    public AidlEmvL2 getEmv() {
        return mService.getEmv();
    }

    @Override
    public AidlPure getPurePay() {
        return mService.getPurePay();
    }

    @Override
    public AidlPaypass getPaypass() {
        return mService.getPaypass();
    }

    @Override
    public AidlPaywave getPaywave() {
        return mService.getPaywave();
    }

    @Override
    public AidlEntry getEntry() {
        return mService.getEntry();
    }

    @Override
    public AidlAmex getAmexPay() {
        return mService.getAmexPay();
    }

    @Override
    public AidlQpboc getUnionPay() {
        return mService.getUnionPay();
    }

    @Override
    public AidlRupay getRupay() {
        return mService.getRupay();
    }

    @Override
    public AidlMir getMirPay() {
        return mService.getMirPay();
    }

    @Override
    public AidlDpas getDpasPay() {
        return mService.getDpasPay();
    }

    @Override
    public AidlJcb getJcbPay() {
        return mService.getJcbPay();
    }

    @Override
    public AidlSystem getSystem() {
        return mService.getSystem();
    }

    @Override
    public AidlPinpad getPinpad(int type) {
        return mService.getPinpad(type);
    }

    @Override
    public AidlShellMonitor getShellMonitor() {
        return mService.getShellMonitor();
    }

    @Override
    public AidlICCard getIcc() {
        return mService.getIcc();
    }

    @Override
    public AidlRFCard getRf() {
        return mService.getRf();
    }

    @Override
    public AidlMagCard getMag() {
        return mService.getMag();
    }

    @Override
    public AidlPsam getPsam(int devid) {
        return mService.getPsam(devid);
    }

    @Override
    public AidlSerialport getSerialport(int port) {
        return mService.getSerialport(port);
    }

    @Override
    public AidlCPUCard getCpu() {
        return mService.getCpu();
    }

    @Override
    public ICardReader getCardReader() {
        return mService.getCardReader();
    }

    @Override
    public IEmv getEmvHelper() {
        return mService.getEmvHelper();
    }

    @Override
    public AidlCheckCard getCheckCard() {
        return mService.getCheckCard();
    }

    @Override
    public void setMode(int mode) {
        mMode = mode;
    }

    @Override
    public void close() {

    }

    public interface InitListener {
        void OnConnection(boolean ret);
    }
}
