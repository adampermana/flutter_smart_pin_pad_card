package com.adpstore.flutter_smart_pin_pad_cards;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.adpstore.flutter_smart_pin_pad_cards.impl.TransProcess;
import com.topwise.cloudpos.aidl.AidlDeviceService;
import com.topwise.cloudpos.aidl.buzzer.AidlBuzzer;
import com.topwise.cloudpos.aidl.camera.AidlCameraScanCode;
import com.topwise.cloudpos.aidl.card.AidlCheckCard;
import com.topwise.cloudpos.aidl.cpucard.AidlCPUCard;
import com.topwise.cloudpos.aidl.decoder.AidlDecoderManager;
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
import com.topwise.cloudpos.aidl.fingerprint.AidlFingerprint;
import com.topwise.cloudpos.aidl.iccard.AidlICCard;
import com.topwise.cloudpos.aidl.led.AidlLed;
import com.topwise.cloudpos.aidl.magcard.AidlMagCard;
import com.topwise.cloudpos.aidl.pedestal.AidlPedestal;
import com.topwise.cloudpos.aidl.pinpad.AidlPinpad;
import com.topwise.cloudpos.aidl.pm.AidlPM;
import com.topwise.cloudpos.aidl.printer.AidlPrinter;
import com.topwise.cloudpos.aidl.psam.AidlPsam;
import com.topwise.cloudpos.aidl.rfcard.AidlRFCard;
import com.topwise.cloudpos.aidl.serialport.AidlSerialport;
import com.topwise.cloudpos.aidl.shellmonitor.AidlShellMonitor;
import com.topwise.cloudpos.aidl.system.AidlSystem;
import com.topwise.cloudpos.aidl.tm.AidlTM;
import com.topwise.toptool.api.convert.IConvert;
import com.topwise.toptool.impl.TopTool;

import java.lang.reflect.Method;

/**
 * @author caixh
 * Improved version with better error handling and null safety
 */
public class DeviceServiceManagers {
    private static final String TAG = "DeviceServiceManagers";

    private static final String DEVICE_SERVICE_PACKAGE_NAME = "com.android.topwise.topusdkservice";
    private static final String DEVICE_SERVICE_CLASS_NAME = "com.android.topwise.topusdkservice.service.DeviceService";
    private static final String ACTION_DEVICE_SERVICE = "topwise_cloudpos_device_service";

    private static DeviceServiceManagers instance;
    private static final IConvert convert = TopTool.getInstance().getConvert();
    private static Context mContext;
    private AidlDeviceService mDeviceService;
    private boolean isServiceConnected = false;
    private boolean isBinding = false;

    public static DeviceServiceManagers getInstance() {
        Log.d(TAG, "getInstance()");
        if (null == instance) {
            synchronized (DeviceServiceManagers.class) {
                if (null == instance) {
                    instance = new DeviceServiceManagers();
                }
            }
        }
        return instance;
    }

    public boolean bindDeviceService(Context context) {
        Log.i(TAG, "bindDeviceService");

        if (isServiceConnected) {
            Log.i(TAG, "Service already connected");
            return true;
        }

        if (isBinding) {
            Log.i(TAG, "Service is currently binding");
            return true;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.i(TAG, "Android O+ detected, using direct service access");
            getDeviceService();
            return true;
        }

        this.mContext = context;
        Intent intent = new Intent();
        intent.setAction(ACTION_DEVICE_SERVICE);
        intent.setClassName(DEVICE_SERVICE_PACKAGE_NAME, DEVICE_SERVICE_CLASS_NAME);

        try {
            isBinding = true;
            boolean bindResult = mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            Log.i(TAG, "bindResult = " + bindResult);

            if (!bindResult) {
                isBinding = false;
                Log.e(TAG, "Failed to bind service, trying direct access");
                getDeviceService();
                return mDeviceService != null;
            }

            return bindResult;
        } catch (Exception e) {
            isBinding = false;
            Log.e(TAG, "Exception during service binding: " + e.getMessage(), e);
            // Try direct access as fallback
            getDeviceService();
            return mDeviceService != null;
        }
    }

    public void unBindDeviceService() {
        Log.i(TAG, "unBindDeviceService");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mDeviceService = null;
            isServiceConnected = false;
            return;
        }
        try {
            if (mContext != null && isServiceConnected) {
                mContext.unbindService(mConnection);
                isServiceConnected = false;
            }
        } catch (Exception e) {
            Log.i(TAG, "unbind DeviceService service failed : " + e.getMessage());
        }
    }

    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mDeviceService = AidlDeviceService.Stub.asInterface(service);
            isServiceConnected = true;
            isBinding = false;
            Log.i(TAG, "onServiceConnected: " + mDeviceService);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.i(TAG, "onServiceDisconnected: " + mDeviceService);
            mDeviceService = null;
            isServiceConnected = false;
            isBinding = false;
        }
    };

    public void getDeviceService() {
        if (mDeviceService == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                IBinder binder = getService(ACTION_DEVICE_SERVICE);
                if (binder != null) {
                    mDeviceService = AidlDeviceService.Stub.asInterface(binder);
                    isServiceConnected = true;
                    Log.i(TAG, "Direct service access successful");
                } else {
                    Log.e(TAG, "Failed to get service binder");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error getting device service: " + e.getMessage(), e);
            }
        }
    }

    private static IBinder getService(String serviceName) {
        IBinder binder = null;
        try {
            ClassLoader cl = mContext != null ? mContext.getClassLoader() :
                    DeviceServiceManagers.class.getClassLoader();

            Class<?> serviceManager = cl.loadClass("android.os.ServiceManager");
            Class[] paramTypes = new Class[1];
            paramTypes[0] = String.class;
            Method get = serviceManager.getMethod("getService", paramTypes);
            Object[] params = new Object[1];
            params[0] = serviceName;
            binder = (IBinder) get.invoke(serviceManager, params);
        } catch (Exception e) {
            Log.e(TAG, "Error getting service: " + e.getMessage(), e);
        }
        return binder;
    }

    // Safe getter methods with null checks
    private <T> T safeGetService(ServiceGetter<T> getter) {
        try {
            getDeviceService();
            if (mDeviceService != null) {
                return getter.get(mDeviceService);
            }
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException in service call: " + e.getMessage(), e);
        } catch (Exception e) {
            Log.e(TAG, "Exception in service call: " + e.getMessage(), e);
        }
        return null;
    }

    @FunctionalInterface
    private interface ServiceGetter<T> {
        T get(AidlDeviceService service) throws RemoteException;
    }

    public AidlSystem getSystemManager() {
        return safeGetService(service -> AidlSystem.Stub.asInterface(service.getSystemService()));
    }

    public AidlBuzzer getBuzzer() {
        return safeGetService(service -> AidlBuzzer.Stub.asInterface(service.getBuzzer()));
    }

    public AidlDecoderManager getDecoder() {
        return safeGetService(service -> AidlDecoderManager.Stub.asInterface(service.getDecoder()));
    }

    public AidlLed getLed() {
        return safeGetService(service -> AidlLed.Stub.asInterface(service.getLed()));
    }

    public AidlPinpad getPinpadManager(int devid) {
        return safeGetService(service -> AidlPinpad.Stub.asInterface(service.getPinPad(devid)));
    }

    public AidlPrinter getPrintManager() {
        return safeGetService(service -> AidlPrinter.Stub.asInterface(service.getPrinter()));
    }

    public AidlTM getAidlTM() {
        return safeGetService(service -> AidlTM.Stub.asInterface(service.getTM()));
    }

    public AidlICCard getICCardReader() {
        AidlICCard icCard = safeGetService(service -> AidlICCard.Stub.asInterface(service.getInsertCardReader()));
        if (icCard == null) {
            Log.w(TAG, "ICCard service is null");
        } else {
            Log.d(TAG, "ICCard service obtained successfully");
        }
        return icCard;
    }

    public AidlRFCard getRfCardReader() {
        AidlRFCard rfCard = safeGetService(service -> AidlRFCard.Stub.asInterface(service.getRFIDReader()));
        if (rfCard == null) {
            Log.w(TAG, "RFCard service is null");
        } else {
            Log.d(TAG, "RFCard service obtained successfully");
        }
        return rfCard;
    }

    public AidlMagCard getMagCardReader() {
        AidlMagCard magCard = safeGetService(service -> AidlMagCard.Stub.asInterface(service.getMagCardReader()));
        if (magCard == null) {
            Log.w(TAG, "MagCard service is null");
        } else {
            Log.d(TAG, "MagCard service obtained successfully");
        }
        return magCard;
    }

    public AidlShellMonitor getShellMonitor() {
        AidlShellMonitor shellMonitor = safeGetService(service -> AidlShellMonitor.Stub.asInterface(service.getShellMonitor()));
        if (shellMonitor == null) {
            Log.w(TAG, "ShellMonitor service is null");
        } else {
            Log.d(TAG, "ShellMonitor service obtained successfully");
        }
        return shellMonitor;
    }

    public AidlEmvL2 getEmvL2() {
        return safeGetService(service -> AidlEmvL2.Stub.asInterface(service.getL2Emv()));
    }

    public AidlPsam getPsamCardReader(int devid) {
        return safeGetService(service -> AidlPsam.Stub.asInterface(service.getPSAMReader(devid)));
    }

    public AidlCPUCard getCPUCardReader() {
        return safeGetService(service -> AidlCPUCard.Stub.asInterface(service.getCPUCard()));
    }

    public AidlSerialport getSerialPort(int port) {
        return safeGetService(service -> AidlSerialport.Stub.asInterface(service.getSerialPort(port)));
    }

    public AidlPedestal getPedestal() {
        return safeGetService(service -> AidlPedestal.Stub.asInterface(service.getPedestal()));
    }

    public AidlPure getL2Pure() {
        return safeGetService(service -> AidlPure.Stub.asInterface(service.getL2Pure()));
    }

    public AidlPaypass getL2Paypass() {
        return safeGetService(service -> AidlPaypass.Stub.asInterface(service.getL2Paypass()));
    }

    public AidlPaywave getL2Paywave() {
        return safeGetService(service -> AidlPaywave.Stub.asInterface(service.getL2Paywave()));
    }

    public AidlEntry getL2Entry() {
        return safeGetService(service -> AidlEntry.Stub.asInterface(service.getL2Entry()));
    }

    public AidlAmex getL2Amex() {
        return safeGetService(service -> AidlAmex.Stub.asInterface(service.getL2Amex()));
    }

    public AidlQpboc getL2Qpboc() {
        return safeGetService(service -> AidlQpboc.Stub.asInterface(service.getL2Qpboc()));
    }

    public AidlRupay getRupay() {
        return safeGetService(service -> AidlRupay.Stub.asInterface(service.getL2Rupay()));
    }

    public AidlMir getMirPay() {
        return safeGetService(service -> AidlMir.Stub.asInterface(service.getL2Mir()));
    }

    public AidlDpas getDpasPay() {
        return safeGetService(service -> AidlDpas.Stub.asInterface(service.getL2Dpas()));
    }

    public AidlJcb getJcbPay() {
        return safeGetService(service -> AidlJcb.Stub.asInterface(service.getL2JCB()));
    }

    public AidlPsam getPsam(int devid) {
        return safeGetService(service -> AidlPsam.Stub.asInterface(service.getPSAMReader(devid)));
    }

    public AidlCameraScanCode getCameraManager() {
        return safeGetService(service -> AidlCameraScanCode.Stub.asInterface(service.getCameraManager()));
    }

    public Bundle expandFunction(Bundle param) {
        return safeGetService(service -> service.expandFunction(param));
    }

    public AidlPM getPm() {
        return safeGetService(service -> AidlPM.Stub.asInterface(service.getPM()));
    }

    public AidlFingerprint getFingerprint() {
        return safeGetService(service -> AidlFingerprint.Stub.asInterface(service.getFingerprint()));
    }

    public ICardReader getCardReader() {
        try {
            CardReader cardReader = CardReader.getInstance(mContext);
            Log.d(TAG, "CardReader instance created: " + (cardReader != null));
            return cardReader;
        } catch (Exception e) {
            Log.e(TAG, "Error creating CardReader instance: " + e.getMessage(), e);
            return null;
        }
    }

    public IEmv getEmvHelper() {
        try {
            return (IEmv) TransProcess.getInstance();
        } catch (Exception e) {
            Log.e(TAG, "Error getting EMV helper: " + e.getMessage(), e);
            return null;
        }
    }

    public AidlCheckCard getCheckCard() {
        return safeGetService(service -> AidlCheckCard.Stub.asInterface(service.getCheckCard()));
    }

    public boolean injectMainKey(int mainKeyIndex, byte[] key) {
        try {
            AidlPinpad pinpadManager = getPinpadManager(0);
            return pinpadManager != null && pinpadManager.loadMainkey(mainKeyIndex, key, null);
        } catch (RemoteException e) {
            Log.e(TAG, "Error injecting main key: " + e.getMessage(), e);
            return false;
        }
    }

    public interface InitListener {
        void OnConnection(boolean ret);
    }

    // Check if service is ready
    public boolean isServiceReady() {
        return mDeviceService != null && isServiceConnected;
    }

    // Wait for service to be ready
    public boolean waitForService(long timeoutMs) {
        long startTime = System.currentTimeMillis();
        while (!isServiceReady() && (System.currentTimeMillis() - startTime) < timeoutMs) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return isServiceReady();
    }
}