package com.adpstore.flutter_smart_pin_pad_cards.emv;

import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.adpstore.flutter_smart_pin_pad_cards.emv.entity.ClssOutComeData;
import com.adpstore.flutter_smart_pin_pad_cards.emv.entity.EmvErrorCode;
import com.adpstore.flutter_smart_pin_pad_cards.emv.entity.EmvOutCome;
import com.adpstore.flutter_smart_pin_pad_cards.emv.enums.EAuthRespCode;
import com.adpstore.flutter_smart_pin_pad_cards.emv.enums.ECVMStatus;
import com.adpstore.flutter_smart_pin_pad_cards.emv.enums.EKernelType;
import com.adpstore.flutter_smart_pin_pad_cards.emv.enums.EOnlineResult;
import com.adpstore.flutter_smart_pin_pad_cards.emv.enums.EPinType;
import com.adpstore.flutter_smart_pin_pad_cards.emv.enums.ETransStatus;
import com.adpstore.flutter_smart_pin_pad_cards.emv.enums.ETransStep;
import com.adpstore.flutter_smart_pin_pad_cards.emv.utils.DataUtils;
import com.adpstore.flutter_smart_pin_pad_cards.emv.utils.TAGUtlis;
import com.topwise.cloudpos.aidl.emv.level2.AidlPure;
import com.topwise.cloudpos.aidl.emv.level2.EmvCapk;
import com.topwise.cloudpos.aidl.emv.level2.PreProcResult;
import com.topwise.cloudpos.struct.BytesUtil;
import com.topwise.cloudpos.struct.TlvList;

/**
 *Creation date: 2021/6/21 on 11:16
 * Description: Pure process
 * Author: Adam Permana
 */
public class TransPurePay extends AClssKernelBaseTrans {
    private static final String TAG = TransPurePay.class.getSimpleName();
    private AidlPure purePay = AdpUsdkManage.getInstance().getPurePay();

    public TransPurePay() {

    }

    @Override
    public EmvOutCome StartKernelTransProc() {
        try {
            AppLog.d(TAG, "start TransPurePay =========== ");

            // Get preProcResult
            PreProcResult preProcResult = clssTransParam.getPreProcResult();

            // Pass kernel type to payment app
            AppUpdateKernelType(EKernelType.KERNTYPE_PURE);

            // Initialize Pure kernel
            int nRet = purePay.initialize();
            AppLog.d(TAG, "initialize nRet= " + nRet);

            // Set final select data
            AppLog.d(TAG, "setFinalSelectData begin ");
            nRet = purePay.setFinalSelectData(clssTransParam.getAucFinalSelectFCIdata(), clssTransParam.getnFinalSelectFCIdataLen());
            AppLog.d(TAG, "setFinalSelectData end " + nRet);
            if (nRet != EmvErrorCode.CLSS_OK) {
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

            // Get current aid and notice payment app
            String currentAid = getCurrentAid();
            if (TextUtils.isEmpty(currentAid)) {
                return new EmvOutCome(EmvErrorCode.CLSS_TERMINATE, ETransStatus.END_APPLICATION, ETransStep.CLSS_SET_AID_PARAMS_TO_KERNEL);
            }

            // Load AID TLV list by aid
            TlvList kernalList = AppGetKernalDataFromAidParam(currentAid);
            if (kernalList == null) {
                return new EmvOutCome(EmvErrorCode.CLSS_TERMINATE, ETransStatus.END_APPLICATION, ETransStep.CLSS_SET_AID_PARAMS_TO_KERNEL);
            }

            // Set tlv data list into kernel
            if (preProcResult != null) {
                byte[] aucReaderTTQ = preProcResult.getAucReaderTTQ();
                if (aucReaderTTQ != null) {
                    byte aucTTQ[] = new byte[4];
                    System.arraycopy(aucReaderTTQ, 0, aucTTQ, 0, 4);
                    kernalList.addTlv("9F66",aucTTQ);
                    AppLog.d(TAG, "Pure preProcResult TTQ :"+ convert.bcdToStr(aucTTQ));
                } else {
                    kernalList.addTlv("9F66","3600C000");
                }
            }

            AppLog.d(TAG, "entryL2 setKernalData ==== ");
            byte [] kernalData = kernalList.getBytes();
            if (kernalData != null) {
                nRet = purePay.setTLVDataList(kernalData, kernalData.length);
                AppLog.d(TAG, "setTLVDataList nRet: " + nRet);
                if (nRet != EmvErrorCode.CLSS_OK) {
                    return new EmvOutCome(EmvErrorCode.CLSS_TERMINATE, ETransStatus.END_APPLICATION, ETransStep.CLSS_SET_AID_PARAMS_TO_KERNEL);
                }
            }

            // Callback app final Select aid
            if (!AppFinalSelectAid()) {
                return new EmvOutCome(EmvErrorCode.CLSS_TERMINATE, ETransStatus.END_APPLICATION, ETransStep.CLSS_SET_AID_PARAMS_TO_KERNEL);
            }

            // PURE_Config_01
            setTLV(0xFF8135,new byte[]{(byte)0x90});
            //ATOL
            setTLV(0xFF8130,new byte[]{(byte)0x9F, 0x02, (byte)0x9F, 0x03, (byte)0x9F, 0x26, (byte)0x82, (byte)0x9F, 0x36, (byte)0x9F,
                    0x27, (byte)0x9F, 0x10, (byte)0x9F, 0x1A, (byte)0x95, 0x5F, 0x2A, (byte)0x9A, (byte)0x9C,
                    (byte)0x9F, 0x37, (byte)0x9F, 0x35, 0x57, (byte)0x9F, 0x34, (byte)0x84, 0x5F, 0x34,
                    0x5A, (byte)0xC7, (byte)0x9F, 0x33, (byte)0x9F, 0x73, (byte)0x9F, 0x77, (byte)0x9F, 0x45});

//            byte[] MTOL = {(byte)0xFF, (byte)0x81, 0x31, 0x02, (byte)0x8C, 0x57};
            setTLV(0xFF8131,new byte[]{(byte)0x8C, 0x57});

//            byte[] ATDTOL = {(byte)0xFF, (byte)0x81, 0x32, 0x05, (byte)0x82, (byte)0x95, (byte)0x9F, (byte)0x77, (byte)0x84};
            setTLV(0xFF8132,new byte[]{(byte)0x82, (byte)0x95, (byte)0x9F, (byte)0x77, (byte)0x84});

//            byte[] appCapabilities = {(byte)0xFF, (byte)0x81, 0x33, 0x05, 0x36, 0x00, 0x60, 0x43, (byte)0xF9};
            setTLV(0xFF8133,new byte[]{0x36, 0x00, 0x60, 0x43, (byte)0xF9});

//            byte[] implOption = {(byte)0xFF, (byte)0x81, 0x34, 0x01, (byte)0xFF};
            setTLV(0xFF8134,new byte[]{(byte)0xFF});

//            byte[] defaultDDOL = {(byte)0xFF, (byte)0x81, 0x36, 0x03, (byte)0x9F, 0x37, 0x04};
            setTLV(0xFF8136,new byte[]{(byte)0x9F, 0x37, 0x04});

//            byte[] posTimeout = {(byte)0xDF, (byte)0x81, 0x27, 0x02, 0x10, 0x00};
            setTLV(0xDF8127,new byte[]{ 0x10, 0x00});

//            byte[] envelope2 = {(byte)0x9F, 0x76, 0x09, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09};
            setTLV(0x9F76,new byte[]{ 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09});

            byte[] C7Tlv = new byte[7];
            byte[] C7Value = new byte[5];
            int[] C7Len = new int[1];
            byte[] Tlv95 = new byte[7];
            byte[] Value95 = new byte[5];
            int[] Len95 = new int[1];
            if ((preProcResult.getUcRdCLFLmtExceed() == 1) ||
                    (preProcResult.getUcTermFLmtExceed() == 1)) {
                purePay.getTLVDataList(new byte[]{(byte)0xC7}, 1, C7Value.length, C7Value, C7Len);
                //Byte2 Bit8
                C7Value[1] |= (byte)0x80;
                C7Tlv[0] = (byte)0xC7;
                C7Tlv[1] = 0x05;
                System.arraycopy(C7Value, 0, C7Tlv, 2, C7Value.length);
                purePay.setTLVDataList(C7Tlv, C7Tlv.length);

                purePay.getTLVDataList(new byte[]{(byte)0x95}, 1, Value95.length, Value95, Len95);
                //Byte4 Bit8
                Value95[3] |= (byte)0x80;
                Tlv95[0] = (byte)0x95;
                Tlv95[1] = 0x05;
                System.arraycopy(Value95, 0, Tlv95, 2, Value95.length);
                purePay.setTLVDataList(Tlv95, Tlv95.length);
            }
            if (preProcResult.getUcRdCVMLmtExceed() == 1) {
                purePay.getTLVDataList(new byte[]{(byte)0xC7}, 1, C7Value.length, C7Value, C7Len);

                //Byte2 Bit7
                C7Value[1] |= (byte)0x40;
                C7Tlv[0] = (byte)0xC7;
                C7Tlv[1] = 0x05;
                System.arraycopy(C7Value, 0, C7Tlv, 2, C7Value.length);
                purePay.setTLVDataList(C7Tlv, C7Tlv.length);
            }

            // GPO
            Log.d("Jeremy", "gpoProc begin ");
            nRet = purePay.gpoProc();
            Log.d("Jeremy", "gpoProc end " + nRet);
            if (nRet != EmvErrorCode.CLSS_OK) {
                if (nRet == EmvErrorCode.CLSS_RESELECT_APP) {
                    nRet = entryL2.delCandListCurApp();
                    AppLog.d(TAG, "entryL2 delCandListCurApp nRet: " + nRet);
                    if (nRet == EmvErrorCode.CLSS_OK) {
                        return new EmvOutCome(EmvErrorCode.CLSS_RESELECT_APP, ETransStatus.SELECT_NEXT_AID, ETransStep.CLSS_KERNEL_GPO_PROC);
                    } else {
                        return new EmvOutCome(EmvErrorCode.CLSS_USE_CONTACT, ETransStatus.TRY_ANOTHER_INTERFACE, ETransStep.CLSS_KERNEL_GPO_PROC);
                    }
                } else if (nRet == EmvErrorCode.CLSS_USE_CONTACT) {
                    return new EmvOutCome(EmvErrorCode.CLSS_USE_CONTACT, ETransStatus.TRY_ANOTHER_INTERFACE, ETransStep.CLSS_KERNEL_GPO_PROC);
                } else if (nRet == EmvErrorCode.CLSS_REFER_CONSUMER_DEVICE) {
                    return new EmvOutCome(nRet, ETransStatus.SEE_PHONE_TRY_AGAIN, ETransStep.CLSS_KERNEL_GPO_PROC);
                } else {
                    return new EmvOutCome(nRet, ETransStatus.END_APPLICATION, ETransStep.CLSS_KERNEL_GPO_PROC);
                }
            }

            // Read card record data
            Log.d("Jeremy", "readData  begin" );
            nRet = purePay.readData();
            Log.d("Jeremy", "readData end " + nRet);
            Log.d("Jeremy", "============== end ========================" );
            if (nRet != EmvErrorCode.CLSS_OK ) {
                if (nRet == EmvErrorCode.CLSS_RESELECT_APP) {
                    nRet = entryL2.delCandListCurApp();
                    AppLog.d(TAG, "entryL2 delCandListCurApp nRet: " + nRet);
                    if (nRet == EmvErrorCode.CLSS_OK) {
                        return new EmvOutCome(EmvErrorCode.CLSS_RESELECT_APP, ETransStatus.SELECT_NEXT_AID, ETransStep.CLSS_KERNEL_READ_DATA_PROC);
                    } else {
                        return new EmvOutCome(EmvErrorCode.CLSS_USE_CONTACT, ETransStatus.TRY_ANOTHER_INTERFACE, ETransStep.CLSS_KERNEL_READ_DATA_PROC);
                    }
                } else {
                    return new EmvOutCome(nRet, ETransStatus.END_APPLICATION, ETransStep.CLSS_KERNEL_READ_DATA_PROC);
                }
            }

            // Read card info
            byte[] aucTrack2 = getTLV(0x57);
            if (aucTrack2 != null){
                String cardNo =  getPan(BytesUtil.bytes2HexString(aucTrack2).split("F")[0]);
                if (!DataUtils.isNullString(cardNo)) {
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

            // Start transaction
            nRet = purePay.startTrans((byte)0);
            AppLog.emvd(TAG, "startTrans nRet: " + nRet);
            if (nRet != EmvErrorCode.CLSS_OK ) {
                if (nRet == EmvErrorCode.CLSS_USE_CONTACT) {
                    return new EmvOutCome(EmvErrorCode.CLSS_USE_CONTACT, ETransStatus.TRY_ANOTHER_INTERFACE, ETransStep.CLSS_KERNEL_TRANS_PROC);
                } else if (nRet == EmvErrorCode.CLSS_DECLINE) {
                    return new EmvOutCome(EmvErrorCode.CLSS_DECLINE, ETransStatus.OFFLINE_DECLINED, ETransStep.CLSS_KERNEL_TRANS_PROC);
                } else {
                    return new EmvOutCome(nRet, ETransStatus.END_APPLICATION, ETransStep.CLSS_KERNEL_TRANS_PROC);
                }
            }

            // Add current capk param to kernel
            addCapk(currentAid);

            // Offline data process
            nRet = purePay.cardAuth();
            AppLog.emvd(TAG, "cardAuth: nRet=" + nRet);
            if (nRet != EmvErrorCode.CLSS_OK ) {
                if (nRet == EmvErrorCode.CLSS_DECLINE) {
                    return new EmvOutCome(EmvErrorCode.CLSS_DECLINE, ETransStatus.OFFLINE_DECLINED, ETransStep.CLSS_KERNEL_TRANS_PROC);
                } else {
                    return new EmvOutCome(nRet, ETransStatus.END_APPLICATION, ETransStep.CLSS_KERNEL_TRANS_PROC);
                }
            }

            // DF8129 tag  - If it needs pwd
            clssOutComeData = getOutcomeData();
            AppLog.d(TAG, "Pure clssOutComeData: " + clssOutComeData.toString());
            if (clssOutComeData.getUcOCCVM() == EmvErrorCode.CLSS_OC_ONLINE_PIN) { // CVM control force online pin
                // Online enciphered PIN
                emvPinEnter = AppReqImportPin(EPinType.ONLINE_PIN_REQ);
                if (ECVMStatus.ENTER_OK != emvPinEnter.getEcvmStatus()) {
                    return new EmvOutCome(EmvErrorCode.EMV_USER_CANCEL,ETransStatus.END_APPLICATION,ETransStep.CLSS_KERNEL_TRANS_CVM);
                }
            } else if (clssTransParam.isClssForceOnlinePin()) { // App control force online pin
                // Online enciphered PIN
                emvPinEnter = AppReqImportPin(EPinType.ONLINE_PIN_REQ);
                if (ECVMStatus.ENTER_OK != emvPinEnter.getEcvmStatus()) {
                    return new EmvOutCome(EmvErrorCode.EMV_USER_CANCEL,ETransStatus.END_APPLICATION,ETransStep.CLSS_KERNEL_TRANS_CVM);
                }
            }

            switch (clssOutComeData.getUcOCStatus()) {
                case EmvErrorCode.CLSS_OC_APPROVED: // OFFLINE_APPROVE
                    // offline success
                    AppLog.d(TAG, "TC offline success");
                    return new EmvOutCome(EmvErrorCode.CLSS_OK, ETransStatus.OFFLINE_APPROVE, ETransStep.CLSS_KERNEL_TRANS_COMPLETE);
                case EmvErrorCode.CLSS_OC_ONLINE_REQUEST:
                    // online success
                    AppLog.d(TAG, "ARQC online success");

                    // Request for online processing and get online response data from issue bank
                    emvOnlineResp = AppReqOnlineProc();
                    AppLog.d(TAG, "AppReqOnlineProc Resp :" + emvOnlineResp.toString());
                    if (EOnlineResult.ONLINE_APPROVE == emvOnlineResp.geteOnlineResult()) {
                        if (emvOnlineResp.isExistAuthRespCode()) {
                            // Check 8A Authorisation Response Code
                            boolean transResSatus = EAuthRespCode.checkTransResSatus(convert.bcdToStr(emvOnlineResp.getAuthRespCode()), EKernelType.KERNTYPE_AMEX);
                            AppLog.d(TAG, "ARQC checkTransResSatus transResSatus " + transResSatus);
                            if (transResSatus) { // ONLINE_APPROVE
                                return new EmvOutCome(EmvErrorCode.CLSS_OK, ETransStatus.ONLINE_APPROVE, ETransStep.CLSS_KERNEL_TRANS_COMPLETE);
                            }
                        }
                    }
                    return new EmvOutCome(EmvErrorCode.CLSS_OK, ETransStatus.ONLINE_DECLINED, ETransStep.CLSS_KERNEL_TRANS_COMPLETE);

                case EmvErrorCode.CLSS_OC_DECLINED: // OFFLINE_DECLINED
                    //transaction reject
                    AppLog.d(TAG, "AAC transaction reject");
                    return new EmvOutCome(EmvErrorCode.CLSS_OK, ETransStatus.OFFLINE_DECLINED, ETransStep.CLSS_KERNEL_TRANS_COMPLETE);
                default:
                    return new EmvOutCome(EmvErrorCode.CLSS_TERMINATE, ETransStatus.END_APPLICATION, ETransStep.CLSS_KERNEL_TRANS_COMPLETE);
            }
        } catch (Exception e) {
            e.getMessage();
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
            if (EmvErrorCode.CLSS_OK == purePay.getTLVDataList(aucTag,aucTag.length,256,aucVale,aucLen)) {
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
            int nRet = purePay.setTLVDataList(aucTLV,aucTLV.length);
            AppLog.d(TAG, "setTLVDataList nRet " + nRet);
            if (nRet == EmvErrorCode.CLSS_OK) {
                return true;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 获取当前rid
     *
     * @return 当前应用rid
     */
    @Override
    protected String getCurrentAid() {
        byte[] tlvVale = getTLV(0x4F);
        if (tlvVale != null) {
            AppLog.d(TAG, "getCurrentAid TAG 4F: " + convert.bcdToStr(tlvVale));
            return convert.bcdToStr(tlvVale);
        }
        return null;
    }

    @Override
    protected ClssOutComeData getOutcomeData() {
        byte[] tlvOutCome = getTLV(0xDF8129);
        if (tlvOutCome != null) {
            AppLog.d(TAG, "getOutcomeData : " + convert.bcdToStr(tlvOutCome));
            return new ClssOutComeData(tlvOutCome);
        }
        return new ClssOutComeData();
    }

    /**
     * 添加CAPK到交易库
     */
    @Override
    protected  boolean addCapk(String aid) {
        try{
            int res;
            AppLog.d(TAG, "addCapk ======== aid: " + aid);
            purePay.delAllRevocList();
            purePay.delAllCAPK();
            byte[] tlvCapkIndex = getTLV(0x8F);
            if (tlvCapkIndex != null && tlvCapkIndex.length == 0x01) {
                EmvCapk emvCapk = AppFindCapkParamsProc(BytesUtil.hexString2Bytes(aid), tlvCapkIndex[0]);
                if (emvCapk != null) {
                    res = purePay.addCAPK(emvCapk);
                    AppLog.d(TAG, "amexPay addCAPK res: " + res);
                    if (res == EmvErrorCode.CLSS_OK) {
                        return true;
                    }
                }
            }
        } catch(RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void getDebugInfo() {
        byte aucAssistInfo[] = new byte[4096];
        int nErrcode[] = new int[1];
        try {
            int nRet = purePay.getDebugInfo(aucAssistInfo.length, aucAssistInfo, nErrcode);
            AppLog.e(TAG, "getDebugInfo nRet: " + nRet);
            AppLog.e(TAG, "getDebugInfo nErrcode: " + nErrcode[0]);
            AppLog.e(TAG, "getDebugInfo aucAssistInfo: " + new String(aucAssistInfo).trim());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
