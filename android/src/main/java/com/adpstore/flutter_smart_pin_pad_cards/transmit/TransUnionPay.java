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
import com.adpstore.flutter_smart_pin_pad_cards.impl.EContactlessQpbocCallback;
import com.adpstore.flutter_smart_pin_pad_cards.utils.DataUtils;
import com.topwise.cloudpos.aidl.emv.level2.AidlQpboc;
import com.topwise.cloudpos.aidl.emv.level2.EmvCapk;
import com.topwise.cloudpos.aidl.emv.level2.PreProcResult;
import com.topwise.cloudpos.struct.BytesUtil;
import com.topwise.cloudpos.struct.Tlv;
import com.topwise.cloudpos.struct.TlvList;

import java.util.Map;

/**
 *Creation date: 2021/6/16 on 10:52
 * Description: Union Pay card
 * Author: Adam Permana
 */
public class TransUnionPay extends AClssKernelBaseTrans {
    private static final String TAG = TransUnionPay.class.getSimpleName();
    private AidlQpboc aidlQpboc = DeviceServiceManagers.getInstance().getL2Qpboc();

    private byte[] version = new byte[64];

    public TransUnionPay() {
        try {
            aidlQpboc.getVersion(version, 64);
            Log.d(TAG, "TransUnionPay version: " + new String(version).trim());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * start Contactless process
     * @return EmvResult
     */
    @Override
    public EmvOutCome StartKernelTransProc() {

        try {
            // Get preProcResult
            PreProcResult preProcResult = clssTransParam.getPreProcResult();

            // Pass kernel type to payment app
            AppUpdateKernelType(EKernelType.KERNTYPE_QPBOC);

            // Initialize UnionPay kernel
            int nRet = aidlQpboc.initialize((byte)EmvErrorCode.KERNTYPE_QPBOC, (byte)0);
            if (nRet != EmvErrorCode.CLSS_OK) {
                return new EmvOutCome(nRet, ETransStatus.END_APPLICATION, ETransStep.CLSS_KERNEL_INIT);
            }

            // Set callback
            nRet = aidlQpboc.setCallback(new EContactlessQpbocCallback());
            if (nRet != EmvErrorCode.CLSS_OK) {
                return new EmvOutCome(nRet, ETransStatus.END_APPLICATION, ETransStep.CLSS_KERNEL_INIT);
            }

            // Set final select data
            Log.d("Jeremy", "setFinalSelectData begin ");
            nRet = aidlQpboc.setFinalSelectData(clssTransParam.getAucFinalSelectFCIdata(), clssTransParam.getnFinalSelectFCIdataLen());
            Log.d("Jeremy", "setFinalSelectData end " +nRet);
            if (nRet != EmvErrorCode.CLSS_OK) {
                if (nRet == EmvErrorCode.CLSS_RESELECT_APP){
                    nRet = entryL2.delCandListCurApp();
                    AppLog.d(TAG, "entryL2 delCandListCurApp nRet: " + nRet);
                    if (nRet == EmvErrorCode.CLSS_OK) {
                        return new EmvOutCome(EmvErrorCode.CLSS_RESELECT_APP, ETransStatus.SELECT_NEXT_AID, ETransStep.CLSS_KERNEL_SET_FINAL_SELECT_DATA);
                    } else {
                        return new EmvOutCome(nRet, ETransStatus.END_APPLICATION, ETransStep.CLSS_KERNEL_SET_FINAL_SELECT_DATA);
                    }
                } else {
                    return new EmvOutCome(nRet, ETransStatus.END_APPLICATION, ETransStep.CLSS_KERNEL_SET_FINAL_SELECT_DATA);
                }
            }

            // Get current aid and notice payment app
            String currentAid = getCurrentAid();
            if (TextUtils.isEmpty(currentAid)){
                return new EmvOutCome(EmvErrorCode.CLSS_TERMINATE, ETransStatus.END_APPLICATION, ETransStep.CLSS_SET_AID_PARAMS_TO_KERNEL);
            }

            // Load AID TLV list by aid
            Log.d("Jeremy", "AppGetKernalDataFromAidParam begin " );
            TlvList kernalList = AppGetKernalDataFromAidParam(currentAid);
            Log.d("Jeremy", "AppGetKernalDataFromAidParam end " );
            if (kernalList == null) {
                return new EmvOutCome(EmvErrorCode.CLSS_TERMINATE, ETransStatus.END_APPLICATION, ETransStep.CLSS_SET_AID_PARAMS_TO_KERNEL);
            }

            // Set tlv data list into kernel
            // Set TTQ
            if (preProcResult != null) {
                byte[] aucReaderTTQ = preProcResult.getAucReaderTTQ();
                if (aucReaderTTQ != null) {
                    byte aucTTQ[] = new byte[4];
                    System.arraycopy(aucReaderTTQ, 0, aucTTQ, 0, 4);
                    kernalList.addTlv("9F66", aucTTQ);
                    AppLog.d(TAG, "Union preProcResult TTQ :"+ convert.bcdToStr(aucTTQ));
                } else {
                    //
                    kernalList.addTlv("9F66","3600C080");
                }
            }

            Log.d("Jeremy", "setTLVDataList  begin" );
            byte [] kernalData = kernalList.getBytes();
            if (kernalData != null) {
                nRet = setTLVDataList(kernalData,kernalData.length);
            }
            Log.d("Jeremy", "setTLVDataList  end " + nRet);

            // Callback app final Select aid
            if (!AppFinalSelectAid()) {
                return new EmvOutCome(EmvErrorCode.CLSS_TERMINATE, ETransStatus.END_APPLICATION, ETransStep.CLSS_SET_AID_PARAMS_TO_KERNEL);
            }

            // Application Initialization (GPO)
            byte[] transPath = new byte[1];
            Log.d("Jeremy", "gpoProc  begin" );
            nRet = aidlQpboc.gpoProc(transPath);
            Log.d("Jeremy", "gpoProc  end " + nRet);
            if (nRet == EmvErrorCode.EMV_SELECT_NEXT_AID) {
                nRet = entryL2.delCandListCurApp();
                if (nRet == EmvErrorCode.CLSS_OK) {
                    return new EmvOutCome(EmvErrorCode.CLSS_RESELECT_APP, ETransStatus.SELECT_NEXT_AID, ETransStep.CLSS_KERNEL_GPO_PROC);
                } else {
                    return new EmvOutCome(EmvErrorCode.CLSS_USE_CONTACT, ETransStatus.TRY_ANOTHER_INTERFACE, ETransStep.CLSS_KERNEL_GPO_PROC);
                }
            } else if (nRet == EmvErrorCode.EMV_TRY_OTHER_INTERFACE) {
                return new EmvOutCome(EmvErrorCode.CLSS_USE_CONTACT, ETransStatus.TRY_ANOTHER_INTERFACE, ETransStep.CLSS_KERNEL_GPO_PROC);
            } else if (nRet == EmvErrorCode.EMV_SEE_PHONE) {
                return new EmvOutCome(EmvErrorCode.CLSS_REFER_CONSUMER_DEVICE, ETransStatus.SEE_PHONE_TRY_AGAIN, ETransStep.CLSS_KERNEL_GPO_PROC);
            } else if (nRet == EmvErrorCode.EMV_TRY_AGAIN) {
                return new EmvOutCome(EmvErrorCode.CLSS_TRY_AGAIN, ETransStatus.TRY_AGAIN, ETransStep.CLSS_KERNEL_GPO_PROC);
            } else if (nRet == EmvErrorCode.EMV_APPROVED && nRet == EmvErrorCode.EMV_DECLINED &&
                    nRet == EmvErrorCode.EMV_ONLINE_REQUEST) {
                return new EmvOutCome(EmvErrorCode.CLSS_TERMINATE, ETransStatus.END_APPLICATION, ETransStep.CLSS_KERNEL_GPO_PROC);
            }
            if (transPath[0] != EmvErrorCode.CLSS_TRANSPATH_EMV) {
                return new EmvOutCome(EmvErrorCode.CLSS_TERMINATE, ETransStatus.END_APPLICATION, ETransStep.CLSS_KERNEL_GPO_PROC);
            }

            // Read card record data
            Log.d("Jeremy", "readData begin ");
            nRet = aidlQpboc.readData();
            Log.d("Jeremy", "readData end " + nRet);
            if (nRet != EmvErrorCode.EMV_APPROVED && nRet != EmvErrorCode.EMV_DECLINED
                    && nRet != EmvErrorCode.EMV_ONLINE_REQUEST) {
                return new EmvOutCome(EmvErrorCode.CLSS_TERMINATE, ETransStatus.END_APPLICATION, ETransStep.CLSS_KERNEL_READ_DATA_PROC);
            }
            AppLog.d(TAG, "============== end ========================" );
            Log.d("Jeremy", "============== end ========================" );

            // Read card info
            AppLog.d(TAG, "cardConfirm begin ");
            byte[] aucTrack2 = getTLV(0x57);
            if (aucTrack2 != null) {
                String cardNo =  getPan(BytesUtil.bytes2HexString(aucTrack2).split("F")[0]);
                if (!DataUtils.isNullString(cardNo)) {
                    if (!AppConfirmPan(cardNo)) {
                        return new EmvOutCome(EmvErrorCode.EMV_USER_CANCEL, ETransStatus.END_APPLICATION, ETransStep.CLSS_APP_CONFIRM_PAN);
                    }
                }
            }

            AppLog.d(TAG, "cardConfirm end ");

            // UnionPay refund
            if (clssTransParam.isbSupSimpleProc() && nRet == EmvErrorCode.EMV_ONLINE_REQUEST) {
                AppLog.d(TAG, "Simple process return : " + nRet);
                return new EmvOutCome(EmvErrorCode.CLSS_OK, ETransStatus.ONLINE_REQUEST, ETransStep.CLSS_KERNEL_TRANS_COMPLETE);
            }

            // AppRemovrCard();

            // Offline Data Authentication
            if (nRet == EmvErrorCode.EMV_APPROVED) {
                // Add current capk param to kernel
                addCapk(currentAid);

                // Offline data process
                nRet = aidlQpboc.cardAuth();
                AppLog.d(TAG, "aidlQpboc.readData res: " + nRet);
                getDebugInfo();
                if (nRet == EmvErrorCode.EMV_TRY_OTHER_INTERFACE) {
                    int [] debugErrCode = new int[1];
                    aidlQpboc.getDebugInfo(0,null,debugErrCode);
                    if (EmvErrorCode.QPBOC_USE_CONTACT == debugErrCode[0]) {
                        return new EmvOutCome(EmvErrorCode.CLSS_USE_CONTACT, ETransStatus.TRY_ANOTHER_INTERFACE, ETransStep.CLSS_KERNEL_CARD_AUTH);
                    }
                }
            }

            // DF8129 tag  - If it needs pwd
            clssOutComeData = getOutcomeData();
            AppLog.d(TAG, "UnionPay clssOutComeData: " + clssOutComeData.toString());
            if (clssOutComeData.getUcOCCVM() == EmvErrorCode.CLSS_OC_ONLINE_PIN) { // CVM control force online pin
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
            }

            AppLog.d(TAG, "nRet === " + nRet);
            switch (nRet) {
                case EmvErrorCode.EMV_APPROVED:
                    // offline success 脱机批准
                    AppLog.d(TAG, "TC offline success");
                    return new EmvOutCome(EmvErrorCode.CLSS_OK, ETransStatus.OFFLINE_APPROVE, ETransStep.CLSS_KERNEL_TRANS_COMPLETE);
                case EmvErrorCode.EMV_ONLINE_REQUEST:
                    // online success 联机请求
                    // Request for online processing and get online response data from issue bank
                    AppLog.d(TAG, "ARQC online success");
                    emvOnlineResp = AppReqOnlineProc();
                    AppLog.d(TAG, " emvOnlineResp " + emvOnlineResp.toString());
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
                    return new EmvOutCome(EmvErrorCode.CLSS_OK,ETransStatus.ONLINE_DECLINED, ETransStep.CLSS_KERNEL_TRANS_COMPLETE);
                case EmvErrorCode.EMV_DECLINED:
                    // transaction reject 交易拒绝
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
        try {
            AppLog.e(TAG,"getTlv TAG= " + tag );
            byte[] tlvData = aidlQpboc.getTLVData(tag);
            AppLog.e(TAG,"getTlv TAG=  end "  );

            if (tlvData != null) {
                return tlvData;
            }
            return new byte[0];
        } catch (RemoteException e) {
            e.printStackTrace();
            AppLog.e(TAG,"getTlv Exception TAG= " + tag + " Exception= " + e.getMessage());
        }
        return new byte[0];
    }

    @Override
    public boolean setTLV(int tag, byte[] datas) {
        if (datas == null){
            return false;
        }
        AppLog.e(TAG,"setTLV TAG= " + tag);
        AppLog.e(TAG,"setTLV Vale= " + convert.bcdToStr(datas) );
        try {
            int nRet = aidlQpboc.setTLVData(tag,datas);
            AppLog.e(TAG,"setTLVData nRet= " + nRet);
            if (nRet == EmvErrorCode.EMV_OK) {
                return true;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            AppLog.e(TAG,"EMV_SetTLVData Exception TAG= " + tag + " Exception= " + e.getMessage());
        }
        return false;
    }


    /**
     * set the kernal data  usdk v2.0.93 support setTLVDataList
     * @param pTLVDatas
     * @param iDataLen
     * @return
     */
    private int setTLVDataList(byte[] pTLVDatas, int iDataLen) {
        int nRet =-1;
        /*try {
            String soCompileTime = new String(version).trim();
            AppLog.d(TAG, "setTLVData soCompileTime: " + soCompileTime);
            soCompileTime = soCompileTime.substring(soCompileTime.length() - 10);
            AppLog.d(TAG, "setTLVData soCompileTime: " + soCompileTime);
            if (!TextUtils.isEmpty(soCompileTime)) {
                if (soCompileTime.compareTo("2023.08.18") >= 0) {
                    AppLog.d(TAG, "setTLVDataList start");
                    nRet = aidlQpboc.setTLVDataList(pTLVDatas,iDataLen);
                    AppLog.d(TAG, "setTLVDataList nRet: " + nRet);
                    return nRet;
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }*/

        TlvList tlvlist = new TlvList();
        AppLog.d(TAG, "pTLVDatas: " + BytesUtil.bytes2HexString(pTLVDatas));
        tlvlist.fromBytes(pTLVDatas);
        if (tlvlist.getList() != null && tlvlist.getList().size() > 0) {
            for (Map.Entry<String, Tlv> entry : tlvlist.getList().entrySet()) {
                byte[] bTag = BytesUtil.hexString2Bytes(entry.getValue().getTag());
                AppLog.d(TAG, "bTag: " + BytesUtil.bytes2HexString(bTag));
                byte[] bTag4Bytes = new byte[4];
                java.util.Arrays.fill(bTag4Bytes, (byte)0);
                System.arraycopy(bTag, 0, bTag4Bytes, bTag4Bytes.length - bTag.length, bTag.length);
                AppLog.d(TAG, "bTag4Bytes: " + BytesUtil.bytes2HexString(bTag4Bytes));
                //The first parameter of 'BytesUtil.bytes2Int' must be 4 bytes
                int iTag = BytesUtil.bytes2Int(bTag4Bytes, true);
                AppLog.d(TAG, "iTag: " + iTag);
                AppLog.d(TAG, "Value: " + BytesUtil.bytes2HexString(entry.getValue().getValue()));
                try {
                    nRet = aidlQpboc.setTLVData(iTag, entry.getValue().getValue());
                    AppLog.d(TAG, "setTLVData nRet: " + nRet);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        return nRet;
    }

    @Override
    protected String getCurrentAid() {
        byte[] tlvVale = getTLV(0x9F06);
        if (tlvVale != null) {
            AppLog.d(TAG, "getCurrentAid TAG 9F06: " + convert.bcdToStr(tlvVale));
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

    @Override
    protected boolean addCapk(String aid) {
        try{
            int res;
            AppLog.d(TAG, "addCapk ======== aid: " + aid);
            aidlQpboc.delAllRevoIPK();
            aidlQpboc.delAllCAPK();
            byte[] tlvCapkIndex = getTLV(0x8F);
            if (tlvCapkIndex != null && tlvCapkIndex.length == 0x01) {
                EmvCapk emvCapk = AppFindCapkParamsProc(BytesUtil.hexString2Bytes(aid), tlvCapkIndex[0]);
                if (emvCapk != null) {
                    res = aidlQpboc.addCAPK(emvCapk);
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
            int nRet = aidlQpboc.getDebugInfo(aucAssistInfo.length,aucAssistInfo,nErrcode);
            AppLog.e(TAG, "getDebugInfo nRet: " + nRet);
            AppLog.e(TAG, "getDebugInfo nErrcode: " + nErrcode[0]);
            AppLog.e(TAG, "getDebugInfo aucAssistInfo: " + new String(aucAssistInfo).trim());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
