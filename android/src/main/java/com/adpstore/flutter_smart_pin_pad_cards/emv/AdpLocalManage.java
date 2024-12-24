/*============================================================
 Module Name       : AdpLocalManage.java
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

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.topwise.cloudpos.aidl.AidlDeviceService;
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

import java.lang.reflect.Method;

public class AdpLocalManage implements IAdpUsdk {
    private static final String TAG = AdpUsdkManage.class.getSimpleName();
    private static String DEVICE_SERVICE_PACKAGE_NAME = "com.android.topwise.topusdkservice";
    private static String DEVICE_SERVICE_CLASS_NAME = "com.android.topwise.topusdkservice.service.DeviceService";
    private static String ACTION_DEVICE_SERVICE = "topwise_cloudpos_device_service";
    /**
     * singleton
     */
    private static AdpLocalManage mService;
    private static final String version = "V1.0.10";
    private static Context mContext;
    private static AidlDeviceService mDeviceService;

    private AdpUsdkManage.InitListener mListener;


    public AdpLocalManage() {
        Log.e(TAG, "AdpLocalManage version = " + version);
    }

    public static IAdpUsdk getInstance() {
        if (mService == null) {
            synchronized (AdpLocalManage.class) {
                if (mService == null) {
                    mService = new AdpLocalManage();
                }
            }
        }
        return mService;
    }

    public synchronized boolean bindDeviceService() {
        Log.i(TAG, "bindDeviceService");
        if (getDeviceService()) {
            if (mListener != null) {
                mListener.OnConnection(true);
            }
            return true;
        }
        Intent intent = new Intent();
        intent.setAction(ACTION_DEVICE_SERVICE);
        intent.setClassName(DEVICE_SERVICE_PACKAGE_NAME, DEVICE_SERVICE_CLASS_NAME);

        try {
            boolean bindResult = mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            AppLog.d(TAG, "bindResult bindResult = " + bindResult);
            return bindResult;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public void unBindDeviceService() {
        Log.i(TAG, "unBindDeviceService");
        try {
            mContext.unbindService(mConnection);
        } catch (Exception e) {
            Log.i(TAG, "unbind DeviceService service failed : " + e);
        }
    }

    private final ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mDeviceService = AidlDeviceService.Stub.asInterface(service);
            AppLog.d(TAG, "gz mDeviceService" + mDeviceService);
            if (mListener != null) {
                mListener.OnConnection(true);
                mListener = null;
            }
        }

        public void onServiceDisconnected(ComponentName componentName) {
            AppLog.d(TAG, "onServiceDisconnected  :  " + mDeviceService);
            mDeviceService = null;
            if (mListener != null) {
                mListener.OnConnection(false);
                mListener = null;
            }
        }
    };

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public void init(Context mContext, AdpUsdkManage.InitListener initListener) {
        Log.e(TAG, "bindDeviceService");
        this.mContext = mContext;
        mListener = initListener;
        bindDeviceService();

    }

    private IBinder getService(String serviceName) {
        IBinder binder = null;
        try {
            ClassLoader cl = mContext.getClassLoader();
            Class<?> serviceManager = cl.loadClass("android.os.ServiceManager");
            Class[] paramTypes = new Class[1];
            paramTypes[0] = String.class;
            Method get = serviceManager.getMethod("getService", paramTypes);
            Object[] params = new Object[1];
            params[0] = serviceName;
            binder = (IBinder) get.invoke(serviceManager, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return binder;
    }

    public boolean getDeviceService() {
        if (mDeviceService == null) {
            mDeviceService = AidlDeviceService.Stub.asInterface(getService(ACTION_DEVICE_SERVICE));
        }
        return mDeviceService == null ? false : true;
    }

    @Override
    public AidlSystem getSystem() {
        try {
            getDeviceService();
            if (mDeviceService != null) {
                return AidlSystem.Stub.asInterface(mDeviceService.getSystemService());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public AidlPinpad getPinpad(int type) {
        try {
            getDeviceService();
            if (mDeviceService != null && mDeviceService.getPinPad(type) != null) {
                return AidlPinpad.Stub.asInterface(mDeviceService.getPinPad(type));
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public AidlShellMonitor getShellMonitor() {
        try {
            getDeviceService();
            if (mDeviceService != null) {
                return AidlShellMonitor.Stub.asInterface(mDeviceService.getShellMonitor());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public AidlICCard getIcc() {
        try {
            getDeviceService();
            if (mDeviceService != null) {
                return AidlICCard.Stub.asInterface(mDeviceService.getInsertCardReader());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public AidlRFCard getRf() {
        try {
            getDeviceService();
            if (mDeviceService != null) {
                return AidlRFCard.Stub.asInterface(mDeviceService.getRFIDReader());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public AidlMagCard getMag() {
        try {
            getDeviceService();
            if (mDeviceService != null) {
                return AidlMagCard.Stub.asInterface(mDeviceService.getMagCardReader());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public AidlEmvL2 getEmv() {
        try {
            getDeviceService();
            if (mDeviceService != null) {
                return AidlEmvL2.Stub.asInterface(mDeviceService.getL2Emv());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public AidlPure getPurePay() {
        try {
            getDeviceService();
            if (mDeviceService != null) {
                return AidlPure.Stub.asInterface(mDeviceService.getL2Pure());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public AidlPaypass getPaypass() {
        try {
            getDeviceService();
            if (mDeviceService != null) {
                return AidlPaypass.Stub.asInterface(mDeviceService.getL2Paypass());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public AidlPaywave getPaywave() {
        try {
            getDeviceService();
            if (mDeviceService != null) {
                return AidlPaywave.Stub.asInterface(mDeviceService.getL2Paywave());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public AidlEntry getEntry() {
        try {
            getDeviceService();
            if (mDeviceService != null) {
                return AidlEntry.Stub.asInterface(mDeviceService.getL2Entry());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public AidlAmex getAmexPay() {
        try {
            getDeviceService();
            if (mDeviceService != null) {
                return AidlAmex.Stub.asInterface(mDeviceService.getL2Amex());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public AidlQpboc getUnionPay() {
        try {
            getDeviceService();
            if (mDeviceService != null) {
                return AidlQpboc.Stub.asInterface(mDeviceService.getL2Qpboc());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public AidlRupay getRupay() {
        try {
            getDeviceService();
            if (mDeviceService != null) {
                return AidlRupay.Stub.asInterface(mDeviceService.getL2Rupay());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public AidlMir getMirPay() {
        try {
            getDeviceService();
            if (mDeviceService != null) {
                return AidlMir.Stub.asInterface(mDeviceService.getL2Mir());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public AidlDpas getDpasPay() {
        try {
            getDeviceService();
            if (mDeviceService != null) {
                return AidlDpas.Stub.asInterface(mDeviceService.getL2Dpas());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public AidlJcb getJcbPay() {
        try {
            getDeviceService();
            if (mDeviceService != null) {
                return AidlJcb.Stub.asInterface(mDeviceService.getL2JCB());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public AidlPsam getPsam(int devid) {
        try {
            getDeviceService();
            if (mDeviceService != null) {
                return AidlPsam.Stub.asInterface(mDeviceService.getPSAMReader(devid));
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public AidlSerialport getSerialport(int port) {
        try {
            getDeviceService();
            if (mDeviceService != null) {
                return AidlSerialport.Stub.asInterface(mDeviceService.getSerialPort(port));
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public AidlCPUCard getCpu() {
        try {
            getDeviceService();
            if (mDeviceService != null) {
                return AidlCPUCard.Stub.asInterface(mDeviceService.getCPUCard());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public AidlCheckCard getCheckCard() {
        try {
            getDeviceService();
            if (mDeviceService != null) {
                return AidlCheckCard.Stub.asInterface(mDeviceService.getCheckCard());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void setMode(int mode) {

    }

    @Override
    public void close() {

    }

    @Override
    public ICardReader getCardReader() {
        CardReader cardReader = CardReader.getInstance();
        return cardReader;
    }

    @Override
    public IEmv getEmvHelper() {
        //TODO: Masih Error
        return TransProcess.getInstance();
    }
}
