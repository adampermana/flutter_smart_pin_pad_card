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
import com.topwise.cloudpos.aidl.emv.level2.AidlRupay;
import com.topwise.cloudpos.aidl.emv.level2.EmvCapk;
import com.topwise.cloudpos.struct.BytesUtil;
import com.topwise.cloudpos.struct.TlvList;
import com.topwise.toptool.api.packer.ITlv;
import com.topwise.toptool.api.packer.TlvException;
import com.topwise.toptool.impl.TopTool;

/**
 *Creation date: 2021/6/21 on 11:19
 * Description: rupay card
 * Author: Adam Permana
 */
public class TransRuPay extends AClssKernelBaseTrans {
    private static final String TAG = TransRuPay.class.getSimpleName();
    private AidlRupay rupay = AdpUsdkManage.getInstance().getRupay();

    public TransRuPay() {
    }

    @Override
    public EmvOutCome StartKernelTransProc() {

        try {
            AppLog.d(TAG, "start TransRuPay =========== ");
//          PreProcResult preProcResult = clssTransParam.getPreProcResult();

            // Pass kernel type to payment app
            AppUpdateKernelType(EKernelType.KERNTYPE_RUPAY);

            // Initialize Rupay kernel
            int nRet = rupay.initialize();
            Log.d("Jeremy", "initialize  nRet:" + nRet);

            // Set final select data
            Log.d("Jeremy", "setFinalSelectData begin " + nRet);
            nRet = rupay.setFinalSelectData(clssTransParam.getAucFinalSelectFCIdata(), clssTransParam.getnFinalSelectFCIdataLen());
            Log.d("Jeremy", "setFinalSelectData end  " + nRet);
            if (nRet != EmvErrorCode.EMV_OK) {
                if (nRet == EmvErrorCode.EMV_SELECT_NEXT_AID || nRet == EmvErrorCode.EMV_DATA_ERR) {
                    nRet = entryL2.delCandListCurApp();
                    AppLog.d(TAG, "entryL2 delCandListCurApp nRet: " + nRet);
                    if (nRet == EmvErrorCode.CLSS_OK) {
                        return new EmvOutCome(EmvErrorCode.CLSS_RESELECT_APP, ETransStatus.SELECT_NEXT_AID, ETransStep.CLSS_KERNEL_SET_FINAL_SELECT_DATA);
                    }
                }
                return new EmvOutCome(nRet, ETransStatus.END_APPLICATION, ETransStep.CLSS_KERNEL_SET_FINAL_SELECT_DATA);
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
            //((unsigned char*)"\xDF\x81\x0C", 3, (unsigned char*)"\x0D", 1)
            AppLog.d(TAG, "Add Kernel ID  Kernel ==== ");
            kernalList.addTlv("DF810C","0D"); //Name: Kernel ID  Kernel:
            AppLog.d(TAG, "entryL2 setKernalData ==== ");
            byte [] kernalData = kernalList.getBytes();
            if (kernalData != null ){
                nRet = rupay.setTLVDataList(kernalData, kernalData.length);
                AppLog.d(TAG, "setTLVDataList nRet: " + nRet);
                if (nRet != EmvErrorCode.CLSS_OK){
                    return new EmvOutCome(EmvErrorCode.CLSS_TERMINATE, ETransStatus.END_APPLICATION, ETransStep.CLSS_SET_AID_PARAMS_TO_KERNEL);
                }
            }

            // Callback app final Select aid
            if (!AppFinalSelectAid()) {
                return new EmvOutCome(EmvErrorCode.CLSS_TERMINATE, ETransStatus.END_APPLICATION, ETransStep.CLSS_SET_AID_PARAMS_TO_KERNEL);
            }
            Log.d("Jeremy", "gpoProc begin ");

            // GPO
            nRet = rupay.gpoProc();
            Log.d("Jeremy", "gpoProc end " + nRet);
            if (nRet != EmvErrorCode.CLSS_OK) {
                if (nRet == EmvErrorCode.CLSS_RESELECT_APP) {
                    nRet = entryL2.delCandListCurApp();
                    AppLog.d(TAG, "entryL2 delCandListCurApp nRet: " + nRet);
                    if (nRet == EmvErrorCode.CLSS_OK) {
                        return new EmvOutCome(EmvErrorCode.CLSS_RESELECT_APP, ETransStatus.SELECT_NEXT_AID, ETransStep.CLSS_KERNEL_GPO_PROC);
                    }
                }
                return new EmvOutCome(nRet, ETransStatus.END_APPLICATION, ETransStep.CLSS_KERNEL_GPO_PROC);
            }

            // Read card record data
            Log.d("Jeremy", "readData begin  " );
            nRet = rupay.readData();
            Log.d("Jeremy", "readData end " + nRet);
            if (nRet != EmvErrorCode.CLSS_OK ) {
                if (nRet == EmvErrorCode.CLSS_RESELECT_APP) {
                    nRet = entryL2.delCandListCurApp();
                    AppLog.d(TAG, "entryL2 delCandListCurApp nRet: " + nRet);
                    if (nRet == EmvErrorCode.CLSS_OK) {
                        return new EmvOutCome(EmvErrorCode.CLSS_RESELECT_APP, ETransStatus.SELECT_NEXT_AID, ETransStep.CLSS_KERNEL_READ_DATA_PROC);
                    }
                }
                return new EmvOutCome(nRet, ETransStatus.END_APPLICATION, ETransStep.CLSS_KERNEL_READ_DATA_PROC);
            }

            // Read card info
            byte[] aucTrack2 = getTLV(0x57);
            if (aucTrack2 != null) {
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

            // Offline data process
            nRet = rupay.cardAuth();
            AppLog.d(TAG, "cardAuth nRet=" + nRet);
            if (nRet != EmvErrorCode.CLSS_OK ) {
                if (nRet == EmvErrorCode.CLSS_DECLINE) {
                    return new EmvOutCome(EmvErrorCode.CLSS_DECLINE, ETransStatus.OFFLINE_DECLINED, ETransStep.CLSS_KERNEL_TRANS_COMPLETE);
                } else {
                    return new EmvOutCome(nRet, ETransStatus.END_APPLICATION, ETransStep.CLSS_KERNEL_CARD_AUTH);
                }
            }

            // Trans process
            nRet = rupay.transProc((byte) 0); //黑名单
            AppLog.d(TAG, "transProc res: " + nRet);
            if (nRet != EmvErrorCode.CLSS_OK ) {
                if (nRet == EmvErrorCode.CLSS_DECLINE) {
                    return new EmvOutCome(EmvErrorCode.CLSS_DECLINE, ETransStatus.OFFLINE_DECLINED, ETransStep.CLSS_KERNEL_TRANS_COMPLETE);
                } else {
                    return new EmvOutCome(nRet, ETransStatus.END_APPLICATION, ETransStep.CLSS_KERNEL_CARD_AUTH);
                }
            }

            // Start transaction
            AppLog.d(TAG, "startTrans=========: " );
            nRet = rupay.startTrans();
            AppLog.d(TAG, "CLSS_OK res: " + nRet);
            if (nRet != EmvErrorCode.EMV_OK ) {
                if (nRet == EmvErrorCode.CLSS_DECLINE) {
                    return new EmvOutCome(EmvErrorCode.CLSS_DECLINE, ETransStatus.OFFLINE_DECLINED, ETransStep.CLSS_KERNEL_TRANS_COMPLETE);
                } else {
                    return new EmvOutCome(nRet, ETransStatus.END_APPLICATION, ETransStep.CLSS_KERNEL_CARD_AUTH);
                }
            }
            Log.d("Jeremy", "============== end ========================" );

            // DF8129 tag  - If it needs pwd
            clssOutComeData = getOutcomeData();
            AppLog.d(TAG, "clssOutComeData: " + clssOutComeData.toString());
            if (clssOutComeData.getUcOCCVM() == EmvErrorCode.CLSS_OC_ONLINE_PIN) {
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
                case EmvErrorCode.CLSS_OC_APPROVED:
                    // offline success
                    AppLog.d(TAG, "TC offline success");
                    return new EmvOutCome(EmvErrorCode.CLSS_OK, ETransStatus.OFFLINE_APPROVE, ETransStep.CLSS_KERNEL_TRANS_COMPLETE);
                case EmvErrorCode.CLSS_OC_ONLINE_REQUEST:
                    // online success
                    AppLog.d(TAG, "ARQC online success");
                    return onlineAndScriptProcess();
                case EmvErrorCode.CLSS_OC_DECLINED:
                    // transaction reject
                    AppLog.d(TAG, "AAC transaction reject");
                    return new EmvOutCome(EmvErrorCode.CLSS_OK, ETransStatus.OFFLINE_DECLINED, ETransStep.CLSS_KERNEL_TRANS_COMPLETE);
                default:
                    return new EmvOutCome(EmvErrorCode.CLSS_TERMINATE, ETransStatus.END_APPLICATION, ETransStep.CLSS_KERNEL_TRANS_COMPLETE);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            return new EmvOutCome(EmvErrorCode.CLSS_TERMINATE, ETransStatus.END_APPLICATION, ETransStep.CLSS_KERNEL_TRANS_COMPLETE);
        }
    }

    @Override
    public byte[] getTLV(int tag) {
        AppLog.d(TAG, "getTLV tag : " + tag);
        byte [] aucVale = new byte[256];
        int [] aucLen  = new int[2];
        byte[] aucTag = TAGUtlis.tagFromInt(tag);
        AppLog.d(TAG, "getTLV aucTag : " + convert.bcdToStr(aucTag));
        try {
            if (EmvErrorCode.CLSS_OK == rupay.getTLVDataList(aucTag,aucTag.length,256,aucVale,aucLen)){
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
        System.arraycopy(aucTag,0,aucTLV,0,aucTag.length);
        System.arraycopy(aucLen,0,aucTLV,aucTag.length,aucLen.length);
        System.arraycopy(datas,0,aucTLV,(aucTag.length + aucLen.length),datas.length);

        AppLog.d(TAG, "setTLV TLV : " + convert.bcdToStr(aucTLV));
        try {
            int nRet = rupay.setTLVDataList(aucTLV,aucTLV.length);
            AppLog.d(TAG, "setTLVDataList nRet " + nRet);
            if (nRet == EmvErrorCode.CLSS_OK){
                return true;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    private EmvOutCome onlineAndScriptProcess() throws RemoteException {
        // Request for online processing and get online response data from issue bank
        AppLog.d(TAG, "onlineAndscriptProcess Start============= ");
        emvOnlineResp = AppReqOnlineProc();
        AppLog.d(TAG, "onlineAndscriptProcess End============= " + emvOnlineResp.toString());

        ITlv tlv = TopTool.getInstance().getPacker().getTlv();
        ITlv.ITlvDataObjList tlvList = tlv.createTlvDataObjectList();
        boolean isExistIssScr = false;

        if (EOnlineResult.ONLINE_APPROVE == emvOnlineResp.geteOnlineResult()) {
            int sAppVerCard = -1;
            int sAppVerTM = -1;

            // Get App version
            byte[] aucAppVerCard = getTLV(0x9F08);
            byte[] aucAppVerTM = getTLV(0x9F09);
            if (aucAppVerCard != null) {
                sAppVerCard = Integer.valueOf(BytesUtil.bytes2HexString(aucAppVerCard));
            }
            if (aucAppVerTM != null) {
                sAppVerTM = Integer.valueOf(BytesUtil.bytes2HexString(aucAppVerTM));
            }
            AppLog.d(TAG,"onlineAndscriptProcess 0x9F08 " + sAppVerCard);
            AppLog.d(TAG,"onlineAndscriptProcess 0x9F09 " + sAppVerTM);

            // Check 8A Authorisation Response Code
            boolean transResSatus = EAuthRespCode.checkTransResSatus(convert.bcdToStr(emvOnlineResp.getAuthRespCode()), EKernelType.KERNTYPE_VISA);
            AppLog.d(TAG, "transResSatus :" + transResSatus);
            if (!transResSatus) {
                return new EmvOutCome(EmvErrorCode.CLSS_OK, ETransStatus.ONLINE_DECLINED, ETransStep.CLSS_KERNEL_TRANS_COMPLETE);
            }

            setTLV(0x8A, emvOnlineResp.getAuthRespCode());
            if (emvOnlineResp.isExistIssAuthData()) {
                setTLV(0x91, emvOnlineResp.getIssueAuthData());
            }
            if (emvOnlineResp.isExistAuthCode()) {
                setTLV(0x89, emvOnlineResp.getAuthCode());
            }

            if (emvOnlineResp.isExistIssScr72() || emvOnlineResp.isExistIssScr71() || emvOnlineResp.isExistIssAuthData()) {
                if (emvOnlineResp.isExistIssScr71()) {
                    ITlv.ITlvDataObj Script71 = tlv.createTlvDataObject();
                    Script71.setTag(0x71);
                    Script71.setValue(emvOnlineResp.getIssueScript71());
                    tlvList.addDataObj(Script71);
                    isExistIssScr = true;
                }
                if (emvOnlineResp.isExistIssScr72()) {
                    ITlv.ITlvDataObj Script72 = tlv.createTlvDataObject();
                    Script72.setTag(0x72);
                    Script72.setValue(emvOnlineResp.getIssueScript72());
                    tlvList.addDataObj(Script72);
                    isExistIssScr = true;
                }
                if (sAppVerCard >= 2 || sAppVerTM >= 2) {
                    if ((isExistIssScr) ||
                            (emvOnlineResp.isExistIssAuthData() && emvOnlineResp.getIssueAuthData().length > 6
                                    && (( emvOnlineResp.getIssueAuthData()[6] & 0x80) == 0x80  || ( emvOnlineResp.getIssueAuthData()[6] & 0x40) == 0x40)))
                    {
                        AppLog.d(TAG,"onlineAndscriptProcess onSecondCheckCard ");
                        boolean b = AppSearchCardbySecond();
                        AppLog.d(TAG,"AppSearchCardbySecond " + b);
                        if (!b) {
                            return new EmvOutCome(EmvErrorCode.EMV_USER_CANCEL, ETransStatus.ONLINE_DECLINED, ETransStep.CLSS_KERNEL_TRANS_COMPLETE);
                        }
                    }
                }

                byte[] srciptout = new byte[256];
                byte[] ucACTypeOut = new byte[256];
                int[] gl_unScriptRstOutLen = new int[10];
                int srciptlen = -1;
                int completeTransRet = -1;

                if (isExistIssScr) {
                    try {
                        byte[] aucScript = tlv.pack(tlvList);
                        AppLog.d(TAG,"onlineAndscriptProcess aucScript " + BytesUtil.bytes2HexString(aucScript));
                        completeTransRet = rupay.completeTrans(0, aucScript, aucScript.length, srciptout, gl_unScriptRstOutLen, ucACTypeOut);
                    } catch (TlvException e) {
                        e.printStackTrace();
                    }
                }

                AppLog.d(TAG,"onlineAndscriptProcess completeTrans cRet " +completeTransRet );
                if (srciptout != null && gl_unScriptRstOutLen[0] > 0) {
                    byte[] scriptVale = new byte[gl_unScriptRstOutLen[0]];
                    System.arraycopy(srciptout, 0, scriptVale, 0, gl_unScriptRstOutLen[0]);
                    AppLog.d(TAG,"onlineAndscriptProcess completeTrans 9F5B " + BytesUtil.bytes2HexString(scriptVale));
                    setTLV(0x9F5B, srciptout);
                }
                if (completeTransRet == EmvErrorCode.CLSS_OK) {
                    return new EmvOutCome(EmvErrorCode.CLSS_OK, ETransStatus.ONLINE_APPROVE, ETransStep.CLSS_KERNEL_TRANS_COMPLETE);
                }
            } else {
                return new EmvOutCome(EmvErrorCode.CLSS_OK, ETransStatus.ONLINE_APPROVE, ETransStep.CLSS_KERNEL_TRANS_COMPLETE);
            }
        }
        return new EmvOutCome(EmvErrorCode.CLSS_OK, ETransStatus.OFFLINE_DECLINED, ETransStep.CLSS_KERNEL_TRANS_COMPLETE);
    }

    /**
     * 获取当前rid
     *
     * @return 当前应用rid
     */
    @Override
    protected String getCurrentAid() {
        byte[] tlvVale = getTLV(0x4F);
        if (tlvVale !=null){
            AppLog.d(TAG, "getCurrentAid TAG 4F: " + convert.bcdToStr(tlvVale));
            return convert.bcdToStr(tlvVale);
        }
        return null;
    }

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
    protected  boolean addCapk(String aid) {
        try{
            int res;
            AppLog.d(TAG, "addCapk ======== aid: " + aid);
            rupay.delAllRevocList();
            rupay.delAllCAPK();
            byte[] tlvCapkIndex = getTLV(0x8F);
            if (tlvCapkIndex != null && tlvCapkIndex.length == 0x01){
                EmvCapk emvCapk = AppFindCapkParamsProc(BytesUtil.hexString2Bytes(aid), tlvCapkIndex[0]);
                if (emvCapk != null) {
                    res = rupay.addCAPK(emvCapk);
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
    @Override
    public void getDebugInfo() {
        byte aucAssistInfo[] = new byte[4096];
        int nErrcode[] = new int[1];
        try {
            int nRet = rupay.getDebugInfo(aucAssistInfo.length,aucAssistInfo,nErrcode);
            AppLog.e(TAG, "getDebugInfo nRet: " + nRet);
            AppLog.e(TAG, "getDebugInfo nErrcode: " + nErrcode[0]);
            AppLog.e(TAG, "getDebugInfo aucAssistInfo: " + new String(aucAssistInfo).trim());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
