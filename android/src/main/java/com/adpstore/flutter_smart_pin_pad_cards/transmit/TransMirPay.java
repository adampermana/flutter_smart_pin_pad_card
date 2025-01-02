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
import com.topwise.cloudpos.aidl.emv.level2.AidlMir;
import com.topwise.cloudpos.aidl.emv.level2.EmvCapk;
import com.topwise.cloudpos.struct.BytesUtil;
import com.topwise.cloudpos.struct.TlvList;

/**
 *Creation date: 2021/6/21 on 11:34
 * Description: Mir Process
 * Author: Adam Permana
 */
public class TransMirPay extends AClssKernelBaseTrans {
    private static final String TAG = TransMirPay.class.getSimpleName();
    private AidlMir mirPay = DeviceServiceManagers.getInstance().getMirPay();

    public TransMirPay() {

    }

    @Override
    public EmvOutCome StartKernelTransProc() {
        try {
            byte [] gl_ucTransPath = new byte[1];;
            AppLog.d(TAG, "start TransMirPay =========== ");

            // Pass kernel type to payment app
            AppUpdateKernelType(EKernelType.KERNTYPE_MIR);

            // Initialize Mir kernel
            int nRet = mirPay.initialize();
            AppLog.d(TAG, "Mir initialize nRet: " + nRet);

            // Set final select data
            Log.d("Jeremy", "setFinalSelectData begin");
            nRet = mirPay.setFinalSelectData(clssTransParam.getAucFinalSelectFCIdata(), clssTransParam.getnFinalSelectFCIdataLen(), gl_ucTransPath);
            Log.d("Jeremy", "setFinalSelectData end: " + nRet);

            AppLog.d(TAG, "setFinalSelectData gl_ucTransPath: " + gl_ucTransPath[0]);
            if (nRet != EmvErrorCode.CLSS_OK) { // ERROR Occurs
                if (nRet == EmvErrorCode.CLSS_RESELECT_APP) {
                    nRet = entryL2.delCandListCurApp();
                    AppLog.d(TAG, "entryL2 delCandListCurApp ret: " + nRet);
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
            if (kernalList == null){
                return new EmvOutCome(EmvErrorCode.CLSS_TERMINATE, ETransStatus.END_APPLICATION, ETransStep.CLSS_SET_AID_PARAMS_TO_KERNEL);
            }

            // Set tlv data list into kernel
            // App_SetTLVDataOneTag((unsigned char*)"\xDF\x81\x0C", 3, (unsigned char*)"\x81", 1);//Kernel ID -MIR 81 06 43
            kernalList.addTlv("DF810C","81");
            //Transaction Recovery Limit
            kernalList.addTlv("DF56","03");
            //68 or E8
            kernalList.addTlv("DF55","E800");

            AppLog.d(TAG, "entryL2 setKernalData ==== ");
            byte [] kernalData = kernalList.getBytes();
            if (kernalData != null) {
                nRet = mirPay.setTLVDataList(kernalData, kernalData.length);
                AppLog.d(TAG, "setTLVDataList ==== " + nRet);
                if (nRet != EmvErrorCode.CLSS_OK) {
                    return new EmvOutCome(EmvErrorCode.CLSS_TERMINATE, ETransStatus.END_APPLICATION, ETransStep.CLSS_SET_AID_PARAMS_TO_KERNEL);
                }
            }

            // Callback app final Select aid
            if (!AppFinalSelectAid()) {
                return new EmvOutCome(EmvErrorCode.CLSS_TERMINATE, ETransStatus.END_APPLICATION, ETransStep.CLSS_SET_AID_PARAMS_TO_KERNEL);
            }

            // GPO
            Log.d("Jeremy", "gpoProc begin " );
            nRet = mirPay.gpoProc();
            Log.d("Jeremy", "gpoProc end " );
            AppLog.d(TAG, "  gpoProc nRet: " + nRet);
            if (nRet != EmvErrorCode.CLSS_OK) {
                if (nRet == EmvErrorCode.CLSS_RESELECT_APP) {
                    nRet = entryL2.delCandListCurApp();
                    AppLog.d(TAG, "entryL2 delCandListCurApp nRet: " + nRet);
                    if (nRet == EmvErrorCode.CLSS_OK) {
                        return new EmvOutCome(EmvErrorCode.CLSS_RESELECT_APP, ETransStatus.SELECT_NEXT_AID, ETransStep.CLSS_KERNEL_GPO_PROC);
                    } else {
                        return new EmvOutCome(EmvErrorCode.CLSS_TERMINATE, ETransStatus.END_APPLICATION, ETransStep.CLSS_KERNEL_GPO_PROC);
                    }
                } else if (nRet == EmvErrorCode.CLSS_USE_CONTACT) {
                    return new EmvOutCome(EmvErrorCode.CLSS_USE_CONTACT, ETransStatus.TRY_ANOTHER_INTERFACE, ETransStep.CLSS_KERNEL_GPO_PROC);
                } else if (nRet == EmvErrorCode.CLSS_DECLINE) {
                    return new EmvOutCome(EmvErrorCode.CLSS_DECLINE, ETransStatus.OFFLINE_DECLINED, ETransStep.CLSS_KERNEL_GPO_PROC);
                } else {
                    return new EmvOutCome(nRet, ETransStatus.END_APPLICATION, ETransStep.CLSS_KERNEL_GPO_PROC);
                }
            }

            // Read card record data
            AppLog.d(TAG, "  readData begin " );
            nRet = mirPay.readData();
            AppLog.d(TAG, "  readData nRet: " + nRet);
            Log.d("Jeremy", "============== end ========================" );
            if (nRet != EmvErrorCode.CLSS_OK ) {
                return new EmvOutCome(nRet, ETransStatus.END_APPLICATION, ETransStep.CLSS_KERNEL_READ_DATA_PROC);
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

            // Add current capk param to kernel
            addCapk(currentAid);

            // Start transaction
            nRet = mirPay.startTrans((byte) 0x00);
            AppLog.d(TAG, "  startTrans nRet: " + nRet);
            if (nRet != EmvErrorCode.CLSS_OK ) { // Error occurs
                if (nRet == EmvErrorCode.CLSS_DECLINE) {
                    return new EmvOutCome(nRet, ETransStatus.OFFLINE_DECLINED, ETransStep.CLSS_KERNEL_TRANS_PROC);
                } else if (nRet == EmvErrorCode.CLSS_REFER_CONSUMER_DEVICE) {
                    return new EmvOutCome(nRet, ETransStatus.SEE_PHONE_TRY_AGAIN, ETransStep.CLSS_KERNEL_TRANS_PROC);
                } else {
                    return new EmvOutCome(nRet, ETransStatus.END_APPLICATION, ETransStep.CLSS_KERNEL_TRANS_PROC);
                }
            }

            // Offline data auth
            nRet = mirPay.cardAuth();
            AppLog.d(TAG, "  cardAuth nRet: " + nRet);
            if (nRet != EmvErrorCode.CLSS_OK ) {
                return new EmvOutCome(nRet, ETransStatus.OFFLINE_DECLINED, ETransStep.CLSS_KERNEL_CARD_AUTH);
            }

            if (nRet == EmvErrorCode.CLSS_OK || nRet == EmvErrorCode.CLSS_DECLINE){
                AppRemovrCard();
            }

            // DF8129 tag  - If it need pwd
            clssOutComeData = getOutcomeData();
            AppLog.d(TAG, "Mir clssOutComeData: " + clssOutComeData.toString());
            if (clssOutComeData.getUcOCCVM() == EmvErrorCode.CLSS_OC_ONLINE_PIN) { // CVM control force online pin
                // Online enciphered PIN
                emvPinEnter = AppReqImportPin(EPinType.ONLINE_PIN_REQ);
                if (ECVMStatus.ENTER_OK != emvPinEnter.getEcvmStatus()) {
                    return new EmvOutCome(EmvErrorCode.EMV_USER_CANCEL,ETransStatus.END_APPLICATION,ETransStep.CLSS_KERNEL_TRANS_CVM);
                }
            } else if (clssTransParam.isClssForceOnlinePin()){ // App control force online pin
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
                    AppLog.d(TAG, "emvOnlineResp " + emvOnlineResp.toString());

                    // Check response data and script
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
                    // ONLINE_DECLINED
                    return new EmvOutCome(EmvErrorCode.CLSS_OK, ETransStatus.ONLINE_DECLINED, ETransStep.CLSS_KERNEL_TRANS_COMPLETE);
                case EmvErrorCode.CLSS_OC_DECLINED: // OFFLINE_DECLINED
                    // Transaction reject
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
            if (EmvErrorCode.CLSS_OK == mirPay.getTLVDataList(aucTag,aucTag.length,256,aucVale,aucLen)) {
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
        if (datas == null){
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
            int nRet = mirPay.setTLVDataList(aucTLV, aucTLV.length);
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
    protected boolean addCapk(String aid) {
        try{
            int res;
            AppLog.d(TAG, "addCapk ======== aid: " + aid);
            mirPay.delAllRevocList();
            mirPay.delAllCAPK();
            byte[] tlvCapkIndex = getTLV(0x8F);
            if (tlvCapkIndex != null && tlvCapkIndex.length == 0x01) {
                EmvCapk emvCapk = AppFindCapkParamsProc(BytesUtil.hexString2Bytes(aid), tlvCapkIndex[0]);
                if (emvCapk != null) {
                    res = mirPay.addCAPK(emvCapk);
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
            int nRet = mirPay.getDebugInfo(aucAssistInfo.length, aucAssistInfo, nErrcode);
            AppLog.e(TAG, "getDebugInfo nRet: " + nRet);
            AppLog.e(TAG, "getDebugInfo nErrcode: " + nErrcode[0]);
            AppLog.e(TAG, "getDebugInfo aucAssistInfo: " + new String(aucAssistInfo).trim());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
