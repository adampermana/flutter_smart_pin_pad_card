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

import android.os.RemoteException;
import android.os.SystemClock;

import com.adpstore.flutter_smart_pin_pad_cards.entity.CardData;
import com.adpstore.flutter_smart_pin_pad_cards.utils.CardTimer;
import com.topwise.cloudpos.aidl.iccard.AidlICCard;
import com.topwise.cloudpos.aidl.magcard.AidlMagCard;
import com.topwise.cloudpos.aidl.rfcard.AidlRFCard;
import com.topwise.cloudpos.aidl.shellmonitor.AidlShellMonitor;
import com.topwise.cloudpos.aidl.shellmonitor.InstructionSendDataCallback;
import com.topwise.toptool.impl.TopTool;

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
     * @return
     */
    private boolean openMag(){
        try {
            return magCard.open();
        } catch (RemoteException e) {
            e.printStackTrace();
            AppLog.e(TAG,   "openMag: false ==============");
            return false;
        }
    }

    /**
     * close Mag
     * @return
     */
    private boolean closeMag(){
        try {
            return magCard.close();
        } catch (RemoteException e) {
            e.printStackTrace();
            AppLog.e(TAG,   "closeMag: false ==============");
            return false;
        }
    }

    /**
     * open IC
     * @return
     */
    private boolean openIc(){
        try {
            return icCard.open();
        } catch (RemoteException e) {
            e.printStackTrace();
            AppLog.e(TAG,   "openIc: false ==============");
            return false;
        }
    }

    /**
     * closeIC
     * @return
     */
    private boolean closeIc(){
        try {
            return icCard.close();
        } catch (RemoteException e) {
            e.printStackTrace();
            AppLog.e(TAG,   "closeIc: false ==============");
            return false;
        }
    }

    /**
     * open RF
     * @return
     */
    private boolean openRf(){
        try {
            return rfCard.open();
        } catch (RemoteException e) {
            e.printStackTrace();
            AppLog.e(TAG,   "openRf: false ==============");
            return false;
        }
    }

    /**
     * close RF
     * @return
     */
    private boolean closeRf(){
        try {
            return rfCard.close();
        } catch (RemoteException e) {
            e.printStackTrace();
            AppLog.e(TAG,   "closeRf: false ==============");
            return false;
        }
    }
    @Override
    public void startFindCard(boolean isMag, boolean isIcc, boolean isRf, int outtime,
                              onReadCardListener onReadCardListener) {
        AppLog.e(TAG,   "startFindCard: isMag= "+isMag + " isIcc="+isIcc + " isRf=" + isRf + " outtime=" +outtime);
        this.isMag = isMag;
        this.isIcc = isIcc;
        this.isRf  = isRf;
        this.onReadCardListener  = onReadCardListener;

        if (cardTimer != null) {
            cardTimer.cancel();
            cardTimer = null;
        }
        bCloseAll = false;
        cardTimer = new CardTimer(outtime,1);
        cardTimer.setTimeCountListener(new CardTimer.TickTimerListener() {
            @Override
            public void onFinish() {
                AppLog.e(TAG,   "CardTimer: onFinish ============== ");
                setResult(new CardData(CardData.EReturnType.OTHER_ERR));
                if (bCloseAll){
                    CloseAll();
                }
                return;
            }

            @Override
            public void onTick(long leftTime) {
                if (findCardThread != null) AppLog.e(TAG,   "FindCardThread ID onTick ==============" +findCardThread.getId() + "  isInterrupted " + findCardThread.isInterrupted());
                AppLog.e(TAG,   "CardTimer: onTick ============== " +leftTime);
                if (leftTime == 1) bCloseAll = true;
            }
        });
        cardTimer.start();

        isRunning = true;
        findCardThread = new FindCardThread();
        findCardThread.start();
        AppLog.e(TAG,   "FindCardThread ID ==============" +findCardThread.getId());
    }

    /**
     *
     */
    class FindCardThread extends Thread{
        @Override
        public void run() {
            CloseAll();
            SystemClock.sleep(20);
            //check and open
            if (isMag && !openMag()){
                if (onReadCardListener != null){
                    CloseAll();
                    setResult(new CardData(CardData.EReturnType.OPEN_MAG_ERR));
                    return;
                }
            }
            if (isIcc && !openIc()){
                if (onReadCardListener != null){
                    CloseAll();
                    setResult(new CardData(CardData.EReturnType.OPEN_IC_ERR));
                    return;
                }
            }
            if (isRf && !openRf()){
                if (onReadCardListener != null){
                    CloseAll();
                    setResult(new CardData(CardData.EReturnType.OPEN_RF_ERR));
                    return;
                }
            }
            SystemClock.sleep(20);

            while (true && !isInterrupted()){
                if (!isRunning){
                    break;
                }
                //mag
                if (isMag){
                    try {
                        byte startRead = readMag();
                        if (startRead == ICC_MAG_CARD || startRead == PURE_MAG_CARD){

                            byte[] firstTlvArray = readData((byte) MSR_TRACK_1);
                            byte[] secondTlvArray = readData((byte)MSR_TRACK_2);
                            byte[] thirdTlvArray = readData((byte)MSR_TRACK_3);

                            CloseAll();

                            if (firstTlvArray == null &&
                                    secondTlvArray == null &&
                                    thirdTlvArray == null ){
                                AppLog.e(TAG,   "Read mag data is null ==============" );
                                setResult(new CardData(CardData.EReturnType.OPEN_MAG_RESET_ERR));
                                return;
                            }

                            cardData = new CardData(CardData.EReturnType.OK, CardData.ECardType.MAG);
                            if (firstTlvArray != null){
                                AppLog.e(TAG,   "Read mag firstTlvArray ==============" + new String(firstTlvArray));
                                int realFirstLen = firstTlvArray.length - 2;
                                if (realFirstLen > 0) {
                                    byte[] realFirstByte = new byte[realFirstLen];
                                    System.arraycopy(firstTlvArray, 1, realFirstByte, 0, realFirstLen);
                                    cardData.setTrack1( new String(realFirstByte));
                                }
                            }
                            if (secondTlvArray != null){
                                AppLog.e(TAG,   "Read mag secondTlvArray ==============" + new String(secondTlvArray));
                                int realSecondLen = secondTlvArray.length - 2;
                                if (realSecondLen > 0) {
                                    byte[] realSecondByte = new byte[realSecondLen];
                                    System.arraycopy(secondTlvArray, 1, realSecondByte, 0, realSecondLen);
                                    String track2Data =  new String(realSecondByte);
                                    cardData.setTrack2( track2Data);
                                    int index  =  track2Data.indexOf("=");
                                    cardData.setPan(track2Data.substring(0,index));
                                    String expirydate = track2Data.substring(index+1,index+1+4);
                                    cardData.setExpiryDate(expirydate);
                                    String serviceCode = track2Data.substring(index+1+4,index+1+4+3);
                                    cardData.setServiceCode(serviceCode);
                                }

                            }
                            if (thirdTlvArray != null){
                                AppLog.e(TAG,   "Read mag thirdTlvArray ==============" + new String(thirdTlvArray));
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
                        AppLog.e(TAG,   "Read mag Exception ==============" + e.getMessage());
                        CloseAll();
                        setResult(new CardData(CardData.EReturnType.OPEN_MAG_RESET_ERR));
                        return;
                    }
                }
                //ic
                if (isIcc ){
                    try {

                        if (icCard.isExist()){
                            byte[] data = icCard.reset(0);
                            if (data != null && data.length > 0) {
                                closeMag();
                                closeRf();
                                AppLog.e(TAG,   "Read Icc SUCC==============");
                                setResult(new CardData(CardData.EReturnType.OK, CardData.ECardType.IC));
                                return;
                            }else {
                                CloseAll();
                                AppLog.e(TAG,   "Read Icc reset fail ==============");
                                setResult(new CardData(CardData.EReturnType.OPEN_IC_RESET_ERR));
                                return;
                            }
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        CloseAll();
                        AppLog.e(TAG,   "Read Icc Exception ==============" + e.getMessage());
                        setResult(new CardData(CardData.EReturnType.OPEN_IC_RESET_ERR));
                        return;
                    }
                }
                //rf
                if (isRf){
                    try {
                        int b = RFCardIsExist();
                        if (0x00 == b){
                            byte[] data = rfCard.reset(0);
                            if (data != null && data.length > 0) {
                                AppLog.e(TAG,   "Read Rf SUCC ==============");
                                closeMag();
                                closeIc();
                                setResult(new CardData(CardData.EReturnType.OK, CardData.ECardType.RF));
                                return;
                            }else {
                                CloseAll();
                                setResult(new CardData(CardData.EReturnType.OPEN_RF_RESET_ERR));
                                AppLog.e(TAG,   "Read Rf reset fail ==============");
                                return;
                            }
                        }else if ((byte) 0x93 == b){
                            if(onReadCardListener!= null) {
                                onReadCardListener.onNotification(CardData.EReturnType.RF_MULTI_CARD);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        CloseAll();
                        setResult(new CardData(CardData.EReturnType.OPEN_RF_RESET_ERR));
                        AppLog.e(TAG,   "Read Rf Exception ==============" + e.getMessage());
                        return;
                    }
                }
                SystemClock.sleep(20);
            }
        }
    }

    /**
     *
     * @return
     * @throws Exception
     */
    private byte readMag() throws Exception {
        AppLog.e(TAG,   "Read readMag ==============" );
        byte[] mBuff = new byte[]{(byte) 0x0b, (byte) 0xb8};
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        aidlShellMonitor.sendIns(6, (byte)0x68, (byte)0x04, (byte) 0x02, mBuff, new InstructionSendDataCallback.Stub() {
            @Override
            public void onReceiveData(byte resultCode, byte[] tlvArray) throws RemoteException {
                if (tlvArray != null && tlvArray.length > 0){
                    AppLog.d(TAG, "readMag onReceiveData receive data: " + TopTool.getInstance().getConvert().bcdToStr(tlvArray));
                }
                AppLog.d(TAG, "readMag onReceiveData resultCode: " + resultCode );
                mResultCode = resultCode;
                mResultData = tlvArray;
                if (countDownLatch != null) countDownLatch.countDown();
            }
        });
        if (countDownLatch != null) countDownLatch.await();
        AppLog.e(TAG,   "Read readMag resultCode ============== "  + mResultCode);
        return mResultCode;
    }

    private byte RFCardIsExist() throws RemoteException {
        AppLog.e(TAG,   "Read RFCardIsExist ==============" );
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        aidlShellMonitor.sendIns(3, (byte)0x66, (byte) 0x03, (byte) 0x02, new byte[]{0x00, 0x11},new InstructionSendDataCallback.Stub() {
            @Override
            public void onReceiveData(byte resultCode, byte[] tlvArray)  {
                if (tlvArray != null && tlvArray.length > 0){
                    AppLog.d(TAG, "RFCardIsExist onReceiveData receive data: " + TopTool.getInstance().getConvert().bcdToStr(tlvArray));
                }
                AppLog.d(TAG, "RFCardIsExist onReceiveData resultCode: " + resultCode );
                mResultCode = resultCode;
                if (countDownLatch != null){
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
     *
     * @param inByte
     * @return
     * @throws Exception
     */
    private byte [] readData(byte inByte) throws Exception{
        AppLog.e(TAG,   "Read readData ==============" );
        byte[] mBuff = new byte[1];
        mBuff[0] = inByte;
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        aidlShellMonitor.sendIns(6, (byte)0x68, (byte)0x05, (byte) 0x01, mBuff, new InstructionSendDataCallback.Stub() {
            @Override
            public void onReceiveData(byte resultCode, byte[] tlvArray) throws RemoteException {
                if (tlvArray != null && tlvArray.length > 0){
                    AppLog.d(TAG, "readData onReceiveData receive data: " + TopTool.getInstance().getConvert().bcdToStr(tlvArray));
                }
                AppLog.d(TAG, "readData onReceiveData resultCode: " + resultCode );
                mResultCode = resultCode;
                mResultData = tlvArray;
                if (countDownLatch != null) countDownLatch.countDown();
            }
        });
        if (countDownLatch != null) countDownLatch.await();
        AppLog.e(TAG,   "Read readData resultCode ============== "  + mResultCode);
        return mResultCode == (byte) 0x00 ? mResultData : null;
    }

    @Override
    public void cancel() {
        AppLog.e(TAG,   "close ==== " );
        CloseAll();
        if (cardTimer != null){
            cardTimer.cancel();
            AppLog.e(TAG,   "close  cardTimer.cancel ==== ");
            cardTimer = null;
        }
        if (findCardThread != null && !findCardThread.isInterrupted()){
            findCardThread.interrupt();
            AppLog.e(TAG,   "close   findCardThread.interrupt ==== ");
            findCardThread = null;
        }
        isRunning = false;
        instance = null;
    }
    private void setResult(CardData cardData){
        isRunning = false;
        if (cardTimer != null){
            cardTimer.cancel();
            AppLog.e(TAG,   "setResult  cardTimer.cancel ==== ");
            cardTimer = null;
        }
        if (findCardThread != null && !findCardThread.isInterrupted()){
            findCardThread.interrupt();
            AppLog.e(TAG,   "setResult   findCardThread.interrupt ==== ");
            findCardThread = null;
        }
        if(onReadCardListener!= null) {
            onReadCardListener.getReadState(cardData);
        }
        onReadCardListener = null;
    }

    /**
     *
     */
    private void CloseAll(){
        closeIc();
        closeMag();
        closeRf();
        AppLog.e(TAG,   "CloseAll ===");
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
