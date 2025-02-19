/*============================================================
 Module Name       : CardReader.java
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

package com.adpstore.flutter_smart_pin_pad_cards;

import android.content.Context;
import android.os.Handler;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;

import com.adpstore.flutter_smart_pin_pad_cards.entity.CardData;
import com.adpstore.flutter_smart_pin_pad_cards.entity.EinputType;
import com.adpstore.flutter_smart_pin_pad_cards.entity.EmvAidParam;
import com.adpstore.flutter_smart_pin_pad_cards.entity.EmvOnlineResp;
import com.adpstore.flutter_smart_pin_pad_cards.entity.EmvOutCome;
import com.adpstore.flutter_smart_pin_pad_cards.entity.EmvPinEnter;
import com.adpstore.flutter_smart_pin_pad_cards.entity.EmvTransPraram;
import com.adpstore.flutter_smart_pin_pad_cards.entity.TransData;
import com.adpstore.flutter_smart_pin_pad_cards.enums.EKernelType;
import com.adpstore.flutter_smart_pin_pad_cards.enums.EPinType;
import com.adpstore.flutter_smart_pin_pad_cards.enums.ETransStatus;
import com.adpstore.flutter_smart_pin_pad_cards.param.AidParam;
import com.adpstore.flutter_smart_pin_pad_cards.param.CapkParam;
import com.adpstore.flutter_smart_pin_pad_cards.param.SysParam;
import com.adpstore.flutter_smart_pin_pad_cards.utils.CardTimer;
import com.topwise.cloudpos.aidl.emv.level2.Combination;
import com.topwise.cloudpos.aidl.emv.level2.EmvCandidateItem;
import com.topwise.cloudpos.aidl.emv.level2.EmvCapk;
import com.topwise.cloudpos.aidl.emv.level2.EmvKernelConfig;
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

public class CardReader implements ICardReader {
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

    private Context context; // Add this field at class level


    private AidlMagCard magCard = DeviceServiceManagers.getInstance().getMagCardReader();
    private AidlICCard icCard = DeviceServiceManagers.getInstance().getICCardReader();
    private AidlRFCard rfCard = DeviceServiceManagers.getInstance().getRfCardReader();
    private AidlShellMonitor aidlShellMonitor = DeviceServiceManagers.getInstance().getShellMonitor();

    private CardReader() {
    }

    public synchronized static CardReader getInstance() {
        if (instance == null) {
            instance = new CardReader();
        }
        return instance;
    }

    /**
     * open MAG
     *
     * @return
     */
    private boolean openMag() {
        try {
            return magCard.open();
        } catch (RemoteException e) {
            e.printStackTrace();
            AppLog.e(TAG, "openMag: false ==============");
            return false;
        }
    }

    public void setContext(Context context) {
        if (context == null) {
            AppLog.e(TAG, "Attempting to set null context");
            return;
        }
        this.context = context;
        AppLog.d(TAG, "Context set to CardReader");

        // Initialize AID params when context is set
        try {
            // Initialize AID params
            AidParam aidParam = new AidParam();
            aidParam.init(context);
            aidParam.saveAll();
            AppLog.d(TAG, "Successfully initialized AID params");

            // Initialize CAPK params
            CapkParam capkParam = new CapkParam();
            capkParam.init(context);
            capkParam.saveAll();
            AppLog.d(TAG, "Successfully initialized CAPK params");
        } catch (Exception e) {
            AppLog.e(TAG, "Error initializing params: " + e.getMessage());
        }
    }

    /**
     * close Mag
     *
     * @return
     */
    private boolean closeMag() {
        try {
            return magCard.close();
        } catch (RemoteException e) {
            e.printStackTrace();
            AppLog.e(TAG, "closeMag: false ==============");
            return false;
        }
    }

    /**
     * open IC
     *
     * @return
     */
    private boolean openIc() {
        try {
            return icCard.open();
        } catch (RemoteException e) {
            e.printStackTrace();
            AppLog.e(TAG, "openIc: false ==============");
            return false;
        }
    }

    /**
     * closeIC
     *
     * @return
     */
    private boolean closeIc() {
        try {
            return icCard.close();
        } catch (RemoteException e) {
            e.printStackTrace();
            AppLog.e(TAG, "closeIc: false ==============");
            return false;
        }
    }

    /**
     * open RF
     *
     * @return
     */
    private boolean openRf() {
        try {
            return rfCard.open();
        } catch (RemoteException e) {
            e.printStackTrace();
            AppLog.e(TAG, "openRf: false ==============");
            return false;
        }
    }

    /**
     * close RF
     *
     * @return
     */
    private boolean closeRf() {
        try {
            return rfCard.close();
        } catch (RemoteException e) {
            e.printStackTrace();
            AppLog.e(TAG, "closeRf: false ==============");
            return false;
        }
    }

    private void setupTerminalInfo(EmvTerminalInfo terminalInfo) {
        terminalInfo.setUcTerminalEntryMode((byte) 0x05); // Contact IC card mode
    }

    @Override
    public void startFindCard(boolean isMag, boolean isIcc, boolean isRf, int outtime,
                              onReadCardListener onReadCardListener) {
        AppLog.e(TAG, "startFindCard: isMag= " + isMag + " isIcc=" + isIcc + " isRf=" + isRf + " outtime=" + outtime);
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
                AppLog.e(TAG, "CardTimer: onFinish ============== ");
                setResult(new CardData(CardData.EReturnType.OTHER_ERR));
                if (bCloseAll) {
                    CloseAll();
                }
                return;
            }

            @Override
            public void onTick(long leftTime) {
                if (findCardThread != null)
                    AppLog.e(TAG, "FindCardThread ID onTick ==============" + findCardThread.getId() + "  isInterrupted " + findCardThread.isInterrupted());
                AppLog.e(TAG, "CardTimer: onTick ============== " + leftTime);
                if (leftTime == 1) bCloseAll = true;
            }
        });
        cardTimer.start();

        isRunning = true;
        findCardThread = new FindCardThread();
        findCardThread.start();
        AppLog.e(TAG, "FindCardThread ID ==============" + findCardThread.getId());
    }

    /**
     *
     */
    class FindCardThread extends Thread {
        private IEmv emv;
        private TransData transData;
        private Handler handler;
        private Context context;


        @Override
        public void run() {
            CloseAll();
            SystemClock.sleep(20);
            //check and open
            if (isMag && !openMag()) {
                if (onReadCardListener != null) {
                    CloseAll();
                    setResult(new CardData(CardData.EReturnType.OPEN_MAG_ERR));
                    return;
                }
            }
            if (isIcc && !openIc()) {
//                gotoEmv();
                if (onReadCardListener != null) {
                    CloseAll();
                    setResult(new CardData(CardData.EReturnType.OPEN_IC_ERR));
                    return;
                }
            }
            if (isRf && !openRf()) {
                if (onReadCardListener != null) {
                    CloseAll();
                    setResult(new CardData(CardData.EReturnType.OPEN_RF_ERR));
                    return;
                }
            }
            SystemClock.sleep(20);

            while (true && !isInterrupted()) {
                if (!isRunning) {
                    break;
                }
                //mag
                if (isMag) {
                    try {
                        byte startRead = readMag();
                        if (startRead == ICC_MAG_CARD || startRead == PURE_MAG_CARD) {

                            byte[] firstTlvArray = readData((byte) MSR_TRACK_1);
                            byte[] secondTlvArray = readData((byte) MSR_TRACK_2);
                            byte[] thirdTlvArray = readData((byte) MSR_TRACK_3);

                            CloseAll();

                            if (firstTlvArray == null &&
                                    secondTlvArray == null &&
                                    thirdTlvArray == null) {
                                AppLog.e(TAG, "Read mag data is null ==============");
                                setResult(new CardData(CardData.EReturnType.OPEN_MAG_RESET_ERR));
                                return;
                            }

                            cardData = new CardData(CardData.EReturnType.OK, CardData.ECardType.MAG);
                            if (firstTlvArray != null) {
                                AppLog.e(TAG, "Read mag firstTlvArray ==============" + new String(firstTlvArray));
                                int realFirstLen = firstTlvArray.length - 2;
                                if (realFirstLen > 0) {
                                    byte[] realFirstByte = new byte[realFirstLen];
                                    System.arraycopy(firstTlvArray, 1, realFirstByte, 0, realFirstLen);
                                    cardData.setTrack1(new String(realFirstByte));
                                }
                            }
                            if (secondTlvArray != null) {
                                AppLog.e(TAG, "Read mag secondTlvArray ==============" + new String(secondTlvArray));
                                int realSecondLen = secondTlvArray.length - 2;
                                if (realSecondLen > 0) {
                                    byte[] realSecondByte = new byte[realSecondLen];
                                    System.arraycopy(secondTlvArray, 1, realSecondByte, 0, realSecondLen);
                                    String track2Data = new String(realSecondByte);
                                    cardData.setTrack2(track2Data);
                                    int index = track2Data.indexOf("=");
                                    cardData.setPan(track2Data.substring(0, index));
                                    String expirydate = track2Data.substring(index + 1, index + 1 + 4);
                                    cardData.setExpiryDate(expirydate);
                                    String serviceCode = track2Data.substring(index + 1 + 4, index + 1 + 4 + 3);
                                    cardData.setServiceCode(serviceCode);
                                }

                            }
                            if (thirdTlvArray != null) {
                                AppLog.e(TAG, "Read mag thirdTlvArray ==============" + new String(thirdTlvArray));
                                int realThirdLen = thirdTlvArray.length - 2;
                                if (realThirdLen > 0) {
                                    byte[] realThirdByte = new byte[realThirdLen];
                                    System.arraycopy(thirdTlvArray, 1, realThirdByte, 0, realThirdLen);
                                    cardData.setTrack3(new String(realThirdByte));
                                }

                            }
                            setResult(cardData);
                            return;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        AppLog.e(TAG, "Read mag Exception ==============" + e.getMessage());
                        CloseAll();
                        setResult(new CardData(CardData.EReturnType.OPEN_MAG_RESET_ERR));
                        return;
                    }
                }
                //ic
                // Di dalam class FindCardThread, update bagian IC card:

                if (isIcc) {
                    try {
                        if (icCard.isExist()) {
                            byte[] data = icCard.reset(0);
                            if (data != null && data.length > 0) {
                                closeMag();
                                closeRf();

                                // Inisialisasi EMV
                                IEmv emvHelper = DeviceServiceManagers.getInstance().getEmvHelper();
                                emvHelper.init(EinputType.CT);

                                // Set Process Listener
                                // Update bagian ITransProcessListener di dalam handleStartCardReading:

                                // Di dalam method startCardReading, update implementasi ITransProcessListener

                                emvHelper.setProcessListener(new ITransProcessListener() {
                                    @Override
                                    public int onReqAppAidSelect(String[] aids) {
                                        // Default: return index 0 if available
                                        if (aids != null && aids.length > 0) {
                                            AppLog.d(TAG, "Selecting AID index: 0");
                                            return 0;  // Return index of selected AID
                                        }
                                        return -1;  // No selection
                                    }

                                    @Override
                                    public void onUpToAppEmvCandidateItem(EmvCandidateItem emvCandidateItem) {
                                        // Handle selected candidate item notification
                                        if (emvCandidateItem != null) {
                                            AppLog.d(TAG, "Selected candidate item: " + emvCandidateItem.toString());
                                        }
                                    }

                                    @Override
                                    public void onUpToAppKernelType(EKernelType eKernelType) {
                                        // Handle kernel type notification
                                        AppLog.d(TAG, "Kernel type: " + eKernelType.toString());
                                    }

                                    @Override
                                    public boolean onReqFinalAidSelect() {
                                        // Allow final AID selection
                                        return true;
                                    }

                                    @Override
                                    public boolean onConfirmCardInfo(String cardNo) {
                                        // Auto confirm card number
                                        AppLog.d(TAG, "Confirming card number: " + cardNo);
                                        return true;
                                    }

                                    @Override
                                    public EmvPinEnter onReqGetPinProc(EPinType pinType, int leftTimes) {
                                        // Handle PIN entry request
                                        AppLog.d(TAG, "PIN request - Type: " + pinType + ", Tries left: " + leftTimes);
                                        return null; // Return PIN data if needed
                                    }

                                    @Override
                                    public boolean onDisplayPinVerifyStatus(int PinTryCounter) {
                                        // Display PIN verification status
                                        AppLog.d(TAG, "PIN try counter: " + PinTryCounter);
                                        return true;
                                    }

                                    @Override
                                    public boolean onReqUserAuthProc(int certype, String certnumber) {
                                        // Handle certificate authorization
                                        AppLog.d(TAG, "Certificate auth - Type: " + certype + ", Number: " + certnumber);
                                        return true;
                                    }

                                    @Override
                                    public EmvOnlineResp onReqOnlineProc() {
                                        // Handle online processing request
                                        return null;
                                    }

                                    @Override
                                    public boolean onSecCheckCardProc() {
                                        // Handle second card check
                                        return false;
                                    }

                                    @Override
                                    public List<Combination> onLoadCombinationParam() {
                                        List<Combination> combinations = new ArrayList<>();
                                        try {
                                            // Add JCB combination
                                            Combination jcb = new Combination();
                                            jcb.setUcAidLen((byte) 7);
                                            jcb.setAucAID(new byte[]{(byte) 0xA0, 0x00, 0x00, 0x00, 0x65, 0x10, 0x10});
                                            jcb.setUcKernIDLen((byte) 1);
                                            jcb.setAucKernelID(new byte[]{(byte) 0x0B});
                                            combinations.add(jcb);

                                            // Add Visa combination
                                            Combination visa = new Combination();
                                            visa.setUcAidLen((byte) 7);
                                            visa.setAucAID(new byte[]{(byte) 0xA0, 0x00, 0x00, 0x00, 0x03, 0x10, 0x10});
                                            visa.setUcKernIDLen((byte) 1);
                                            visa.setAucKernelID(new byte[]{(byte) 0x03});
                                            combinations.add(visa);

                                            // Add Mastercard combination
                                            Combination mc = new Combination();
                                            mc.setUcAidLen((byte) 7);
                                            mc.setAucAID(new byte[]{(byte) 0xA0, 0x00, 0x00, 0x00, 0x04, 0x10, 0x10});
                                            mc.setUcKernIDLen((byte) 1);
                                            mc.setAucKernelID(new byte[]{(byte) 0x02});
                                            combinations.add(mc);

                                        } catch (Exception e) {
                                            AppLog.e(TAG, "Error creating combinations: " + e.getMessage());
                                        }
                                        return combinations;
                                    }

                                    @Override
                                    public EmvAidParam onFindCurAidParamProc(String sAid) {
                                        // Load AID parameters from aid.xml
                                        try {
                                            AidParam aidParam = new AidParam();
                                            aidParam.init(context);
                                            aidParam.saveAll();
                                            EmvAidParam emvAidParam = new EmvAidParam();
                                            // Set AID parameters based on sAid
                                            return emvAidParam;
                                        } catch (Exception e) {
                                            AppLog.e(TAG, "Error loading AID param: " + e.getMessage());
                                            return null;
                                        }
                                    }

                                    @Override
                                    public void onRemoveCardProc() {
                                        // Handle card removal notification
                                        AppLog.d(TAG, "Card removal requested");
                                    }

                                    @Override
                                    public EmvCapk onFindIssCapkParamProc(String sAid, byte bCapkIndex) {
                                        // Load CAPK based on AID and index
                                        try {
                                            CapkParam capkParam = new CapkParam();
                                            capkParam.init(context);
                                            capkParam.saveAll();
                                            EmvCapk capk = new EmvCapk();
                                            // Set CAPK parameters based on sAid and bCapkIndex
                                            return capk;
                                        } catch (Exception e) {
                                            AppLog.e(TAG, "Error loading CAPK: " + e.getMessage());
                                            return null;
                                        }
                                    }
                                });

                                // Set terminal info
                                EmvTerminalInfo terminalInfo = new EmvTerminalInfo();
                                terminalInfo.setUcTerminalType((byte) 0x22);  // Terminal Type
                                terminalInfo.setUcTerminalEntryMode((byte) 0x05);  // IC + magstripe
                                emvHelper.setTerminalInfo(terminalInfo);

                                // Set transaction parameters
                                EmvTransPraram transParam = new EmvTransPraram((byte) 0x00);  // Purchase
//                                transParam.setAmount(0L);  // Amount in cents
                                transParam.getAucUnNumber();
                                transParam.getCountryCode(SysParam.COUNTRY_CODE);

//                                transParam.setAucTransDate("230101");  // YYMMDD
//                                transParam.setAucTransTime("235959");  // HHMMSS
                                emvHelper.setTransPraram(transParam);

                                // Start EMV process
                                EmvOutCome emvOutCome = emvHelper.StartEmvProcess();
                                AppLog.d(TAG, "EMV Process result: " + emvOutCome.toString());

                                if (emvOutCome.geteTransStatus() == ETransStatus.ONLINE_APPROVE ||
                                        emvOutCome.geteTransStatus() == ETransStatus.OFFLINE_APPROVE) {

                                    // Get card data
                                    cardData = new CardData(CardData.EReturnType.OK, CardData.ECardType.IC);

                                    // Get PAN
                                    byte[] panData = emvHelper.getTlv(0x5A);
                                    if (panData != null && panData.length > 0) {
                                        String pan = BytesUtil.bytes2HexString(panData).replace("F", "");
                                        cardData.setPan(pan);
                                        AppLog.d(TAG, "Card PAN: " + pan);
                                    }

                                    // Get expiry date
                                    byte[] expiryData = emvHelper.getTlv(0x5F24);
                                    if (expiryData != null && expiryData.length >= 2) {
                                        String expiry = BytesUtil.bytes2HexString(expiryData);
                                        cardData.setExpiryDate(expiry);
                                        AppLog.d(TAG, "Expiry date: " + expiry);
                                    }

                                    // Get track2
                                    byte[] track2Data = emvHelper.getTlv(0x57);
                                    if (track2Data != null && track2Data.length > 0) {
                                        String track2 = BytesUtil.bytes2HexString(track2Data);
                                        cardData.setTrack2(track2);
                                        AppLog.d(TAG, "Track2: " + track2);
                                    }

                                    setResult(cardData);
                                } else {
                                    AppLog.e(TAG, "EMV process failed: " + emvOutCome.geteTransStatus());
                                    setResult(new CardData(CardData.EReturnType.OPEN_IC_RESET_ERR));
                                }
                                return;
                            } else {
                                CloseAll();
                                AppLog.e(TAG, "IC Card reset failed");
                                setResult(new CardData(CardData.EReturnType.OPEN_IC_RESET_ERR));
                                return;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        CloseAll();
                        AppLog.e(TAG, "IC Card Exception: " + e.getMessage());
                        setResult(new CardData(CardData.EReturnType.OPEN_IC_RESET_ERR));
                        return;
                    }
                }
                //rf
                if (isRf) {
                    try {
                        int b = RFCardIsExist();
                        if (0x00 == b) {
                            byte[] data = rfCard.reset(0);
                            if (data != null && data.length > 0) {
                                AppLog.e(TAG, "Read Rf SUCC ==============");
                                closeMag();
                                closeIc();
                                setResult(new CardData(CardData.EReturnType.OK, CardData.ECardType.RF));
                                return;
                            } else {
                                CloseAll();
                                setResult(new CardData(CardData.EReturnType.OPEN_RF_RESET_ERR));
                                AppLog.e(TAG, "Read Rf reset fail ==============");
                                return;
                            }
                        } else if ((byte) 0x93 == b) {
                            if (onReadCardListener != null) {
                                onReadCardListener.onNotification(CardData.EReturnType.RF_MULTI_CARD);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        CloseAll();
                        setResult(new CardData(CardData.EReturnType.OPEN_RF_RESET_ERR));
                        AppLog.e(TAG, "Read Rf Exception ==============" + e.getMessage());
                        return;
                    }
                }
                SystemClock.sleep(20);
            }
        }

//        private void gotoEmv() {
//            {
//
//                /**********1，init Emv Terminal data ************/
//                EmvTerminalInfo emvTerminalInfo = EmvResultUtlis.setEmvTerminalInfo();
//
//                /**********2，init Emv transaction params ************/
//                EmvTransPraram emvTransPraram = new EmvTransPraram(EmvTags.checkKernelTransType(transData));
//                String transCurCode = SysParam.COUNTRY_CODE;
//                emvTransPraram.setAucTransCurCode(transCurCode);
//
//                /**********3，init Emv kernel config  ************/
//                EmvKernelConfig emvKernelConfig = EmvResultUtlis.setEmvKernelConfig();
//
//                emv = DeviceServiceManagers.getInstance().getEmvHelper();
//                emv.setProcessListener(new EmvTransProcessImpl(context, transData, emv, handler));
//                emv.setTerminalInfo(emvTerminalInfo);
//                emv.setTransPraram(emvTransPraram);
//                emv.setKernelConfig(emvKernelConfig);
//
//                /**********4，start Emv process  ************/
//                EmvOutCome emvOutCome = emv.StartEmvProcess();
//
//                /**********5，handle Emv result  ************/
//                if (ETransStatus.ONLINE_APPROVE == emvOutCome.geteTransStatus() ||
//                        ETransStatus.OFFLINE_APPROVE == emvOutCome.geteTransStatus()) {
//                    AppLog.i(TAG, "Emv Process Success");
//                } else if (ETransStatus.ONLINE_REQUEST == emvOutCome.geteTransStatus()) {
//                    AppLog.i(TAG, "Emv Process Success");
//                } else {
//                    AppLog.e(TAG, "Emv Process fail");
//                }
//            }
//        }

//        public void onInitCAPK() {
//            try {
//                List<String> list;
//                CapkParam capkParam = new CapkParam();
//                capkParam.init(context);
//                capkParam.saveAll();
//
//                AidParam aidParam = new AidParam();
//                aidParam.init(context);
//                aidParam.saveAll();
//            } catch (Exception e) {
//                Log.e(TAG, "Error initializing CAPK: " + e.getMessage());
//            }
//        }
    }

    /**
     * @return
     * @throws Exception
     */
    private byte readMag() throws Exception {
        AppLog.e(TAG, "Read readMag ==============");
        byte[] mBuff = new byte[]{(byte) 0x0b, (byte) 0xb8};
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        aidlShellMonitor.sendIns(6, (byte) 0x68, (byte) 0x04, (byte) 0x02, mBuff, new InstructionSendDataCallback.Stub() {
            @Override
            public void onReceiveData(byte resultCode, byte[] tlvArray) throws RemoteException {
                if (tlvArray != null && tlvArray.length > 0) {
                    AppLog.d(TAG, "readMag onReceiveData receive data: " + TopTool.getInstance().getConvert().bcdToStr(tlvArray));
                }
                AppLog.d(TAG, "readMag onReceiveData resultCode: " + resultCode);
                mResultCode = resultCode;
                mResultData = tlvArray;
                if (countDownLatch != null) countDownLatch.countDown();
            }
        });
        if (countDownLatch != null) countDownLatch.await();
        AppLog.e(TAG, "Read readMag resultCode ============== " + mResultCode);
        return mResultCode;
    }

    private byte RFCardIsExist() throws RemoteException {
        AppLog.e(TAG, "Read RFCardIsExist ==============");
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        aidlShellMonitor.sendIns(3, (byte) 0x66, (byte) 0x03, (byte) 0x02, new byte[]{0x00, 0x11}, new InstructionSendDataCallback.Stub() {
            @Override
            public void onReceiveData(byte resultCode, byte[] tlvArray) {
                if (tlvArray != null && tlvArray.length > 0) {
                    AppLog.d(TAG, "RFCardIsExist onReceiveData receive data: " + TopTool.getInstance().getConvert().bcdToStr(tlvArray));
                }
                AppLog.d(TAG, "RFCardIsExist onReceiveData resultCode: " + resultCode);
                mResultCode = resultCode;
                if (countDownLatch != null) {
                    countDownLatch.countDown();
                }
            }
        });

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return mResultCode;
    }


    /**
     * @param inByte
     * @return
     * @throws Exception
     */
    private byte[] readData(byte inByte) throws Exception {
        AppLog.e(TAG, "Read readData ==============");
        byte[] mBuff = new byte[1];
        mBuff[0] = inByte;
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        aidlShellMonitor.sendIns(6, (byte) 0x68, (byte) 0x05, (byte) 0x01, mBuff, new InstructionSendDataCallback.Stub() {
            @Override
            public void onReceiveData(byte resultCode, byte[] tlvArray) throws RemoteException {
                if (tlvArray != null && tlvArray.length > 0) {
                    AppLog.d(TAG, "readData onReceiveData receive data: " + TopTool.getInstance().getConvert().bcdToStr(tlvArray));
                }
                AppLog.d(TAG, "readData onReceiveData resultCode: " + resultCode);
                mResultCode = resultCode;
                mResultData = tlvArray;
                if (countDownLatch != null) countDownLatch.countDown();
            }
        });
        if (countDownLatch != null) countDownLatch.await();
        AppLog.e(TAG, "Read readData resultCode ============== " + mResultCode);
        return mResultCode == (byte) 0x00 ? mResultData : null;
    }

    @Override
    public void cancel() {
        AppLog.e(TAG, "close ==== ");
        CloseAll();
        if (cardTimer != null) {
            cardTimer.cancel();
            AppLog.e(TAG, "close  cardTimer.cancel ==== ");
            cardTimer = null;
        }
        if (findCardThread != null && !findCardThread.isInterrupted()) {
            findCardThread.interrupt();
            AppLog.e(TAG, "close   findCardThread.interrupt ==== ");
            findCardThread = null;
        }
        isRunning = false;
        instance = null;
    }

    private void setResult(CardData cardData) {
        isRunning = false;
        if (cardTimer != null) {
            cardTimer.cancel();
            AppLog.e(TAG, "setResult  cardTimer.cancel ==== ");
            cardTimer = null;
        }
        if (findCardThread != null && !findCardThread.isInterrupted()) {
            findCardThread.interrupt();
            AppLog.e(TAG, "setResult   findCardThread.interrupt ==== ");
            findCardThread = null;
        }
        if (onReadCardListener != null) {
            onReadCardListener.getReadState(cardData);
        }
        onReadCardListener = null;
    }

    /**
     *
     */
    private void CloseAll() {
        closeIc();
        closeMag();
        closeRf();
        AppLog.e(TAG, "CloseAll ===");
    }

    public interface onReadCardListener {

        /**
         * Success to read card
         *
         * @param cardData
         */
        void getReadState(CardData cardData);

        /**
         * Notice app about return type
         *
         * @param eReturnType
         */
        void onNotification(CardData.EReturnType eReturnType);
    }

}
