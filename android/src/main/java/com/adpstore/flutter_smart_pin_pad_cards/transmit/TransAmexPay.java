/*============================================================
 Module Name       : TransAmexPay.java
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

package com.adpstore.flutter_smart_pin_pad_cards.transmit;

import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.adpstore.flutter_smart_pin_pad_cards.AClssKernelBaseTrans;
import com.adpstore.flutter_smart_pin_pad_cards.AppLog;
import com.adpstore.flutter_smart_pin_pad_cards.DeviceServiceManagers;
import com.adpstore.flutter_smart_pin_pad_cards.entity.ClssOutComeData;
import com.adpstore.flutter_smart_pin_pad_cards.entity.EmvErrorCode;
import com.adpstore.flutter_smart_pin_pad_cards.entity.EmvOutCome;
import com.adpstore.flutter_smart_pin_pad_cards.enums.EAuthRespCode;
import com.adpstore.flutter_smart_pin_pad_cards.enums.ECVMStatus;
import com.adpstore.flutter_smart_pin_pad_cards.enums.EKernelType;
import com.adpstore.flutter_smart_pin_pad_cards.enums.EOnlineResult;
import com.adpstore.flutter_smart_pin_pad_cards.enums.EPinType;
import com.adpstore.flutter_smart_pin_pad_cards.enums.ETransStatus;
import com.adpstore.flutter_smart_pin_pad_cards.enums.ETransStep;
import com.adpstore.flutter_smart_pin_pad_cards.utils.DataUtils;
import com.adpstore.flutter_smart_pin_pad_cards.utils.TAGUtlis;
import com.topwise.cloudpos.aidl.emv.level2.AidlAmex;
import com.topwise.cloudpos.aidl.emv.level2.EmvCapk;
import com.topwise.cloudpos.aidl.emv.level2.PreProcResult;
import com.topwise.cloudpos.struct.BytesUtil;
import com.topwise.cloudpos.struct.TlvList;

/**
 * Creation date: 2021/6/21 on 11:20
 * Description: Amex kernel card reading process encapsulation
 * Author: Adam Permana
 */
public class TransAmexPay extends AClssKernelBaseTrans {
    private static final String TAG = TransAmexPay.class.getSimpleName();
    private AidlAmex amexPay = DeviceServiceManagers.getInstance().getL2Amex();

    public TransAmexPay() {

    }

    @Override
    public EmvOutCome StartKernelTransProc() {
        try {
            AppLog.d(TAG, "start TransAMEXPay =========== ");

            // Get preprocess result
            PreProcResult preProcResult = clssTransParam.getPreProcResult();
            if (preProcResult != null) {
                String buffer = BytesUtil.bytes2HexString(preProcResult.getAucReaderTTQ());
                if (buffer.contains("00000000")) { // default
                    preProcResult.setAucReaderTTQ(BytesUtil.hexString2Bytes("3600C000"));
                }
            }

            // Pass kernel type to payment app
            AppUpdateKernelType(EKernelType.KERNTYPE_AMEX);

            // Initialize amex kernel
            Log.d("Jeremy", "initialize begin" );
            int nRet = amexPay.initialize();
            Log.d("Jeremy", "initialize end "  + nRet);

            // Set final select data
            Log.d("Jeremy", "setFinalSelectData begin" );
            nRet = amexPay.setFinalSelectData(clssTransParam.getAucFinalSelectFCIdata(), clssTransParam.getnFinalSelectFCIdataLen());
            Log.d("Jeremy", "setFinalSelectData end" );
            AppLog.d(TAG, "setFinalSelectData nRet: " + nRet);
            if (nRet != EmvErrorCode.CLSS_OK) { // ERROR Occurs
                if (nRet == EmvErrorCode.CLSS_RESELECT_APP) {
                    nRet = entryL2.delCandListCurApp();
                    AppLog.d(TAG, "entryL2 delCandListCurApp nRet: " + nRet);
                    if (nRet == EmvErrorCode.CLSS_OK) {
                        return new EmvOutCome(EmvErrorCode.CLSS_RESELECT_APP, ETransStatus.SELECT_NEXT_AID, ETransStep.CLSS_KERNEL_SET_FINAL_SELECT_DATA);
                    } else {
                        return new EmvOutCome(EmvErrorCode.CLSS_USE_CONTACT, ETransStatus.TRY_ANOTHER_INTERFACE, ETransStep.CLSS_KERNEL_SET_FINAL_SELECT_DATA);
                    }
                } else {
                    return new EmvOutCome(nRet, ETransStatus.END_APPLICATION, ETransStep.CLSS_KERNEL_SET_FINAL_SELECT_DATA);
                }
            }

            // Get current aid and inform payment app
            String currentAid = getCurrentAid();
            if (TextUtils.isEmpty(currentAid)) {
                return new EmvOutCome(EmvErrorCode.CLSS_TERMINATE, ETransStatus.END_APPLICATION, ETransStep.CLSS_SET_AID_PARAMS_TO_KERNEL);
            }

            // Load AID TLV list by aid
            TlvList kernalList = AppGetKernalDataFromAidParam(currentAid);
            if (kernalList == null) {
                return new EmvOutCome(EmvErrorCode.CLSS_TERMINATE, ETransStatus.END_APPLICATION, ETransStep.CLSS_SET_AID_PARAMS_TO_KERNEL);
            }
            AppLog.d(TAG, "entryL2 AppGetKernalDataFromAidParam ==== ");

            // Set tlv data list into kernel
            byte [] kernalData = kernalList.getBytes();
            if (kernalData != null) {
                nRet = amexPay.setTLVDataList(kernalData, kernalData.length);
                AppLog.d(TAG, "setTLVDataList nRet: " + nRet);
                if (nRet != EmvErrorCode.CLSS_OK) {
                    return new EmvOutCome(EmvErrorCode.CLSS_TERMINATE, ETransStatus.END_APPLICATION, ETransStep.CLSS_SET_AID_PARAMS_TO_KERNEL);
                }
            }

            // Set 9F6E
            setTLV(0x9F6E, new byte[]{(byte)0x9C, (byte)0xA0, 0x00, 0x03} );

            // Set transParam and preProcResult
            nRet = amexPay.setTransData(clssTransParam.getTransParam(), preProcResult);
            AppLog.d(TAG, "setTransData nRet: " + nRet);

            // Callback app final Select aid
            if (!AppFinalSelectAid()) {
                return new EmvOutCome(EmvErrorCode.CLSS_TERMINATE, ETransStatus.END_APPLICATION, ETransStep.CLSS_SET_AID_PARAMS_TO_KERNEL);
            }

            // GPO
            byte[] dataBuf = new byte[1];
            Log.d("Jeremy", "gpoProc begin" );
            nRet = amexPay.gpoProc(dataBuf);
            Log.d("Jeremy", "gpoProc end  "  +nRet);
            AppLog.d(TAG, "AMEX gpoProc Transaction Path: " + dataBuf[0]);
            if (nRet != EmvErrorCode.CLSS_OK) {
                if (nRet == EmvErrorCode.CLSS_RESELECT_APP) {
                    nRet = entryL2.delCandListCurApp();
                    AppLog.d(TAG, "entryL2 delCandListCurApp nRet: " + nRet);
                    if (nRet == EmvErrorCode.CLSS_OK) {
                        return new EmvOutCome(EmvErrorCode.CLSS_RESELECT_APP, ETransStatus.SELECT_NEXT_AID, ETransStep.CLSS_KERNEL_GPO_PROC);
                    } else {
                        return new EmvOutCome(EmvErrorCode.CLSS_USE_CONTACT, ETransStatus.TRY_ANOTHER_INTERFACE, ETransStep.CLSS_KERNEL_GPO_PROC);
                    }
                } else {
                    return new EmvOutCome(nRet, ETransStatus.END_APPLICATION, ETransStep.CLSS_KERNEL_GPO_PROC);
                }
            }

            // Read card record data
            Log.d("Jeremy", "readData" );
            nRet = amexPay.readData();
            Log.d("Jeremy", "readData end " + nRet);
            Log.d("Jeremy", "============== end ========================" );
            if (nRet != EmvErrorCode.CLSS_OK) {
                if (nRet == EmvErrorCode.CLSS_RESELECT_APP) {
                    nRet = entryL2.delCandListCurApp();
                    AppLog.d(TAG, "entryL2 delCandListCurApp nRet: " + nRet);
                    if (nRet == EmvErrorCode.CLSS_OK) {
                        return new EmvOutCome(EmvErrorCode.CLSS_RESELECT_APP, ETransStatus.SELECT_NEXT_AID, ETransStep.CLSS_KERNEL_READ_DATA_PROC);
                    } else {
                        return new EmvOutCome(EmvErrorCode.CLSS_USE_CONTACT, ETransStatus.TRY_ANOTHER_INTERFACE, ETransStep.CLSS_KERNEL_READ_DATA_PROC);
                    }
                }else {
                    return new EmvOutCome(nRet, ETransStatus.END_APPLICATION, ETransStep.CLSS_KERNEL_READ_DATA_PROC);
                }
            }

            // Read card info
            byte[] aucTrack2 = getTLV(0x57);
            if (aucTrack2 != null) {
                // Get Card number
                String cardNo = getPan(BytesUtil.bytes2HexString(aucTrack2).split("F")[0]);
                if (!DataUtils.isNullString(cardNo)) {
                    // Request customer to confirm card info
                    if (!AppConfirmPan(cardNo)) {
                        return new EmvOutCome(EmvErrorCode.EMV_USER_CANCEL, ETransStatus.END_APPLICATION, ETransStep.CLSS_APP_CONFIRM_PAN);
                    }
                }
            }

            // If it's a simple process, return with CLSS_OK here.
            if (clssTransParam.isbSupSimpleProc()) {
                AppLog.d(TAG, "Simple process return : " + nRet);
                return new EmvOutCome(EmvErrorCode.CLSS_OK, ETransStatus.ONLINE_REQUEST, ETransStep.CLSS_KERNEL_TRANS_COMPLETE);
            }

            AppLog.d(TAG, "Transaction Path: " + dataBuf[0]);
            if (dataBuf[0] == EmvErrorCode.CLSS_TRANSPATH_EMV) { // EMV
                addCapk(currentAid); // Add current capk param to kernel
                nRet = amexPay.cardAuth(); // Offline data auth
                AppLog.d(TAG, "AMEX cardAuth nRet: " + nRet);
            } else if (dataBuf[0] == EmvErrorCode.CLSS_TRANSPATH_MAG) { // MAG
                AppLog.d(TAG, "Transaction Path  = CLSS_TRANSPATH_MAG");
            } else {
                return new EmvOutCome(EmvErrorCode.EMV_TERMINATED, ETransStatus.END_APPLICATION, ETransStep.CLSS_KERNEL_CARD_AUTH);
            }

            // Start transaction
            byte[] ucAcType = new byte[1];
            byte[] ucAdviceFlag = new byte[1];
            byte[] ucDelayAuth = new byte[1];
            nRet = amexPay.startTrans((byte) 0, ucAdviceFlag, ucAcType, ucDelayAuth);
            AppLog.d(TAG, "AMEX startTrans nRet: " + nRet);
            if (nRet != EmvErrorCode.CLSS_OK ) { // Error occurs
                if (nRet == EmvErrorCode.CLSS_DECLINE) {
                    AppRemovrCard();
                }
                if (nRet == EmvErrorCode.CLSS_RESELECT_APP) {
                    nRet = entryL2.delCandListCurApp();
                    AppLog.d(TAG, "entryL2 delCandListCurApp nRet: " + nRet);
                    if (nRet == EmvErrorCode.CLSS_OK) {
                        return new EmvOutCome(EmvErrorCode.CLSS_RESELECT_APP, ETransStatus.SELECT_NEXT_AID, ETransStep.CLSS_KERNEL_TRANS_PROC);
                    } else {
                        return new EmvOutCome(EmvErrorCode.CLSS_USE_CONTACT, ETransStatus.TRY_ANOTHER_INTERFACE, ETransStep.CLSS_KERNEL_TRANS_PROC);
                    }
                } else if (nRet == EmvErrorCode.CLSS_REFER_CONSUMER_DEVICE) {
                    return new EmvOutCome(nRet, ETransStatus.SEE_PHONE_TRY_AGAIN, ETransStep.CLSS_KERNEL_TRANS_PROC);
                } else {
                    return new EmvOutCome(nRet, ETransStatus.END_APPLICATION, ETransStep.CLSS_KERNEL_TRANS_PROC);
                }
            }
            if (nRet == EmvErrorCode.CLSS_OK || nRet == EmvErrorCode.CLSS_DECLINE) {
                AppRemovrCard();
            }

            // DF8129 tag  - If it need pwd
            clssOutComeData = getOutcomeData();
            AppLog.d(TAG, "AMEX clssOutComeData: " + clssOutComeData.toString());

            if (clssOutComeData.getUcOCCVM() == EmvErrorCode.CLSS_OC_ONLINE_PIN) { // CVM control force online pin
                // Online enciphered PIN
                emvPinEnter = AppReqImportPin(EPinType.ONLINE_PIN_REQ);
                if (ECVMStatus.ENTER_OK != emvPinEnter.getEcvmStatus()) {
                    return new EmvOutCome(EmvErrorCode.EMV_USER_CANCEL, ETransStatus.END_APPLICATION, ETransStep.CLSS_KERNEL_TRANS_CVM);
                }
            } else if (clssTransParam.isClssForceOnlinePin()){ // App control force online pin
                // Online enciphered PIN
                emvPinEnter = AppReqImportPin(EPinType.ONLINE_PIN_REQ);
                if (ECVMStatus.ENTER_OK != emvPinEnter.getEcvmStatus()) {
                    return new EmvOutCome(EmvErrorCode.EMV_USER_CANCEL, ETransStatus.END_APPLICATION, ETransStep.CLSS_KERNEL_TRANS_CVM);
                }
            }

            AppLog.d(TAG, "AMEX OutcomeData: " + clssOutComeData.toString());
            switch (clssOutComeData.getUcOCStatus()) {
                case EmvErrorCode.CLSS_OC_APPROVED: // OFFLINE_APPROVE
                    // offline success
                    AppLog.d(TAG, "AMEX OFFLINE_APPROVE");
                    return new EmvOutCome(EmvErrorCode.CLSS_OK, ETransStatus.OFFLINE_APPROVE, ETransStep.CLSS_KERNEL_TRANS_COMPLETE);
                case EmvErrorCode.CLSS_OC_ONLINE_REQUEST: // Request online
                    // online success
                    AppLog.d(TAG, "AMEX ONLINE_REQUEST");

                    // Request for online processing and get online response data from issue bank
                    emvOnlineResp = AppReqOnlineProc();
                    AppLog.d(TAG, "AMEX ONLINE_REQUEST emvOnlineResp " + emvOnlineResp.toString());

                    // Check response data and script
                    if (EOnlineResult.ONLINE_APPROVE == emvOnlineResp.geteOnlineResult()) {
                        if (emvOnlineResp.isExistAuthRespCode()) {
                            // Check 8A Authorisation Response Code
                            boolean transResSatus = EAuthRespCode.checkTransResSatus(convert.bcdToStr(emvOnlineResp.getAuthRespCode()), EKernelType.KERNTYPE_AMEX);
                            AppLog.d(TAG, "ARQC checkTransResSatus transResSatus " + transResSatus);
                            if (transResSatus) {
                                return new EmvOutCome(EmvErrorCode.CLSS_OK, ETransStatus.ONLINE_APPROVE, ETransStep.CLSS_KERNEL_TRANS_COMPLETE);
                            }
                        }
                    }
                    return new EmvOutCome(EmvErrorCode.CLSS_OK, ETransStatus.ONLINE_DECLINED, ETransStep.CLSS_KERNEL_TRANS_COMPLETE);
                case EmvErrorCode.CLSS_OC_DECLINED: // OFFLINE_DECLINED
                    // transaction reject
                    AppLog.d(TAG, "AAC transaction reject");
                    return new EmvOutCome(EmvErrorCode.CLSS_OK, ETransStatus.OFFLINE_DECLINED, ETransStep.CLSS_KERNEL_TRANS_COMPLETE);
                default:
                    return new EmvOutCome(EmvErrorCode.CLSS_TERMINATE, ETransStatus.END_APPLICATION, ETransStep.CLSS_KERNEL_TRANS_COMPLETE);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return new EmvOutCome(EmvErrorCode.CLSS_TERMINATE, ETransStatus.END_APPLICATION, ETransStep.CLSS_KERNEL_TRANS_COMPLETE);
    }

    @Override
    public byte[] getTLV(int tag) {
        AppLog.d(TAG, "getTLV tag : " + tag);
        byte [] aucVale = new byte[256];
        int [] aucLen  = new int[2];
        byte[] aucTag = TAGUtlis.tagFromInt(tag);
        AppLog.d(TAG, "getTLV aucTag : " + convert.bcdToStr(aucTag));
        try {
            if (EmvErrorCode.CLSS_OK == amexPay.getTLVDataList(aucTag,aucTag.length,256,aucVale,aucLen)){
                byte [] aucResp = new byte[aucLen[0]];
                System.arraycopy(aucVale, 0, aucResp, 0, aucLen[0]);
                AppLog.d(TAG, "getTLV aucResp : " + convert.bcdToStr(aucResp));
                return aucResp;            
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    @Override
    public boolean setTLV(int tag, byte[] datas) {
        if (datas == null) {
            AppLog.d(TAG, "vale data is null " );
            return false;
        }
        AppLog.d(TAG, "setTLV T : " + tag);
        byte[] aucTag = TAGUtlis.tagFromInt(tag);
        AppLog.d(TAG, "setTLV T : " + convert.bcdToStr(aucTag));
        byte[] aucLen = TAGUtlis.genLen(datas.length);
        AppLog.d(TAG, "setTLV L : " + convert.bcdToStr(aucLen));
        AppLog.d(TAG, "setTLV V : " + convert.bcdToStr(datas));
//        System.arraycopy(aid, 0, rid, 0, 5);
        byte[] aucTLV = new byte[aucTag.length + aucLen.length + datas.length];
        System.arraycopy(aucTag,0, aucTLV,0, aucTag.length);
        System.arraycopy(aucLen,0, aucTLV, aucTag.length, aucLen.length);
        System.arraycopy(datas,0, aucTLV, (aucTag.length + aucLen.length), datas.length);

        AppLog.d(TAG, "setTLV TLV : " + convert.bcdToStr(aucTLV));
        try {
            int nRet = amexPay.setTLVDataList(aucTLV, aucTLV.length);
            AppLog.d(TAG, "setTLVDataList nRet " + nRet);
            if (nRet == EmvErrorCode.CLSS_OK) {
                return true;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void getDebugInfo() {
        byte aucAssistInfo[] = new byte[4096];
        int nErrcode[] = new int[1];
        try {
            int nRet = amexPay.getDebugInfo(aucAssistInfo.length,aucAssistInfo,nErrcode);
            AppLog.e(TAG, "getDebugInfo nRet: " + nRet);
            AppLog.e(TAG, "getDebugInfo nErrcode: " + nErrcode[0]);
            AppLog.e(TAG, "getDebugInfo aucAssistInfo: " + new String(aucAssistInfo).trim());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    protected String getCurrentAid() {
        byte[] tlvVale = getTLV(0x4F);
        if (tlvVale !=null){
            AppLog.d(TAG, "getCurrentAid TAG 4F: " + convert.bcdToStr(tlvVale));
            return convert.bcdToStr(tlvVale);
        }
        return null;
    }

    /**
     *
     * @return
     */
    @Override
    protected ClssOutComeData getOutcomeData() {
        byte[] tlvOutCome = getTLV(0xDF8129);
        if (tlvOutCome !=null){
            AppLog.d(TAG, "getOutcomeData : " + convert.bcdToStr(tlvOutCome));
            return new ClssOutComeData(tlvOutCome);
        }
        return new ClssOutComeData();
    }

    /**
     * 添加CAPK到交易库
     */
    @Override
    protected boolean addCapk(String aid) {
        try{
            int res;
            AppLog.d(TAG, "addCapk ======== aid: " + aid);
            amexPay.delAllRevocList();
            amexPay.delAllCAPK();
            byte[] tlvCapkIndex = getTLV(0x8F);
            if (tlvCapkIndex != null && tlvCapkIndex.length == 0x01){
                EmvCapk emvCapk = AppFindCapkParamsProc(BytesUtil.hexString2Bytes(aid), tlvCapkIndex[0]);
                if (emvCapk != null) {
                    res = amexPay.addCAPK(emvCapk);
                    AppLog.d(TAG, "amexPay addCAPK res: " + res);
                    if (res == EmvErrorCode.CLSS_OK){
                        return true;
                    }
                }
            }
        }catch(RemoteException e){
            e.printStackTrace();
        }
        return false;
    }
}
