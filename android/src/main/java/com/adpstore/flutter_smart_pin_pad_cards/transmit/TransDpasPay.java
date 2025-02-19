/*============================================================
 Module Name       : TransDpasPay.java
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
import com.topwise.cloudpos.aidl.emv.level2.AidlDpas;
import com.topwise.cloudpos.aidl.emv.level2.EmvCapk;
import com.topwise.cloudpos.aidl.emv.level2.PreProcResult;
import com.topwise.cloudpos.struct.BytesUtil;
import com.topwise.cloudpos.struct.TlvList;
import com.topwise.toptool.api.packer.ITlv;
import com.topwise.toptool.impl.TopTool;

/**
 *Creation date: 2021/6/21 on 11:33
 * Description: Dpas kernel card reading process encapsulation
 * Author: Adam Permana
 */
public class TransDpasPay extends AClssKernelBaseTrans {
    private static final String TAG = TransDpasPay.class.getSimpleName();
    private AidlDpas dpasPay = DeviceServiceManagers.getInstance().getDpasPay();

    public TransDpasPay() {

    }

    @Override
    public EmvOutCome StartKernelTransProc() {
        try {
            AppLog.d(TAG, "DPAS start TransPurePay =========== ");

            // Get preprocess result
            PreProcResult preProcResult = clssTransParam.getPreProcResult();

            // Pass kernel type to payment app
            AppUpdateKernelType(EKernelType.KERNTYPE_DPAS);

            // Initialize Dpas kernel
            int nRet = dpasPay.initialize();
            AppLog.d(TAG, "DPAS initialize nRet: " + nRet);

            // Set final select data
            Log.d("Jeremy", "DPAS setFinalSelectData begin");
            nRet = dpasPay.setFinalSelectData(clssTransParam.getAucFinalSelectFCIdata(), clssTransParam.getnFinalSelectFCIdataLen());
            Log.d("Jeremy", "DPAS setFinalSelectData nRet: " + nRet);
            if (nRet != EmvErrorCode.CLSS_OK) { // ERROR Occurs
                if (nRet == EmvErrorCode.CLSS_USE_CONTACT) {
                    return new EmvOutCome(EmvErrorCode.CLSS_USE_CONTACT, ETransStatus.TRY_ANOTHER_INTERFACE, ETransStep.CLSS_KERNEL_SET_FINAL_SELECT_DATA);
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
            // App_SetTLVDataOneTag((unsigned char*)"\xDF\x81\x0C", 3, (unsigned char*)"\x06", 1);//Kernel ID -DPAS 06
            kernalList.addTlv("DF810C","06"); //Name:		Kernel ID  Kernel:
            // 9F66 Terminal Transaction Qualifiers (TTQ)
            if (preProcResult != null) {
                byte[] aucReaderTTQ = preProcResult.getAucReaderTTQ();
                if (aucReaderTTQ != null) {
                    byte aucTTQ[] = new byte[4];
                    System.arraycopy(aucReaderTTQ, 0, aucTTQ, 0, 4);
                    kernalList.addTlv("9F66", aucTTQ);
                    AppLog.d(TAG, "DPAS preProcResult TTQ :" + convert.bcdToStr(aucTTQ));
                } else {
                    // B620C000 TTQ Online and Offline :Should be B6 00 C0 00
                    kernalList.addTlv("9F66","3600C000");
                }

                if (preProcResult.getUcRdCLFLmtExceed() == 0x01 || preProcResult.getUcTermFLmtExceed() == 0x01) {
                    kernalList.addTlv("DF8151","01");
                }
            }

            byte [] kernalData = kernalList.getBytes();
            if (kernalData != null ) {
                nRet = dpasPay.setTLVDataList(kernalData, kernalData.length);
                AppLog.d(TAG, "setTLVDataList nRet: " + nRet);
                if (nRet != EmvErrorCode.CLSS_OK) {
                    return new EmvOutCome(EmvErrorCode.CLSS_TERMINATE, ETransStatus.END_APPLICATION, ETransStep.CLSS_SET_AID_PARAMS_TO_KERNEL);
                }
            }

            // Callback app final Select aid
            if (!AppFinalSelectAid()) {
                return new EmvOutCome(EmvErrorCode.CLSS_TERMINATE, ETransStatus.END_APPLICATION, ETransStep.CLSS_SET_AID_PARAMS_TO_KERNEL);
            }

            // GPO
            byte[] dataBuf = new byte[1];
            Log.d("Jeremy", "gpoProc begin" );
            nRet = dpasPay.gpoProc(dataBuf);
            Log.d("Jeremy", "gpoProc end "+ nRet);
            AppLog.d(TAG, "DPAS gpoProc Transaction Path: " + dataBuf[0]);
            if (nRet != EmvErrorCode.CLSS_OK) {
                if (nRet == EmvErrorCode.CLSS_USE_CONTACT) {
                    return new EmvOutCome(EmvErrorCode.CLSS_USE_CONTACT, ETransStatus.TRY_ANOTHER_INTERFACE, ETransStep.CLSS_KERNEL_GPO_PROC);
                } else if (nRet == EmvErrorCode.CLSS_REFER_CONSUMER_DEVICE || nRet == EmvErrorCode.CLSS_DEVICE_NOT_AUTH) {
                    return new EmvOutCome(nRet, ETransStatus.SEE_PHONE_TRY_AGAIN, ETransStep.CLSS_KERNEL_GPO_PROC);
                } else {
                    return new EmvOutCome(nRet, ETransStatus.END_APPLICATION, ETransStep.CLSS_KERNEL_GPO_PROC);
                }
            }

            // Read card record data
            Log.d("Jeremy", "DPAS readData begin" );
            nRet = dpasPay.readData();
            Log.d("Jeremy", "DPAS readData end " + nRet);
            Log.d("Jeremy", "============== end ========================" );
            if (nRet != EmvErrorCode.CLSS_OK) {
                if (nRet == EmvErrorCode.CLSS_USE_CONTACT) {
                    return new EmvOutCome(EmvErrorCode.CLSS_USE_CONTACT, ETransStatus.TRY_ANOTHER_INTERFACE, ETransStep.CLSS_KERNEL_READ_DATA_PROC);
                } else {
                    return new EmvOutCome(nRet, ETransStatus.END_APPLICATION, ETransStep.CLSS_KERNEL_READ_DATA_PROC);
                }
            }

            // Read card info
            byte[] aucTrack2 = getTLV(0x57);
            if (aucTrack2 != null) { // Success to read card info
                String cardNo = getPan(BytesUtil.bytes2HexString(aucTrack2).split("F")[0]);
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

            if (dataBuf[0] == EmvErrorCode.CLSS_TRANSPATH_EMV) {
                addCapk(currentAid); // Add current capk param to kernel
            }

            // Start transaction
            nRet = dpasPay.transProc((byte)0);
            AppLog.d(TAG, "DPAS transProc nRet: " + nRet);
            int[] nODAReslutCode = new int[1];
            byte[] data = new byte[65];
            dpasPay.getDebugInfo(0x01, data, nODAReslutCode);
            AppLog.d(TAG, "DPAS nODAReslutCode: " + nODAReslutCode);
            if (nRet != EmvErrorCode.CLSS_OK) { // Error occurs
                if (nRet == EmvErrorCode.CLSS_USE_CONTACT) {
                    return new EmvOutCome(EmvErrorCode.CLSS_USE_CONTACT, ETransStatus.TRY_ANOTHER_INTERFACE, ETransStep.CLSS_KERNEL_TRANS_PROC);
                } else if (nRet == EmvErrorCode.CLSS_TRY_ANOTHER_CARD) {
                    return new EmvOutCome(nRet, ETransStatus.TRY_ANOTHER_INTERFACE, ETransStep.CLSS_KERNEL_TRANS_PROC);
                } else if (nRet == EmvErrorCode.CLSS_DECLINE ) {
                    return new EmvOutCome(nRet, ETransStatus.OFFLINE_DECLINED, ETransStep.CLSS_KERNEL_TRANS_PROC);
                } else {
                    return new EmvOutCome(nRet, ETransStatus.END_APPLICATION, ETransStep.CLSS_KERNEL_TRANS_PROC);
                }
            }
            if (nRet == EmvErrorCode.CLSS_OK || nRet == EmvErrorCode.CLSS_DECLINE) {
                AppRemovrCard();
            }

            // DF8129 tag  - If it need pwd
            clssOutComeData = getOutcomeData();
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

            //DF8129
            //Byte 1  b8-5
            // 0001 APPROVED
            // 0010 DECLIEND
            // 0011 ONLINE REQUEST
            // 0100 END APPLICATION
            // 0101 SELECE NEXT
            // 0111 TRY AGAIN
            // 1111 N/A
            AppLog.d(TAG, "DPAS OutcomeData: " + clssOutComeData.toString());
            switch (clssOutComeData.getUcOCStatus()) {
                case EmvErrorCode.CLSS_OC_APPROVED: // OFFLINE_APPROVE
                    // Offline success
                    AppLog.d(TAG, "DPAS OFFLINE_APPROVE");
                    return new EmvOutCome(EmvErrorCode.CLSS_OK, ETransStatus.OFFLINE_APPROVE, ETransStep.CLSS_KERNEL_TRANS_COMPLETE);
                case EmvErrorCode.CLSS_OC_ONLINE_REQUEST: // Request Online
                    // Online success
                    AppLog.d(TAG, "ARQC online success");

                    // Request for online processing and get online response data from issue bank
                    emvOnlineResp = AppReqOnlineProc();
                    AppLog.d(TAG, "ARQC AppReqOnlineProc " + emvOnlineResp.toString());

                    // Check response data and script
                    if (EOnlineResult.ONLINE_APPROVE == emvOnlineResp.geteOnlineResult()) {
                        if (emvOnlineResp.isExistAuthRespCode()) {
                            // Check 8A Authorisation Response Code
                            boolean transResSatus = EAuthRespCode.checkTransResSatus(convert.bcdToStr(emvOnlineResp.getAuthRespCode()), EKernelType.KERNTYPE_DPAS);
                            AppLog.d(TAG, "ARQC checkTransResSatus transResSatus " + transResSatus);
                            if (!transResSatus) { // Reject
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
                            boolean isExistIssScr = false;
                            ITlv tlv = TopTool.getInstance().getPacker().getTlv();
                            ITlv.ITlvDataObjList tlvList = tlv.createTlvDataObjectList();
                            if ((emvOnlineResp.isExistIssScr71() || emvOnlineResp.isExistIssScr72())
                                    && dataBuf[0] == EmvErrorCode.CLSS_TRANSPATH_EMV) {
                                // Second search card
                                boolean bSecSearchCardStatus = AppSearchCardbySecond();
                                AppLog.d(TAG, "bSecSearchCardStatus :" + bSecSearchCardStatus);
                                if (!bSecSearchCardStatus) {
                                    return new EmvOutCome(EmvErrorCode.CLSS_OK, ETransStatus.ONLINE_DECLINED, ETransStep.CLSS_KERNEL_TRANS_COMPLETE);
                                }

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

                                // Process script
                                if (isExistIssScr) {
                                    byte[] aucScript = tlv.pack(tlvList);;
                                    AppLog.d(TAG, "aucScript :" + convert.bcdToStr(aucScript));
                                    nRet = dpasPay.issuerUpdateProc(0x00, aucScript, new int[]{aucScript.length});
                                    AppLog.d(TAG, "issScriptProc nRet :" + nRet);
                                    if (nRet == EmvErrorCode.CLSS_OK) { // ONLINE_APPROVE
                                        return new EmvOutCome(EmvErrorCode.CLSS_OK, ETransStatus.ONLINE_APPROVE, ETransStep.CLSS_KERNEL_TRANS_COMPLETE);
                                    } else if (nRet == EmvErrorCode.CLSS_DECLINE) { // ONLINE_DECLINED
                                        return new EmvOutCome(EmvErrorCode.CLSS_OK, ETransStatus.ONLINE_DECLINED, ETransStep.CLSS_KERNEL_TRANS_COMPLETE);
                                    } else { // Abort emv process
                                        return new EmvOutCome(EmvErrorCode.CLSS_TERMINATE, ETransStatus.END_APPLICATION, ETransStep.CLSS_KERNEL_TRANS_COMPLETE);
                                    }
                                }
                            }
                            return new EmvOutCome(EmvErrorCode.CLSS_OK, ETransStatus.ONLINE_APPROVE, ETransStep.CLSS_KERNEL_TRANS_COMPLETE);
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
        } catch (Exception e) {
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
            if (EmvErrorCode.CLSS_OK == dpasPay.getTLVDataList(aucTag,aucTag.length,256,aucVale,aucLen)) {
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
//      System.arraycopy(aid, 0, rid, 0, 5);
        byte[] aucTLV = new byte[aucTag.length + aucLen.length + datas.length];
        System.arraycopy(aucTag,0,aucTLV,0,aucTag.length);
        System.arraycopy(aucLen,0,aucTLV,aucTag.length,aucLen.length);
        System.arraycopy(datas,0,aucTLV,(aucTag.length + aucLen.length),datas.length);

        AppLog.d(TAG, "setTLV TLV : " + convert.bcdToStr(aucTLV));
        try {
            int nRet = dpasPay.setTLVDataList(aucTLV,aucTLV.length);
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
            dpasPay.delAllRevocList();
            dpasPay.delAllCAPK();
            byte[] tlvCapkIndex = getTLV(0x8F);
            if (tlvCapkIndex != null && tlvCapkIndex.length == 0x01) {
                EmvCapk emvCapk = AppFindCapkParamsProc(BytesUtil.hexString2Bytes(aid), tlvCapkIndex[0]);
                if (emvCapk != null) {
                    res = dpasPay.addCAPK(emvCapk);
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
            int nRet = dpasPay.getDebugInfo(aucAssistInfo.length, aucAssistInfo, nErrcode);
            AppLog.e(TAG, "getDebugInfo nRet: " + nRet);
            AppLog.e(TAG, "getDebugInfo nErrcode: " + nErrcode[0]);
            AppLog.e(TAG, "getDebugInfo aucAssistInfo: " + new String(aucAssistInfo).trim());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
