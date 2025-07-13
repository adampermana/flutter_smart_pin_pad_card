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

        // Replace the processICCard() method in CardReader.java
        private void processICCard() throws RemoteException {
            byte[] data = icCard.reset(0);
            if (data != null && data.length > 0) {
                Log.d(TAG, "IC Card reset successful, starting EMV process");
                closeMag();
                closeRf();

                try {
                    // Get EMV helper
                    IEmv emvHelper = DeviceServiceManagers.getInstance().getEmvHelper();
                    if (emvHelper == null) {
                        Log.e(TAG, "EMV helper is null");
                        setResult(new CardData(CardData.EReturnType.OPEN_IC_RESET_ERR));
                        return;
                    }

                    emvHelper.init(EinputType.CT);

                    emvHelper.setProcessListener(new ITransProcessListener() {
                        @Override
                        public int onReqAppAidSelect(String[] aids) {
                            if (aids != null && aids.length > 0) {
                                Log.d(TAG, "Selecting AID index: 0 from " + aids.length + " options");
                                return 0;
                            }
                            return -1;
                        }

                        @Override
                        public void onUpToAppEmvCandidateItem(EmvCandidateItem emvCandidateItem) {
                            if (emvCandidateItem != null) {
                                Log.d(TAG, "Selected candidate item: " + emvCandidateItem);
                            }
                        }

                        @Override
                        public void onUpToAppKernelType(EKernelType eKernelType) {
                            Log.d(TAG, "Kernel type: " + eKernelType);
                        }

                        @Override
                        public boolean onReqFinalAidSelect() {
                            return true;
                        }

                        @Override
                        public boolean onConfirmCardInfo(String cardNo) {
                            Log.d(TAG, "Confirming card number: " + cardNo);
                            return true;
                        }

                        @Override
                        public EmvPinEnter onReqGetPinProc(EPinType pinType, int leftTimes) {
                            Log.d(TAG, "PIN request - Type: " + pinType + ", Tries left: " + leftTimes);
                            // Return null to skip PIN entry for card reading
                            return null;
                        }

                        @Override
                        public boolean onDisplayPinVerifyStatus(int PinTryCounter) {
                            Log.d(TAG, "PIN try counter: " + PinTryCounter);
                            return true;
                        }

                        @Override
                        public boolean onReqUserAuthProc(int certype, String certnumber) {
                            Log.d(TAG, "Certificate auth - Type: " + certype + ", Number: " + certnumber);
                            return true;
                        }

                        @Override
                        public EmvOnlineResp onReqOnlineProc() {
                            Log.d(TAG, "Online processing requested");
                            EmvOnlineResp onlineResp = new EmvOnlineResp();
                            onlineResp.setAuthRespCode("00".getBytes()); // Approve
                            return onlineResp;
                        }

                        @Override
                        public boolean onSecCheckCardProc() {
                            return false;
                        }

                        @Override
                        public List<Combination> onLoadCombinationParam() {
                            List<Combination> combinations = new ArrayList<>();
                            try {
                                // Visa
                                Combination visa = new Combination();
                                visa.setUcAidLen((byte) 7);
                                visa.setAucAID(new byte[]{(byte) 0xA0, 0x00, 0x00, 0x00, 0x03, 0x10, 0x10});
                                visa.setUcKernIDLen((byte) 1);
                                visa.setAucKernelID(new byte[]{(byte) 0x03});
                                combinations.add(visa);

                                // MasterCard
                                Combination masterCard = new Combination();
                                masterCard.setUcAidLen((byte) 7);
                                masterCard.setAucAID(new byte[]{(byte) 0xA0, 0x00, 0x00, 0x00, 0x04, 0x10, 0x10});
                                masterCard.setUcKernIDLen((byte) 1);
                                masterCard.setAucKernelID(new byte[]{(byte) 0x02});
                                combinations.add(masterCard);

                                // JCB
                                Combination jcb = new Combination();
                                jcb.setUcAidLen((byte) 7);
                                jcb.setAucAID(new byte[]{(byte) 0xA0, 0x00, 0x00, 0x00, 0x65, 0x10, 0x10});
                                jcb.setUcKernIDLen((byte) 1);
                                jcb.setAucKernelID(new byte[]{(byte) 0x0B});
                                combinations.add(jcb);

                                // GPN
                                Combination gpn = new Combination();
                                gpn.setUcAidLen((byte) 7);
                                gpn.setAucAID(new byte[]{(byte) 0xA0, 0x00, 0x00, 0x06, 0x02, 0x10, 0x10});
                                gpn.setUcKernIDLen((byte) 1);
                                gpn.setAucKernelID(new byte[]{(byte) 0x15});
                                combinations.add(gpn);

                                Log.d(TAG, "Loaded " + combinations.size() + " card combinations");
                            } catch (Exception e) {
                                Log.e(TAG, "Error creating combinations: " + e.getMessage());
                            }
                            return combinations;
                        }

                        @Override
                        public EmvAidParam onFindCurAidParamProc(String sAid) {
                            Log.d(TAG, "Finding AID parameters for: " + sAid);
                            try {
                                EmvAidParam emvAidParam = new EmvAidParam();
                                AidParam aidParam = new AidParam();
                                aidParam.init(context);
                                emvAidParam.setAid(sAid);
                                aidParam.saveAll();
                                return emvAidParam;
                            } catch (Exception e) {
                                Log.e(TAG, "Error loading AID param: " + e.getMessage());
                                return null;
                            }
                        }

                        @Override
                        public void onRemoveCardProc() {
                            Log.d(TAG, "Card removal requested");
                        }

                        @Override
                        public EmvCapk onFindIssCapkParamProc(String sAid, byte bCapkIndex) {
                            Log.d(TAG, "Finding CAPK for AID: " + sAid + ", Index: " + bCapkIndex);
                            try {
                                CapkParam capkParam = new CapkParam();
                                capkParam.init(context);
                                capkParam.saveAll();
                                EmvCapk capk = new EmvCapk();
                                return capk;
                            } catch (Exception e) {
                                Log.e(TAG, "Error loading CAPK: " + e.getMessage());
                                return null;
                            }
                        }
                    });

                    // Set terminal info
                    EmvTerminalInfo terminalInfo = new EmvTerminalInfo();
                    terminalInfo.setUcTerminalType((byte) 0x22);
                    terminalInfo.setUcTerminalEntryMode((byte) 0x05);
                    emvHelper.setTerminalInfo(terminalInfo);

                    // Set transaction parameters
                    EmvTransPraram transParam = new EmvTransPraram((byte) 0x00);
                    transParam.setAmount(1000L);
                    transParam.setAucTransDate("250220");
                    transParam.setAucTransTime("120000");
                    emvHelper.setTransPraram(transParam);

                    // Start EMV process
                    Log.d(TAG, "Starting EMV process...");
                    EmvOutCome emvOutCome = emvHelper.StartEmvProcess();
                    Log.d(TAG, "EMV Process completed with result: " + emvOutCome);

                    // Debug all available EMV TLV data
                    debugEmvTlvData(emvHelper);

                    // Extract card data from EMV TLV data
                    String pan = extractPanFromEmv(emvHelper);
                    String expiryDate = null;
                    String track2 = null;

                    // Try to get expiry date (Tag 5F24)
                    byte[] expiryData = emvHelper.getTlv(0x5F24);
                    if (expiryData != null && expiryData.length >= 2) {
                        expiryDate = BytesUtil.bytes2HexString(expiryData);
                        Log.d(TAG, "Expiry date extracted: " + expiryDate);
                    }

                    // Try to get track2 data (Tag 57)
                    byte[] track2Data = emvHelper.getTlv(0x57);
                    if (track2Data != null && track2Data.length > 0) {
                        track2 = BytesUtil.bytes2HexString(track2Data);
                        Log.d(TAG, "Track2 extracted: " + track2);
                    }

                    // If we still don't have PAN, try alternative tags
                    if (pan == null || pan.isEmpty()) {
                        // Try Application PAN (Tag 5A)
                        byte[] altPanData = emvHelper.getTlv(0x5A);
                        if (altPanData != null) {
                            pan = BytesUtil.bytes2HexString(altPanData).replace("F", "");
                            Log.d(TAG, "Alternative PAN extracted: " + pan);
                        }
                    }

                    // Create card data
                    if (pan != null && !pan.isEmpty()) {
                        cardData = new CardData(CardData.EReturnType.OK, CardData.ECardType.IC);
                        cardData.setPan(pan);

                        if (expiryDate != null && !expiryDate.isEmpty()) {
                            cardData.setExpiryDate(expiryDate);
                        }

                        if (track2 != null && !track2.isEmpty()) {
                            cardData.setTrack2(track2);
                        }

                        Log.d(TAG, "IC Card data successfully extracted - PAN: " + pan);
                        setResult(cardData);
                    } else {
                        Log.e(TAG, "No PAN data found in EMV response");
                        // Try to extract from track2 if available
                        if (track2 != null && !track2.isEmpty()) {
                            try {
                                // Track2 format: PAN=EXPIRYSERVICECODE...
                                String track2String = track2;
                                int separatorIndex = track2String.indexOf("D"); // D or = separator
                                if (separatorIndex == -1) {
                                    separatorIndex = track2String.indexOf("=");
                                }

                                if (separatorIndex > 0) {
                                    String extractedPan = track2String.substring(0, separatorIndex);

                                    cardData = new CardData(CardData.EReturnType.OK, CardData.ECardType.IC);
                                    cardData.setPan(extractedPan);
                                    cardData.setTrack2(track2);

                                    if (separatorIndex + 4 < track2String.length()) {
                                        String extractedExpiry = track2String.substring(separatorIndex + 1, separatorIndex + 5);
                                        cardData.setExpiryDate(extractedExpiry);
                                    }

                                    Log.d(TAG, "PAN extracted from Track2: " + extractedPan);
                                    setResult(cardData);
                                    return;
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error extracting from track2: " + e.getMessage());
                            }
                        }

                        setResult(new CardData(CardData.EReturnType.OPEN_IC_RESET_ERR));
                    }

                } catch (Exception e) {
                    Log.e(TAG, "EMV processing error: " + e.getMessage(), e);
                    setResult(new CardData(CardData.EReturnType.OPEN_IC_RESET_ERR));
                }
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
// Add these methods to CardReader.java for debugging EMV data

    /**
     * Debug method to extract all available EMV TLV data
     */
    private void debugEmvTlvData(IEmv emvHelper) {
        Log.d(TAG, "=== EMV TLV Data Debug ===");

        // Common EMV tags to check
        int[] commonTags = {
                0x5A,   // Application Primary Account Number (PAN)
                0x5F24, // Application Expiration Date
                0x5F20, // Cardholder Name
                0x57,   // Track 2 Equivalent Data
                0x9F6E, // Visa Low-Value Payment (VLP) Supported Indicator
                0x9F07, // Application Usage Control
                0x9F08, // Application Version Number
                0x9F42, // Application Currency Code
                0x5F25, // Application Effective Date
                0x5F28, // Issuer Country Code
                0x9F12, // Application Preferred Name
                0x9F11, // Issuer Code Table Index
                0x9F4A, // Static Data Authentication Tag List
                0x82,   // Application Interchange Profile
                0x84,   // Dedicated File (DF) Name
                0x9F38, // Processing Options Data Object List (PDOL)
                0x9F36, // Application Transaction Counter (ATC)
                0x9F26, // Application Cryptogram
                0x9F27, // Cryptogram Information Data
                0x9F10, // Issuer Application Data
                0x9F37, // Unpredictable Number
                0x9F35, // Terminal Type
                0x9F33, // Terminal Capabilities
                0x9F40, // Additional Terminal Capabilities
                0x9F39, // Point-of-Service (POS) Entry Mode
                0x9F41, // Transaction Sequence Counter
                0x9A,   // Transaction Date
                0x9C,   // Transaction Type
                0x9F02, // Amount, Authorized (Numeric)
                0x9F03, // Amount, Other (Numeric)
                0x9F1A, // Terminal Country Code
                0x5F2A, // Transaction Currency Code
                0x9F21, // Transaction Time
                0x9F34, // Cardholder Verification Method (CVM) Results
                0x9F0D, // Issuer Action Code - Default
                0x9F0E, // Issuer Action Code - Denial
                0x9F0F  // Issuer Action Code - Online
        };

        for (int tag : commonTags) {
            try {
                byte[] data = emvHelper.getTlv(tag);
                if (data != null && data.length > 0) {
                    String hexData = BytesUtil.bytes2HexString(data);
                    String tagHex = String.format("%X", tag);
                    Log.d(TAG, String.format("Tag %s (%s): %s", tagHex, getTagDescription(tag), hexData));

                    // Special handling for specific tags
                    if (tag == 0x5A) { // PAN
                        String pan = hexData.replace("F", "");
                        Log.d(TAG, "PAN (processed): " + pan);
                    } else if (tag == 0x5F24) { // Expiry Date
                        Log.d(TAG, "Expiry Date (YYMMDD): " + hexData);
                    } else if (tag == 0x57) { // Track2
                        Log.d(TAG, "Track2 Data: " + hexData);
                    } else if (tag == 0x5F20) { // Cardholder Name
                        try {
                            String name = new String(data, "UTF-8").trim();
                            Log.d(TAG, "Cardholder Name: " + name);
                        } catch (Exception e) {
                            Log.d(TAG, "Cardholder Name (hex): " + hexData);
                        }
                    }
                }
            } catch (Exception e) {
                Log.w(TAG, "Error reading tag " + String.format("%X", tag) + ": " + e.getMessage());
            }
        }

        Log.d(TAG, "=== End EMV TLV Data Debug ===");
    }

    /**
     * Get description for EMV tags
     */
    private String getTagDescription(int tag) {
        switch (tag) {
            case 0x5A: return "Application Primary Account Number (PAN)";
            case 0x5F24: return "Application Expiration Date";
            case 0x5F20: return "Cardholder Name";
            case 0x57: return "Track 2 Equivalent Data";
            case 0x82: return "Application Interchange Profile";
            case 0x84: return "Dedicated File (DF) Name";
            case 0x9F07: return "Application Usage Control";
            case 0x9F08: return "Application Version Number";
            case 0x9F42: return "Application Currency Code";
            case 0x5F25: return "Application Effective Date";
            case 0x5F28: return "Issuer Country Code";
            case 0x9F12: return "Application Preferred Name";
            case 0x9F11: return "Issuer Code Table Index";
            case 0x9A: return "Transaction Date";
            case 0x9C: return "Transaction Type";
            case 0x9F02: return "Amount, Authorized";
            case 0x9F03: return "Amount, Other";
            case 0x9F1A: return "Terminal Country Code";
            case 0x5F2A: return "Transaction Currency Code";
            case 0x9F21: return "Transaction Time";
            default: return "Unknown";
        }
    }

    /**
     * Try multiple methods to extract PAN from EMV data
     */
    private String extractPanFromEmv(IEmv emvHelper) {
        String pan = null;

        // Method 1: Direct PAN tag (5A)
        byte[] panData = emvHelper.getTlv(0x5A);
        if (panData != null && panData.length > 0) {
            pan = BytesUtil.bytes2HexString(panData).replace("F", "");
            Log.d(TAG, "PAN found via tag 5A: " + pan);
            return pan;
        }

        // Method 2: Extract from Track2 (57)
        byte[] track2Data = emvHelper.getTlv(0x57);
        if (track2Data != null && track2Data.length > 0) {
            String track2Hex = BytesUtil.bytes2HexString(track2Data);
            Log.d(TAG, "Track2 hex: " + track2Hex);

            // Track2 format in hex: PAN + D + YYMM + service code + ...
            int separatorIndex = track2Hex.indexOf("D");
            if (separatorIndex > 0) {
                pan = track2Hex.substring(0, separatorIndex);
                Log.d(TAG, "PAN extracted from Track2: " + pan);
                return pan;
            }
        }

        // Method 3: Try other possible tags
        int[] alternativeTags = {0x9F1F, 0x9F20, 0x9F6E};
        for (int tag : alternativeTags) {
            byte[] data = emvHelper.getTlv(tag);
            if (data != null && data.length > 0) {
                String hexData = BytesUtil.bytes2HexString(data);
                Log.d(TAG, "Alternative tag " + String.format("%X", tag) + ": " + hexData);
                // Check if it looks like a PAN (numeric, 13-19 digits)
                if (hexData.matches("[0-9A-F]{26,38}")) { // 13-19 digits in hex
                    pan = hexData.replace("F", "");
                    if (pan.length() >= 13 && pan.length() <= 19) {
                        Log.d(TAG, "PAN found via alternative tag " + String.format("%X", tag) + ": " + pan);
                        return pan;
                    }
                }
            }
        }

        return null;
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