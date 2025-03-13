package com.adpstore.flutter_smart_pin_pad_cards.transmit;

import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.adpstore.flutter_smart_pin_pad_cards.AClssKernelBaseTrans;
import com.adpstore.flutter_smart_pin_pad_cards.AppLog;
import com.adpstore.flutter_smart_pin_pad_cards.DeviceServiceManagers;
import com.adpstore.flutter_smart_pin_pad_cards.entity.ClssOutComeData;
import com.adpstore.flutter_smart_pin_pad_cards.entity.ClssUserInterRequestData;
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
import com.topwise.cloudpos.aidl.emv.level2.AidlPaypass;
import com.topwise.cloudpos.aidl.emv.level2.EmvCapk;
import com.topwise.cloudpos.struct.BytesUtil;
import com.topwise.cloudpos.struct.TlvList;

public class TransPayGPN extends AClssKernelBaseTrans {
    private static final String TAG = TransPayGPN.class.getSimpleName();
    private final AidlPaypass gpn = DeviceServiceManagers.getInstance().getL2Paypass();

    public TransPayGPN() {
    }

    // Add to your TransPayGPN class to directly check card responses
    private void testCardCommunication() {
        try {
            // Test selecting GPN application directly
            byte[] selectGPN = {0x00, (byte)0xA4, 0x04, 0x00, 0x07, (byte)0xA0, 0x00, 0x00, 0x06, 0x02, 0x00, 0x00, 0x00};
//            byte[] resp = DeviceServiceManagers.getInstance().getICCardReader().transmit(selectGPN);
//            AppLog.e(TAG, "GPN Select Response: " + BytesUtil.bytes2HexString(resp));
        } catch (Exception e) {
            AppLog.e(TAG, "Card test error: " + e.getMessage());
        }
    }

    @Override
    public EmvOutCome StartKernelTransProc() {
        try {
            AppLog.d(TAG, "start TransPayGPN =========== ");

            // Pass kernel type to payment app
            AppUpdateKernelType(EKernelType.KERNTYPE_GPN);

            // Initialize GPN kernel
            int nRet = gpn.initialize(1);
            AppLog.d(TAG, "initialize nRet: " + nRet);

            // Set final select data
            nRet = gpn.setFinalSelectData(clssTransParam.getAucFinalSelectFCIdata(), clssTransParam.getnFinalSelectFCIdataLen());
            AppLog.d(TAG, "setFinalSelectData res: " + nRet);
            if (nRet != EmvErrorCode.CLSS_OK) { // ERROR Occurs
                if (nRet == EmvErrorCode.EMV_SELECT_NEXT_AID) {
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
            // GPN specific parameters
// Make sure these GPN tags are set
            kernalList.addTlv("9F1D", new byte[]{0x6C, 0x00, (byte)0x80, 0x00, 0x00, 0x00, 0x00, 0x00});
            kernalList.addTlv("DF811B", new byte[]{(byte)0xB0});

            // Add GPN specific tag DF60 (Processing Options)
            kernalList.addTlv("DF60", new byte[]{0x02}); // Value based on GPN specifications

            AppLog.d(TAG, "entryL2 setKernalData ==== ");
            byte[] kernalData = kernalList.getBytes();
            if (kernalData != null) {
                AppLog.d(TAG, "setTLVDataList data: " + convert.bcdToStr(kernalData));

                nRet = gpn.setTLVDataList(kernalData, kernalData.length);
                AppLog.d(TAG, "setTLVDataList nRet: " + nRet);
                if (nRet != EmvErrorCode.CLSS_OK) {
                    return new EmvOutCome(EmvErrorCode.CLSS_TERMINATE, ETransStatus.END_APPLICATION, ETransStep.CLSS_SET_AID_PARAMS_TO_KERNEL);
                }
            }

            // Callback app final Select aid
            if (!AppFinalSelectAid()) {
                return new EmvOutCome(EmvErrorCode.CLSS_TERMINATE, ETransStatus.END_APPLICATION, ETransStep.CLSS_SET_AID_PARAMS_TO_KERNEL);
            }

            // GPO - Get Processing Options
            byte[] dataBuf = new byte[1];
            Log.d(TAG, "gpoProc begin");
            nRet = gpn.gpoProc(dataBuf);
            Log.d(TAG, "gpoProc end");
            AppLog.d(TAG, "gpoProc nRet: " + nRet);
            AppLog.d(TAG, "gpoProc Transaction Path: " + dataBuf[0]);
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
            Log.d(TAG, "readData begin");
            nRet = gpn.readData();
            Log.d(TAG, "readData nRet: " + nRet);
            Log.d(TAG, "============== end ========================");
            if (nRet != EmvErrorCode.CLSS_OK) {
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
            if (aucTrack2 != null) {
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

            // Start transaction
            byte[] ucAcType = new byte[1];
            AppLog.d(TAG, "ucType: " + dataBuf[0]);
            if (dataBuf[0] == EmvErrorCode.CLSS_TRANSPATH_EMV) {
                // Add current capk param to kernel
                addCapk(currentAid);

                AppLog.d(TAG, "Start transProc for GPN EMV path");
                nRet = gpn.transProcMChip(ucAcType);
                AppLog.d(TAG, "End transProc ucAcType= " + ucAcType[0]);
            } else if (dataBuf[0] == EmvErrorCode.CLSS_TRANSPATH_MAG) {
                nRet = gpn.transProcMag(ucAcType);
                AppLog.d(TAG, "transProcMag res: " + nRet);
            } else {
                nRet = EmvErrorCode.CLSS_TERMINATE;
            }

            AppLog.d(TAG, "trans proc res: " + nRet + "; ucAcType: " + ucAcType[0]);
            if (nRet != EmvErrorCode.CLSS_OK) {
                if (nRet == EmvErrorCode.CLSS_USE_CONTACT) {
                    return new EmvOutCome(EmvErrorCode.CLSS_USE_CONTACT, ETransStatus.TRY_ANOTHER_INTERFACE, ETransStep.CLSS_KERNEL_CARD_AUTH);
                } else if (nRet == EmvErrorCode.CLSS_DECLINE) {
                    return new EmvOutCome(EmvErrorCode.CLSS_DECLINE, ETransStatus.OFFLINE_DECLINED, ETransStep.CLSS_KERNEL_TRANS_COMPLETE);
                } else {
                    return new EmvOutCome(nRet, ETransStatus.END_APPLICATION, ETransStep.CLSS_KERNEL_CARD_AUTH);
                }
            }

            if (nRet == EmvErrorCode.CLSS_OK || nRet == EmvErrorCode.CLSS_DECLINE) {
                AppRemovrCard();
            }

            // Get outcome data for GPN
            clssOutComeData = getOutcomeData();
            AppLog.d(TAG, "GPN clssOutComeData: " + clssOutComeData.toString());

            switch (clssOutComeData.getUcOCStatus()) {
                case EmvErrorCode.CLSS_OC_APPROVED: // OFFLINE_APPROVE
                    // offline success
                    AppLog.d(TAG, " OFFLINE_APPROVE");
                    return new EmvOutCome(EmvErrorCode.CLSS_OK, ETransStatus.OFFLINE_APPROVE, ETransStep.CLSS_KERNEL_TRANS_COMPLETE);
                case EmvErrorCode.CLSS_OC_ONLINE_REQUEST:
                    // online success
                    AppLog.d(TAG, " ONLINE_REQUEST");
                    if (clssOutComeData.getUcOCCVM() == EmvErrorCode.CLSS_OC_ONLINE_PIN) { // CVM control force online pin
                        // Online enciphered PIN
                        emvPinEnter = AppReqImportPin(EPinType.ONLINE_PIN_REQ);
                        if (ECVMStatus.ENTER_OK != emvPinEnter.getEcvmStatus()) {
                            return new EmvOutCome(EmvErrorCode.EMV_USER_CANCEL, ETransStatus.END_APPLICATION, ETransStep.CLSS_KERNEL_TRANS_CVM);
                        }
                    }

                    // Request for online processing and get online response data from issue bank
                    emvOnlineResp = AppReqOnlineProc();
                    AppLog.d(TAG, "emvOnlineResp " + emvOnlineResp.toString());

                    // Check response data and script
                    if (EOnlineResult.ONLINE_APPROVE == emvOnlineResp.geteOnlineResult()) {
                        if (emvOnlineResp.isExistAuthRespCode()) {
                            // Check 8A Authorisation Response Code
                            boolean transResSatus = EAuthRespCode.checkTransResSatus(convert.bcdToStr(emvOnlineResp.getAuthRespCode()), EKernelType.KERNTYPE_GPN);
                            AppLog.d(TAG, "ARQC checkTransResSatus transResSatus " + transResSatus);
                            if (transResSatus) { // ONLINE_APPROVE
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
        } catch (Exception e) {
            e.getMessage();
        }
        return new EmvOutCome(EmvErrorCode.CLSS_TERMINATE, ETransStatus.END_APPLICATION, ETransStep.CLSS_KERNEL_TRANS_COMPLETE);
    }

    @Override
    public byte[] getTLV(int tag) {
        AppLog.d(TAG, "getTLV tag : " + tag);
        byte[] aucVale = new byte[256];
        int[] aucLen = new int[2];
        byte[] aucTag = TAGUtlis.tagFromInt(tag);
        AppLog.d(TAG, "getTLV aucTag : " + convert.bcdToStr(aucTag));
        try {
            if (EmvErrorCode.CLSS_OK == gpn.getTLVDataList(aucTag, aucTag.length, 256, aucVale, aucLen)) {
                byte[] aucResp = new byte[aucLen[0]];
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
            AppLog.d(TAG, "vale data is null ");
            return false;
        }
        AppLog.d(TAG, "setTLV T : " + tag);
        byte[] aucTag = TAGUtlis.tagFromInt(tag);
        AppLog.d(TAG, "setTLV T : " + convert.bcdToStr(aucTag));
        byte[] aucLen = TAGUtlis.genLen(datas.length);
        AppLog.d(TAG, "setTLV L : " + convert.bcdToStr(aucLen));
        AppLog.d(TAG, "setTLV V : " + convert.bcdToStr(datas));
        byte[] aucTLV = new byte[aucTag.length + aucLen.length + datas.length];
        System.arraycopy(aucTag, 0, aucTLV, 0, aucTag.length);
        System.arraycopy(aucLen, 0, aucTLV, aucTag.length, aucLen.length);
        System.arraycopy(datas, 0, aucTLV, (aucTag.length + aucLen.length), datas.length);

        AppLog.d(TAG, "setTLV TLV : " + convert.bcdToStr(aucTLV));
        try {
            int nRet = gpn.setTLVDataList(aucTLV, aucTLV.length);
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
     * Get current aid
     *
     * @return current application aid
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

    private ClssUserInterRequestData getUserInterRequestData() {
        byte[] tlvUserInterRequestData = getTLV(0xDF8116);
        if (tlvUserInterRequestData != null) {
            AppLog.d(TAG, "ClssUserInterRequestData : " + convert.bcdToStr(tlvUserInterRequestData));
            return new ClssUserInterRequestData(tlvUserInterRequestData);
        }
        return new ClssUserInterRequestData();
    }

    /**
     * Add CAPK to transaction kernel
     */
    @Override
    protected boolean addCapk(String aid) {
        try {
            int res;
            AppLog.d(TAG, "addCapk ======== aid: " + aid);
            gpn.delAllRevocList();
            gpn.delAllCAPK();
            byte[] tlvCapkIndex = getTLV(0x8F);
            if (tlvCapkIndex != null && tlvCapkIndex.length == 0x01) {
                EmvCapk emvCapk = AppFindCapkParamsProc(BytesUtil.hexString2Bytes(aid), tlvCapkIndex[0]);
                if (emvCapk != null) {
                    // For GPN - Add specific CAPK handling if needed
                    res = gpn.addCAPK(emvCapk);
                    AppLog.d(TAG, "gpn addCAPK res: " + res);
                    if (res == EmvErrorCode.CLSS_OK) {
                        return true;
                    }
                }
            }

            // Add GPN-specific CAPKs directly if needed
            addGpnSpecificCapks();

        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Add GPN-specific CAPKs directly
     */
    private boolean addGpnSpecificCapks() {
        try {
            // Add the GPN CAPKs directly from the provided parameters
            EmvCapk capk1 = new EmvCapk();
            capk1.setRID(BytesUtil.hexString2Bytes("A0000006029F"));
            capk1.setKeyID((byte)0x05);
            capk1.setModul(BytesUtil.hexString2Bytes("B48CC63D71A486DFC920608A3E42D7C305472BF76B8E50C8C02FB8387E788F72931A29DC15F913E7D69E43AD4C38A5C4317E36D15DE5F49FA2327D9754799D2484A6E156941ACA9632417E5C92931A85E1BB5F2A2C1B847D5008C7B30591F1ACBF3B98DFB0CF2849B6C7CDC7435AEA85F3A58BAC3B8C990416A5E19EC4EA08DC91CEF2FBE5940FA6622926D2AD0523D109A7024EB1035BBE37260B30F41AA52EEB36E60DD37120B9401C3850920F0E03"));
            capk1.setExponent(BytesUtil.hexString2Bytes("03"));
            capk1.setExpDate(BytesUtil.hexString2Bytes("20261231"));
            capk1.setCheckSum(BytesUtil.hexString2Bytes("1CAB162A1BE81492BB952C2846617B756F833C07"));
            int res1 = gpn.addCAPK(capk1);

            EmvCapk capk2 = new EmvCapk();
            capk2.setRID(BytesUtil.hexString2Bytes("A0000006029F"));
            capk2.setKeyID((byte)0x09);
            capk2.setModul(BytesUtil.hexString2Bytes("A517A338854E0856EE4AFDBF4BDA5DD3F9EB3895CBD8971B1E58A8EB167BF9935E0752DAEA7EAFB25E79D601EB201895A93F8B0A16D95A230366C05FEC55858C94D6097B2FB1EDDD2C6A3647DD0B71BC1DCDDC68B4E9ECC919FB544070952443159733471292993AB23E5B8C00E6A8526DF04A0B6E65E0F9D0378F71497E12FA83540B49FC05D0A86DC3D66FC4BB291A69B2EBB98D057C8F1EE7CB8E942FD05E9E4FAD0361BC184C13418C313C042C547DEF41310BA1850EF59CAF8CC7B14DAEE72FA4689C1047434024D565A3FA46EDCA3F53E236235268C893F268AA24AB2D20EB7AE06FF3123318041CB23E30839C58DFD4991D7C88CB"));
            capk2.setExponent(BytesUtil.hexString2Bytes("03"));
            capk2.setExpDate(BytesUtil.hexString2Bytes("20301231"));
            capk2.setCheckSum(BytesUtil.hexString2Bytes("E78686DB119C1CBFAD2149EF3CBE9CF54AC6321E"));
            int res2 = gpn.addCAPK(capk2);

            AppLog.d(TAG, "addGpnSpecificCapks res1: " + res1 + ", res2: " + res2);
            return (res1 == EmvErrorCode.CLSS_OK && res2 == EmvErrorCode.CLSS_OK);

        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void getDebugInfo() {
        byte aucAssistInfo[] = new byte[4096];
        int nErrcode[] = new int[1];
        try {
            int nRet = gpn.getDebugInfo(aucAssistInfo.length, aucAssistInfo, nErrcode);
            AppLog.e(TAG, "getDebugInfo nRet: " + nRet);
            AppLog.e(TAG, "getDebugInfo nErrcode: " + nErrcode[0]);
            AppLog.e(TAG, "getDebugInfo aucAssistInfo: " + new String(aucAssistInfo).trim());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
