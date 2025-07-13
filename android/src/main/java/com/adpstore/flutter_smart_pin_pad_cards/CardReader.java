package com.adpstore.flutter_smart_pin_pad_cards;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import androidx.annotation.Nullable;
import android.util.Log;

import com.adpstore.flutter_smart_pin_pad_cards.entity.CardData;
import com.adpstore.flutter_smart_pin_pad_cards.entity.EinputType;
import com.adpstore.flutter_smart_pin_pad_cards.entity.EmvAidParam;
import com.adpstore.flutter_smart_pin_pad_cards.entity.EmvOnlineResp;
import com.adpstore.flutter_smart_pin_pad_cards.entity.EmvOutCome;
import com.adpstore.flutter_smart_pin_pad_cards.entity.EmvPinEnter;
import com.adpstore.flutter_smart_pin_pad_cards.entity.EmvTransPraram;
import com.adpstore.flutter_smart_pin_pad_cards.enums.EKernelType;
import com.adpstore.flutter_smart_pin_pad_cards.enums.EPinType;
import com.adpstore.flutter_smart_pin_pad_cards.enums.ETransStatus;
import com.adpstore.flutter_smart_pin_pad_cards.param.AidParam;
import com.adpstore.flutter_smart_pin_pad_cards.param.CapkParam;
import com.adpstore.flutter_smart_pin_pad_cards.utils.CardTimer;
import com.topwise.cloudpos.aidl.emv.level2.Combination;
import com.topwise.cloudpos.aidl.emv.level2.EmvCandidateItem;
import com.topwise.cloudpos.aidl.emv.level2.EmvCapk;
import com.topwise.cloudpos.aidl.emv.level2.EmvTerminalInfo;
import com.topwise.cloudpos.aidl.iccard.AidlICCard;
import com.topwise.cloudpos.aidl.magcard.AidlMagCard;
import com.topwise.cloudpos.aidl.rfcard.AidlRFCard;
import com.topwise.cloudpos.aidl.shellmonitor.AidlShellMonitor;
import com.topwise.cloudpos.aidl.shellmonitor.InstructionSendDataCallback;
import com.topwise.cloudpos.struct.BytesUtil;
import com.topwise.toptool.impl.TopTool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class CardReader extends Service implements ICardReader {
    private static final String TAG = "CardReader";
    private static CardReader instance;
    private boolean isRunning = false;
    private CardTimer cardTimer;
    private CardData cardData;
    private FindCardThread findCardThread;
    private boolean isMag;
    private boolean isIcc;
    private boolean isRf;

    private onReadCardListener onReadCardListener;
    public static final int PURE_MAG_CARD = 0X00;
    public static final int ICC_MAG_CARD = 0X58;
    public static final int MSR_TRACK_1 = 0X01;
    public static final int MSR_TRACK_2 = 0X02;
    public static final int MSR_TRACK_3 = 0X03;
    private byte mResultCode;
    private byte[] mResultData;
    private boolean bCloseAll;
    private Context context;

    // Initialize services with null safety
    private AidlMagCard magCard;
    private AidlICCard icCard;
    private AidlRFCard rfCard;
    private AidlShellMonitor aidlShellMonitor;

    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        CardReader getService() {
            return CardReader.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    // Required public constructor
    public CardReader() {
        initializeServices();
    }

    private CardReader(Context context) {
        this.context = context;
        initializeServices();
    }

    public synchronized static CardReader getInstance(Context context) {
        if (instance == null) {
            instance = new CardReader(context);
        }
        return instance;
    }

    private void initializeServices() {
        try {
            // Wait for DeviceServiceManagers to be ready
            for (int i = 0; i < 50; i++) { // Max 5 seconds wait
                DeviceServiceManagers serviceManager = DeviceServiceManagers.getInstance();
                if (serviceManager != null) {
                    magCard = serviceManager.getMagCardReader();
                    icCard = serviceManager.getICCardReader();
                    rfCard = serviceManager.getRfCardReader();
                    aidlShellMonitor = serviceManager.getShellMonitor();

                    Log.d(TAG, String.format("Services initialized - Mag: %s, IC: %s, RF: %s, Shell: %s",
                            magCard != null, icCard != null, rfCard != null, aidlShellMonitor != null));
                    break;
                }
                SystemClock.sleep(100);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error initializing services: " + e.getMessage(), e);
        }
    }

    /**
     * open MAG with null safety
     */
    private boolean openMag() {
        try {
            if (magCard == null) {
                Log.e(TAG, "magCard is null, cannot open");
                return false;
            }
            boolean result = magCard.open();
            Log.d(TAG, "openMag result: " + result);
            return result;
        } catch (RemoteException e) {
            Log.e(TAG, "openMag error: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * close Mag with null safety
     */
    private boolean closeMag() {
        try {
            if (magCard == null) {
                Log.w(TAG, "magCard is null, cannot close");
                return false;
            }
            boolean result = magCard.close();
            Log.d(TAG, "closeMag result: " + result);
            return result;
        } catch (RemoteException e) {
            Log.e(TAG, "closeMag error: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * open IC with null safety
     */
    private boolean openIc() {
        try {
            if (icCard == null) {
                Log.e(TAG, "icCard is null, cannot open");
                return false;
            }
            boolean result = icCard.open();
            Log.d(TAG, "openIc result: " + result);
            return result;
        } catch (RemoteException e) {
            Log.e(TAG, "openIc error: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * closeIC with null safety
     */
    private boolean closeIc() {
        try {
            if (icCard == null) {
                Log.w(TAG, "icCard is null, cannot close");
                return false;
            }
            boolean result = icCard.close();
            Log.d(TAG, "closeIc result: " + result);
            return result;
        } catch (RemoteException e) {
            Log.e(TAG, "closeIc error: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * open RF with null safety
     */
    private boolean openRf() {
        try {
            if (rfCard == null) {
                Log.e(TAG, "rfCard is null, cannot open");
                return false;
            }
            boolean result = rfCard.open();
            Log.d(TAG, "openRf result: " + result);
            return result;
        } catch (RemoteException e) {
            Log.e(TAG, "openRf error: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * close RF with null safety
     */
    private boolean closeRf() {
        try {
            if (rfCard == null) {
                Log.w(TAG, "rfCard is null, cannot close");
                return false;
            }
            boolean result = rfCard.close();
            Log.d(TAG, "closeRf result: " + result);
            return result;
        } catch (RemoteException e) {
            Log.e(TAG, "closeRf error: " + e.getMessage(), e);
            return false;
        }
    }

    @Override
    public void startFindCard(boolean isMag, boolean isIcc, boolean isRf, int outtime,
                              onReadCardListener onReadCardListener) {
        Log.d(TAG, String.format("startFindCard: isMag=%s, isIcc=%s, isRf=%s, timeout=%d",
                isMag, isIcc, isRf, outtime));

        // Check if services are initialized
        if (!areServicesReady()) {
            Log.e(TAG, "Services not ready, reinitializing...");
            initializeServices();

            if (!areServicesReady()) {
                Log.e(TAG, "Failed to initialize services");
                if (onReadCardListener != null) {
                    onReadCardListener.getReadState(new CardData(CardData.EReturnType.OTHER_ERR));
                }
                return;
            }
        }

        this.isMag = isMag;
        this.isIcc = isIcc;
        this.isRf = isRf;
        this.onReadCardListener = onReadCardListener;

        if (cardTimer != null) {
            cardTimer.cancel();
            cardTimer = null;
        }

        bCloseAll = false;
        cardTimer = new CardTimer(outtime, 1);
        cardTimer.setTimeCountListener(new CardTimer.TickTimerListener() {
            @Override
            public void onFinish() {
                Log.d(TAG, "CardTimer: onFinish");
                setResult(new CardData(CardData.EReturnType.OTHER_ERR));
                if (bCloseAll) {
                    CloseAll();
                }
            }

            @Override
            public void onTick(long leftTime) {
                if (findCardThread != null) {
                    Log.v(TAG, String.format("FindCardThread ID onTick: %d, isInterrupted: %s",
                            findCardThread.getId(), findCardThread.isInterrupted()));
                }
                Log.v(TAG, "CardTimer: onTick " + leftTime);
                if (leftTime == 1) bCloseAll = true;
            }
        });
        cardTimer.start();

        isRunning = true;
        findCardThread = new FindCardThread();
        findCardThread.start();
        Log.d(TAG, "FindCardThread ID: " + findCardThread.getId());
    }

    private boolean areServicesReady() {
        return (magCard != null || !isMag) &&
                (icCard != null || !isIcc) &&
                (rfCard != null || !isRf) &&
                (aidlShellMonitor != null);
    }

    class FindCardThread extends Thread {
        @Override
        public void run() {
            try {
                CloseAll();
                SystemClock.sleep(20);

                // Check and open with null safety
                if (isMag && !openMag()) {
                    Log.e(TAG, "Failed to open MAG card reader");
                    setResult(new CardData(CardData.EReturnType.OPEN_MAG_ERR));
                    return;
                }

                if (isIcc && !openIc()) {
                    Log.e(TAG, "Failed to open IC card reader");
                    CloseAll();
                    setResult(new CardData(CardData.EReturnType.OPEN_IC_ERR));
                    return;
                }

                if (isRf && !openRf()) {
                    Log.e(TAG, "Failed to open RF card reader");
                    CloseAll();
                    setResult(new CardData(CardData.EReturnType.OPEN_RF_ERR));
                    return;
                }

                SystemClock.sleep(20);

                while (!isInterrupted() && isRunning) {
                    // MAG card processing
                    if (isMag) {
                        try {
                            byte startRead = readMag();
                            if (startRead == ICC_MAG_CARD || startRead == PURE_MAG_CARD) {
                                processmagCard();
                                return;
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "MAG card processing error: " + e.getMessage(), e);
                            CloseAll();
                            setResult(new CardData(CardData.EReturnType.OPEN_MAG_RESET_ERR));
                            return;
                        }
                    }

                    // IC card processing
                    if (isIcc) {
                        try {
                            if (icCard != null && icCard.isExist()) {
                                processICCard();
                                return;
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "IC card processing error: " + e.getMessage(), e);
                            CloseAll();
                            setResult(new CardData(CardData.EReturnType.OPEN_IC_RESET_ERR));
                            return;
                        }
                    }

                    // RF card processing
                    if (isRf) {
                        try {
                            int b = RFCardIsExist();
                            if (0x00 == b) {
                                processRFCard();
                                return;
                            } else if ((byte) 0x93 == b) {
                                if (onReadCardListener != null) {
                                    onReadCardListener.onNotification(CardData.EReturnType.RF_MULTI_CARD);
                                }
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "RF card processing error: " + e.getMessage(), e);
                            CloseAll();
                            setResult(new CardData(CardData.EReturnType.OPEN_RF_RESET_ERR));
                            return;
                        }
                    }

                    SystemClock.sleep(20);
                }
            } catch (Exception e) {
                Log.e(TAG, "FindCardThread error: " + e.getMessage(), e);
                CloseAll();
                setResult(new CardData(CardData.EReturnType.OTHER_ERR));
            }
        }

        private void processmagCard() throws Exception {
            byte[] firstTlvArray = readData((byte) MSR_TRACK_1);
            byte[] secondTlvArray = readData((byte) MSR_TRACK_2);
            byte[] thirdTlvArray = readData((byte) MSR_TRACK_3);

            CloseAll();

            if (firstTlvArray == null && secondTlvArray == null && thirdTlvArray == null) {
                Log.e(TAG, "Read mag data is null");
                setResult(new CardData(CardData.EReturnType.OPEN_MAG_RESET_ERR));
                return;
            }

            cardData = new CardData(CardData.EReturnType.OK, CardData.ECardType.MAG);

            // Process track data
            if (firstTlvArray != null) {
                Log.d(TAG, "Read mag firstTlvArray: " + new String(firstTlvArray));
                int realFirstLen = firstTlvArray.length - 2;
                if (realFirstLen > 0) {
                    byte[] realFirstByte = new byte[realFirstLen];
                    System.arraycopy(firstTlvArray, 1, realFirstByte, 0, realFirstLen);
                    cardData.setTrack1(new String(realFirstByte));
                }
            }

            if (secondTlvArray != null) {
                Log.d(TAG, "Read mag secondTlvArray: " + new String(secondTlvArray));
                int realSecondLen = secondTlvArray.length - 2;
                if (realSecondLen > 0) {
                    byte[] realSecondByte = new byte[realSecondLen];
                    System.arraycopy(secondTlvArray, 1, realSecondByte, 0, realSecondLen);
                    String track2Data = new String(realSecondByte);
                    cardData.setTrack2(track2Data);

                    int index = track2Data.indexOf("=");
                    if (index > 0) {
                        cardData.setPan(track2Data.substring(0, index));
                        if (track2Data.length() > index + 5) {
                            String expirydate = track2Data.substring(index + 1, index + 1 + 4);
                            cardData.setExpiryDate(expirydate);
                        }
                        if (track2Data.length() > index + 8) {
                            String serviceCode = track2Data.substring(index + 1 + 4, index + 1 + 4 + 3);
                            cardData.setServiceCode(serviceCode);
                        }
                    }
                }
            }

            if (thirdTlvArray != null) {
                Log.d(TAG, "Read mag thirdTlvArray: " + new String(thirdTlvArray));
                int realThirdLen = thirdTlvArray.length - 2;
                if (realThirdLen > 0) {
                    byte[] realThirdByte = new byte[realThirdLen];
                    System.arraycopy(thirdTlvArray, 1, realThirdByte, 0, realThirdLen);
                    cardData.setTrack3(new String(realThirdByte));
                }
            }

            setResult(cardData);
        }

        private void processICCard() throws RemoteException {
            byte[] data = icCard.reset(0);
            if (data != null && data.length > 0) {
                closeMag();
                closeRf();

                // Simplified IC card processing
                cardData = new CardData(CardData.EReturnType.OK, CardData.ECardType.IC);
                cardData.setPan("************"); // Placeholder
                setResult(cardData);
            } else {
                CloseAll();
                Log.e(TAG, "IC Card reset failed");
                setResult(new CardData(CardData.EReturnType.OPEN_IC_RESET_ERR));
            }
        }

        private void processRFCard() throws RemoteException {
            if (rfCard != null) {
                byte[] data = rfCard.reset(0);
                if (data != null && data.length > 0) {
                    Log.d(TAG, "Read RF SUCCESS");
                    closeMag();
                    closeIc();
                    setResult(new CardData(CardData.EReturnType.OK, CardData.ECardType.RF));
                } else {
                    CloseAll();
                    setResult(new CardData(CardData.EReturnType.OPEN_RF_RESET_ERR));
                    Log.e(TAG, "Read RF reset fail");
                }
            }
        }
    }

    private byte readMag() throws Exception {
        if (aidlShellMonitor == null) {
            Log.e(TAG, "aidlShellMonitor is null");
            throw new Exception("Shell monitor not available");
        }

        Log.d(TAG, "Reading MAG card...");
        byte[] mBuff = new byte[]{(byte) 0x0b, (byte) 0xb8};
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        aidlShellMonitor.sendIns(6, (byte) 0x68, (byte) 0x04, (byte) 0x02, mBuff,
                new InstructionSendDataCallback.Stub() {
                    @Override
                    public void onReceiveData(byte resultCode, byte[] tlvArray) throws RemoteException {
                        if (tlvArray != null && tlvArray.length > 0) {
                            Log.d(TAG, "readMag received data: " + TopTool.getInstance().getConvert().bcdToStr(tlvArray));
                        }
                        Log.d(TAG, "readMag resultCode: " + resultCode);
                        mResultCode = resultCode;
                        mResultData = tlvArray;
                        countDownLatch.countDown();
                    }
                });

        countDownLatch.await();
        Log.d(TAG, "readMag resultCode: " + mResultCode);
        return mResultCode;
    }

    private byte RFCardIsExist() throws RemoteException {
        if (aidlShellMonitor == null) {
            Log.e(TAG, "aidlShellMonitor is null");
            return (byte) 0xFF; // Error code
        }

        Log.d(TAG, "Checking RF card existence...");
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        aidlShellMonitor.sendIns(3, (byte) 0x66, (byte) 0x03, (byte) 0x02, new byte[]{0x00, 0x11},
                new InstructionSendDataCallback.Stub() {
                    @Override
                    public void onReceiveData(byte resultCode, byte[] tlvArray) {
                        if (tlvArray != null && tlvArray.length > 0) {
                            Log.d(TAG, "RFCardIsExist received data: " + TopTool.getInstance().getConvert().bcdToStr(tlvArray));
                        }
                        Log.d(TAG, "RFCardIsExist resultCode: " + resultCode);
                        mResultCode = resultCode;
                        countDownLatch.countDown();
                    }
                });

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            Log.e(TAG, "RFCardIsExist interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
        }

        return mResultCode;
    }

    private byte[] readData(byte inByte) throws Exception {
        if (aidlShellMonitor == null) {
            Log.e(TAG, "aidlShellMonitor is null");
            throw new Exception("Shell monitor not available");
        }

        Log.d(TAG, "Reading data for byte: " + inByte);
        byte[] mBuff = new byte[]{inByte};
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        aidlShellMonitor.sendIns(6, (byte) 0x68, (byte) 0x05, (byte) 0x01, mBuff,
                new InstructionSendDataCallback.Stub() {
                    @Override
                    public void onReceiveData(byte resultCode, byte[] tlvArray) throws RemoteException {
                        if (tlvArray != null && tlvArray.length > 0) {
                            Log.d(TAG, "readData received data: " + TopTool.getInstance().getConvert().bcdToStr(tlvArray));
                        }
                        Log.d(TAG, "readData resultCode: " + resultCode);
                        mResultCode = resultCode;
                        mResultData = tlvArray;
                        countDownLatch.countDown();
                    }
                });

        countDownLatch.await();
        Log.d(TAG, "readData resultCode: " + mResultCode);
        return mResultCode == (byte) 0x00 ? mResultData : null;
    }

    @Override
    public void cancel() {
        Log.d(TAG, "Cancelling card reader...");
        CloseAll();

        if (cardTimer != null) {
            cardTimer.cancel();
            Log.d(TAG, "cardTimer cancelled");
            cardTimer = null;
        }

        if (findCardThread != null && !findCardThread.isInterrupted()) {
            findCardThread.interrupt();
            Log.d(TAG, "findCardThread interrupted");
            findCardThread = null;
        }

        isRunning = false;
        instance = null;
    }

    private void setResult(CardData cardData) {
        isRunning = false;

        if (cardTimer != null) {
            cardTimer.cancel();
            Log.d(TAG, "setResult cardTimer cancelled");
            cardTimer = null;
        }

        if (findCardThread != null && !findCardThread.isInterrupted()) {
            findCardThread.interrupt();
            Log.d(TAG, "setResult findCardThread interrupted");
            findCardThread = null;
        }

        if (onReadCardListener != null) {
            onReadCardListener.getReadState(cardData);
        }

        onReadCardListener = null;
    }

    /**
     * Close all card readers safely
     */
    private void CloseAll() {
        Log.d(TAG, "Closing all card readers...");
        closeIc();
        closeMag();
        closeRf();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "CardReader service onCreate");
        // Initialize any service-related resources here if needed
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "CardReader service onStartCommand");
        // Handle service start command if needed
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "CardReader service onDestroy");
        cancel();
    }

    public interface onReadCardListener {
        /**
         * Success to read card
         * @param cardData
         */
        void getReadState(CardData cardData);

        /**
         * Notice app about return type
         * @param eReturnType
         */
        void onNotification(CardData.EReturnType eReturnType);
    }
}