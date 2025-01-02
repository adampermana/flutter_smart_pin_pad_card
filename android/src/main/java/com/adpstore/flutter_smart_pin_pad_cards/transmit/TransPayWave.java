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
import com.topwise.cloudpos.aidl.emv.level2.AidlPaywave;
import com.topwise.cloudpos.aidl.emv.level2.EmvCapk;
import com.topwise.cloudpos.aidl.emv.level2.PreProcResult;
import com.topwise.cloudpos.aidl.emv.level2.TransParam;
import com.topwise.cloudpos.struct.BytesUtil;
import com.topwise.cloudpos.struct.TlvList;
import com.topwise.toptool.api.packer.ITlv;
import com.topwise.toptool.impl.TopTool;

public class TransPayWave extends AClssKernelBaseTrans {
    private static final String TAG = TransPayWave.class.getSimpleName();
    private AidlPaywave paywave = DeviceServiceManagers.getInstance().getL2Paywave();

    public TransPayWave() {

    }

    @Override
    public EmvOutCome StartKernelTransProc() {
        try {
            AppLog.d(TAG, "start Paywave =========== ");

            // Get preProcResult
            PreProcResult preProcResult = clssTransParam.getPreProcResult();
            AppLog.d(TAG, "getAucReaderTTQ " + convert.bcdToStr(preProcResult.getAucReaderTTQ()));

            // Pass kernel type to payment app
            AppUpdateKernelType(EKernelType.KERNTYPE_VISA);

            // Initialize PayWave kernel
            int nRet = paywave.initialize();
            AppLog.d(TAG, "initialize nRet: " + nRet);

            // Set final select data
            Log.d("Jeremy", "setFinalSelectData begin " );
            nRet = paywave.setFinalSelectData(clssTransParam.getAucFinalSelectFCIdata(), clssTransParam.getnFinalSelectFCIdataLen());
            Log.d("Jeremy", "setFinalSelectData end " + nRet);
            if (nRet != EmvErrorCode.CLSS_OK) { // ERROR Occurs
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
            AppLog.d(TAG, "entryL2 setKernalData ==== ");
            byte [] kernalData = kernalList.getBytes();
            if (kernalData != null) {
                nRet = paywave.setTLVDataList(kernalData, kernalData.length);
                AppLog.d(TAG, "setTLVDataList nRet= " + nRet);
                if (nRet != EmvErrorCode.CLSS_OK) {
                    return new EmvOutCome(EmvErrorCode.CLSS_TERMINATE, ETransStatus.END_APPLICATION, ETransStep.CLSS_SET_AID_PARAMS_TO_KERNEL);
                }
            }

            // Set Transaction Param
            TransParam transParam = clssTransParam.getTransParam();
            nRet = paywave.setTransData(transParam, preProcResult);
            AppLog.d(TAG, "setTransData res: " + nRet);
            if (nRet != EmvErrorCode.CLSS_OK) {
                return new EmvOutCome(nRet, ETransStatus.END_APPLICATION, ETransStep.CLSS_SET_TRANSDATA_TO_KERNEL);
            }

            // PayWave will in GPO set ttq
            // Callback app final Select aid
            if (!AppFinalSelectAid()) {
                return new EmvOutCome(EmvErrorCode.CLSS_TERMINATE, ETransStatus.END_APPLICATION, ETransStep.CLSS_SET_AID_PARAMS_TO_KERNEL);
            }

            // Gpo
            byte[] transPath = new byte[1];
            Log.d("Jeremy", "gpoProc begin");
            nRet = paywave.gpoProc(transPath);
            Log.d("Jeremy", "gpoProc end "+ nRet);
            AppLog.d(TAG, "gpoProc Transaction Path: " + transPath[0]);
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
            byte[] ucAcType1 = new byte[1];
            Log.d("Jeremy", "readData begin");
            nRet = paywave.readData(ucAcType1);
            Log.d("Jeremy", "readData end "+nRet);
            Log.d("Jeremy", "============== end ========================" );
            AppLog.d(TAG, "readData nRet: " + nRet + "ACType :"+  ucAcType1[0]);
            if (nRet != EmvErrorCode.CLSS_OK) {
                if (nRet == EmvErrorCode.CLSS_USE_CONTACT) {
                    return new EmvOutCome(EmvErrorCode.CLSS_USE_CONTACT, ETransStatus.TRY_ANOTHER_INTERFACE, ETransStep.CLSS_KERNEL_READ_DATA_PROC);
                } else if (nRet == EmvErrorCode.CLSS_DECLINE) {
                    return new EmvOutCome(nRet, ETransStatus.OFFLINE_DECLINED, ETransStep.CLSS_KERNEL_READ_DATA_PROC);
                } else {
                    return new EmvOutCome(nRet, ETransStatus.END_APPLICATION, ETransStep.CLSS_KERNEL_READ_DATA_PROC);
                }
            }

            // Refund process
            if (clssTransParam.getTransParam().getUcTransType() == EmvErrorCode.EMV_TRANS_TYPE_REFUND &&
                    (ucAcType1[0] == EmvErrorCode.AC_TC || ucAcType1[0] == EmvErrorCode.AC_AAC)) {
                AppLog.d(TAG, "TransType is Refund and approval");
                return new EmvOutCome(EmvErrorCode.CLSS_OK, ETransStatus.ONLINE_REQUEST, ETransStep.CLSS_KERNEL_TRANS_COMPLETE);
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

            //ucAcType1 == EmvErrorCode.AC_TC   ----TO DO
            byte[] ucAcType = new byte[1];

            if (transPath[0] == EmvErrorCode.CLSS_TRANSPATH_EMV) {
                // Add current capk param to kernel
                addCapk(currentAid);

                // Offline data process
                byte[] ucODDAResultFlg = new byte[1];
                int[] nErrcode = new int[1];
                nRet = paywave.cardAuth(ucAcType, ucODDAResultFlg);
                paywave.getDebugInfo(0,null,nErrcode);
                AppLog.d(TAG, "cardAuth nRet: " + nRet + ";ucODDAResultFlg: " + ucODDAResultFlg[0] + ",nErrcode:"+nErrcode[0]);
            } else if (transPath[0] == EmvErrorCode.CLSS_TRANSPATH_MAG) {
                AppLog.d(TAG, "ucType == EmvErrorCode.CLSS_TRANSPATH_MAG");
            } else {
                nRet = EmvErrorCode.CLSS_TERMINATE;
            }
            if (nRet == EmvErrorCode.CLSS_OK || nRet == EmvErrorCode.CLSS_DECLINE) {
                AppRemovrCard();
            }
            if (nRet != EmvErrorCode.CLSS_OK) {
                if (nRet == EmvErrorCode.CLSS_USE_CONTACT) {
                    return new EmvOutCome(EmvErrorCode.CLSS_USE_CONTACT, ETransStatus.TRY_ANOTHER_INTERFACE, ETransStep.CLSS_KERNEL_CARD_AUTH);
                } else if (nRet == EmvErrorCode.CLSS_DECLINE) {
                    return new EmvOutCome(EmvErrorCode.CLSS_DECLINE, ETransStatus.OFFLINE_DECLINED, ETransStep.CLSS_KERNEL_TRANS_COMPLETE);
                } else {
                    return new EmvOutCome(nRet, ETransStatus.END_APPLICATION, ETransStep.CLSS_KERNEL_CARD_AUTH);
                }
            }

            // DF8129 tag  - If it needs pwd
            clssOutComeData = getOutcomeData();
            AppLog.d(TAG, "PayWave clssOutComeData: " + clssOutComeData.toString());
           /* if (clssOutComeData.getUcOCCVM() == EmvErrorCode.CLSS_OC_ONLINE_PIN) { // CVM control force online pin
                // Online enciphered PIN
                emvPinEnter = AppReqImportPin(EPinType.ONLINE_PIN_REQ);
                if (ECVMStatus.ENTER_OK != emvPinEnter.getEcvmStatus()) {
                    return new EmvOutCome(EmvErrorCode.EMV_USER_CANCEL, ETransStatus.END_APPLICATION, ETransStep.CLSS_KERNEL_TRANS_CVM);
                }
            } else if (clssTransParam.isClssForceOnlinePin()) { // App control force online pin
                // Online enciphered PIN
                emvPinEnter = AppReqImportPin(EPinType.ONLINE_PIN_REQ);
                if (ECVMStatus.ENTER_OK != emvPinEnter.getEcvmStatus()) {
                    return new EmvOutCome(EmvErrorCode.EMV_USER_CANCEL, ETransStatus.END_APPLICATION, ETransStep.CLSS_KERNEL_TRANS_CVM);
                }
            }*/

            switch (clssOutComeData.getUcOCStatus()) {
                case EmvErrorCode.CLSS_OC_APPROVED: // OFFLINE_APPROVE
                    // offline success
                    AppLog.d(TAG, "TC offline success");
                    return new EmvOutCome(EmvErrorCode.CLSS_OK, ETransStatus.OFFLINE_APPROVE, ETransStep.CLSS_KERNEL_TRANS_COMPLETE);
                case EmvErrorCode.CLSS_OC_ONLINE_REQUEST:
                    // online success
                    AppLog.d(TAG, "ARQC online success");

                    if (clssOutComeData.getUcOCCVM() == EmvErrorCode.CLSS_OC_ONLINE_PIN) { // CVM control force online pin
                        // Online enciphered PIN
                        emvPinEnter = AppReqImportPin(EPinType.ONLINE_PIN_REQ);
                        if (ECVMStatus.ENTER_OK != emvPinEnter.getEcvmStatus()) {
                            return new EmvOutCome(EmvErrorCode.EMV_USER_CANCEL, ETransStatus.END_APPLICATION, ETransStep.CLSS_KERNEL_TRANS_CVM);
                        }
                    }

                    boolean isExistIssScr = false;

                    // Request for online processing and get online response data from issue bank
                    emvOnlineResp = AppReqOnlineProc();
                    AppLog.d(TAG, "AppReqOnlineProc Resp :" + emvOnlineResp.toString());

                    // Check response data and script
                    if (EOnlineResult.ONLINE_APPROVE == emvOnlineResp.geteOnlineResult()) {
                        if (emvOnlineResp.isExistAuthRespCode()) {
                            // Check 8A Authorisation Response Code
                            boolean transResSatus = EAuthRespCode.checkTransResSatus(convert.bcdToStr(emvOnlineResp.getAuthRespCode()), EKernelType.KERNTYPE_VISA);
                            AppLog.d(TAG, "transResSatus :" + transResSatus);
                            if (!transResSatus) { // ONLINE_DECLINED
                                return new EmvOutCome(EmvErrorCode.CLSS_OK, ETransStatus.ONLINE_DECLINED, ETransStep.CLSS_KERNEL_TRANS_COMPLETE);
                            }

                            // Set 8A Authorisation Response Code
                            setTLV(0x8A, emvOnlineResp.getAuthRespCode());

                            // Set 91 Issuer Authentication Data
                            if (emvOnlineResp.isExistIssAuthData()) {
                                setTLV(0x91, emvOnlineResp.getIssueAuthData());
                            }

                            // Set 89 Authorisation Code
                            if (emvOnlineResp.isExistAuthCode()) {
                                setTLV(0x89, emvOnlineResp.getAuthCode());
                            }

                            // Check issuer script
                            ITlv tlv = TopTool.getInstance().getPacker().getTlv();
                            ITlv.ITlvDataObjList tlvList = tlv.createTlvDataObjectList();
                            if (emvOnlineResp.isExistIssScr72() || emvOnlineResp.isExistIssScr71() || emvOnlineResp.isExistIssAuthData()) {
                                // Check 71 script
                                if (emvOnlineResp.isExistIssScr71()) {
                                    ITlv.ITlvDataObj Script71 = tlv.createTlvDataObject();
                                    Script71.setTag(0x71);
                                    Script71.setValue(emvOnlineResp.getIssueScript71());
                                    tlvList.addDataObj(Script71);
                                    isExistIssScr = true;
                                }

                                // Check 72 script
                                if (emvOnlineResp.isExistIssScr72()) {
                                    ITlv.ITlvDataObj Script72 = tlv.createTlvDataObject();
                                    Script72.setTag(0x72);
                                    Script72.setValue(emvOnlineResp.getIssueScript72());
                                    tlvList.addDataObj(Script72);
                                    isExistIssScr = true;
                                }

                                while (true) {
                                    byte[] aucTTQ = getTLV(0x9F66);
                                    byte[] aucCTQ = getTLV(0x9F6C);
                                    AppLog.d(TAG, "aucTTQ :" + convert.bcdToStr(aucTTQ));
                                    AppLog.d(TAG, "aucCTQ :" + convert.bcdToStr(aucCTQ));

                                    if ((aucTTQ[2] & 0x80) == 0x00 || (aucCTQ[1] & 0x40) == 0x00) {
                                        return new EmvOutCome(EmvErrorCode.CLSS_OK, ETransStatus.ONLINE_APPROVE, ETransStep.CLSS_KERNEL_TRANS_COMPLETE);
                                    }

                                    // Second search card
                                    boolean bSecSearchCardStatus = AppSearchCardbySecond();
                                    AppLog.d(TAG, "bSecSearchCardStatus :" + bSecSearchCardStatus);
                                    if (!bSecSearchCardStatus) {
                                        return new EmvOutCome(EmvErrorCode.CLSS_OK, ETransStatus.ONLINE_DECLINED, ETransStep.CLSS_KERNEL_TRANS_COMPLETE);
                                    }

                                    // Issuer Auth
                                    if (emvOnlineResp.isExistIssAuthData()) {
                                        nRet = paywave.issuerAuth(emvOnlineResp.getIssueAuthData(), emvOnlineResp.getIssueAuthData().length);
                                        AppLog.d(TAG, "issuerAuth :" + bSecSearchCardStatus);
                                        if (nRet == EmvErrorCode.CLSS_RESELECT_APP) {
                                            continue;
                                        }
                                    }

                                    // Process issuer script
                                    if (isExistIssScr) {
                                        byte[] aucScript = tlv.pack(tlvList);
                                        AppLog.d(TAG, "aucScript :" + convert.bcdToStr(aucScript));
                                        nRet = paywave.issScriptProc(aucScript, aucScript.length);
                                        AppLog.d(TAG, "issScriptProc nRet :" + nRet);
                                    }
                                    // ONLINE_APPROVE
                                    return new EmvOutCome(EmvErrorCode.CLSS_OK, ETransStatus.ONLINE_APPROVE, ETransStep.CLSS_KERNEL_TRANS_COMPLETE);
                                }
                            } else { // ONLINE_APPROVE
                                return new EmvOutCome(EmvErrorCode.CLSS_OK, ETransStatus.ONLINE_APPROVE, ETransStep.CLSS_KERNEL_TRANS_COMPLETE);
                            }
                        }
                    }
                    // ONLINE_DECLINED
                    return new EmvOutCome(EmvErrorCode.CLSS_OK, ETransStatus.ONLINE_DECLINED, ETransStep.CLSS_KERNEL_TRANS_COMPLETE);
                case EmvErrorCode.CLSS_OC_DECLINED: // OFFLINE_DECLINED
                    // transaction reject
                    AppLog.d(TAG, "AAC transaction reject");
                    return new EmvOutCome(EmvErrorCode.CLSS_OK, ETransStatus.OFFLINE_DECLINED, ETransStep.CLSS_KERNEL_TRANS_COMPLETE);
                default:
                    return new EmvOutCome(EmvErrorCode.CLSS_TERMINATE, ETransStatus.END_APPLICATION, ETransStep.CLSS_KERNEL_TRANS_COMPLETE);
            }
        } catch(Exception e) {
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
            if (EmvErrorCode.CLSS_OK == paywave.getTLVDataList(aucTag,aucTag.length,256,aucVale,aucLen)) {
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
        System.arraycopy(aucTag,0, aucTLV,0,aucTag.length);
        System.arraycopy(aucLen,0, aucTLV, aucTag.length,aucLen.length);
        System.arraycopy(datas,0, aucTLV, (aucTag.length + aucLen.length),datas.length);

        AppLog.d(TAG, "setTLV TLV : " + convert.bcdToStr(aucTLV));
        try {
            int nRet = paywave.setTLVDataList(aucTLV, aucTLV.length);
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
            paywave.delAllRevocList();
            paywave.delAllCAPK();
            byte[] tlvCapkIndex = getTLV(0x8F);
            if (tlvCapkIndex != null && tlvCapkIndex.length == 0x01) {
                EmvCapk emvCapk = AppFindCapkParamsProc(BytesUtil.hexString2Bytes(aid), tlvCapkIndex[0]);
                if (emvCapk != null) {
                    res = paywave.addCAPK(emvCapk);
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
            int nRet = paywave.getDebugInfo(aucAssistInfo.length, aucAssistInfo, nErrcode);
            AppLog.e(TAG, "getDebugInfo nRet: " + nRet);
            AppLog.e(TAG, "getDebugInfo nErrcode: " + nErrcode[0]);
            AppLog.e(TAG, "getDebugInfo aucAssistInfo: " + new String(aucAssistInfo).trim());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}