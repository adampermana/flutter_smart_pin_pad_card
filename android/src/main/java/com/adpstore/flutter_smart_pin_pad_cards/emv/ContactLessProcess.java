package com.adpstore.flutter_smart_pin_pad_cards.emv;

import android.util.Log;

import com.adpstore.flutter_smart_pin_pad_cards.emv.entity.ClssPreProcResult;
import com.adpstore.flutter_smart_pin_pad_cards.emv.entity.ClssTransParam;
import com.adpstore.flutter_smart_pin_pad_cards.emv.entity.EmvAidParam;
import com.adpstore.flutter_smart_pin_pad_cards.emv.entity.EmvErrorCode;
import com.adpstore.flutter_smart_pin_pad_cards.emv.entity.EmvOnlineResp;
import com.adpstore.flutter_smart_pin_pad_cards.emv.entity.EmvOutCome;
import com.adpstore.flutter_smart_pin_pad_cards.emv.entity.EmvPinEnter;
import com.adpstore.flutter_smart_pin_pad_cards.emv.enums.EKernelType;
import com.adpstore.flutter_smart_pin_pad_cards.emv.enums.EPinType;
import com.adpstore.flutter_smart_pin_pad_cards.emv.enums.ETransStatus;
import com.adpstore.flutter_smart_pin_pad_cards.emv.enums.ETransStep;
import com.topwise.cloudpos.aidl.emv.level2.AidlEntry;
import com.topwise.cloudpos.aidl.emv.level2.CandList;
import com.topwise.cloudpos.aidl.emv.level2.EmvCapk;
import com.topwise.cloudpos.aidl.emv.level2.PreProcResult;
import com.topwise.cloudpos.aidl.emv.level2.TransParam;
import com.topwise.cloudpos.struct.BytesUtil;
import com.topwise.cloudpos.struct.TlvList;
import com.topwise.toptool.api.convert.IConvert;
import com.topwise.toptool.impl.Convert;
import com.topwise.toptool.impl.TopTool;

import java.util.ArrayList;
import java.util.List;

public class ContactLessProcess extends ABaseTransProcess {
//    TODO: Masih Error
    private static final String TAG = ContactLessProcess.class.getSimpleName();
    private AidlEntry entryL2 = AdpUsdkManage.getInstance().getEntry();
    protected AClssKernelBaseTrans aClssKernelBaseTrans;
    private List<ClssPreProcResult> mPreProcResultList = new ArrayList<>();
    IConvert convert = TopTool.getInstance().getConvert();
    private ClssTransParam clssTransParam;

    public ContactLessProcess() { }

    @Override
    public EmvOutCome StartTransProcess() {
        EmvOutCome emvOutCome = new EmvOutCome(EmvErrorCode.CLSS_DECLINE);
        Log.d("Jeremy", "StartTransProcess===");

        // Call preProcessing and getPreProcResult without initialization
        emvOutCome = init();
        AppLog.d(TAG, "emvProcess===" + emvOutCome.toString());
        if (emvOutCome.getnErrorCodeL2() != EmvErrorCode.CLSS_OK) {
            return emvOutCome;
        }

        try {
            AppLog.d(TAG, "emvProcess requestImportAmount " + emvTransData.toString());

            for (;;) {
                // Final select and save clssTransParam
                emvOutCome = AppFinalSelectAid();
                AppLog.d(TAG, "emvProcess finalSelectAid emvResult " + emvOutCome.toString());
                if (emvOutCome.getnErrorCodeL2() != EmvErrorCode.CLSS_OK) { // Abort emv process
                    return emvOutCome;
                }

                // Get kernel type
                aClssKernelBaseTrans = checkKernalType(clssTransParam.getKernType());
                if (aClssKernelBaseTrans == null) {
                    return new EmvOutCome(EmvErrorCode.CLSS_TRY_ANOTHER_CARD, ETransStatus.END_APPLICATION, ETransStep.CLSS_ENTRY_FINAL_SELECT);
                }

                // Set listener
                aClssKernelBaseTrans.setProcessListener(new AClssKernelBaseTrans.kernekTransProcessListener() {
                    @Override
                    public void onUpdateKernelType(EKernelType eKernelType) {
                        AppLog.d(TAG, "onUpdateKernelType ======"+eKernelType.toString() );
                        // update kernal Type
                        if (emvProcessListener != null) {
                            emvProcessListener.onUpToAppKernelType(eKernelType);
                        }
                    }

                    @Override
                    public TlvList onGetKernalDataFromAidParams(String Aid) {
                        AppLog.d(TAG, "onGetKernalDataFromAidParams ======" );
                        if (emvProcessListener != null) {
                            curAidParam = emvProcessListener.onFindCurAidParamProc(Aid);
                            if (curAidParam != null) {
                                return handleKernalData();
                            }
                        }
                        return null;
                    }

                    @Override
                    public boolean onFinalSelectAid() {
                        AppLog.d(TAG, "onFinalSelectAid ======");
                        if (emvProcessListener != null) {
                            return emvProcessListener.onReqFinalAidSelect();
                        }
                        return false;
                    }

                    @Override
                    public boolean onConfirmPan(String pan) {
                        AppLog.d(TAG, "onConfirmPan ======" + pan);
                        if (emvProcessListener != null) {
                            return emvProcessListener.onConfirmCardInfo(pan);
                        }
                        return false;
                    }

                    @Override
                    public EmvPinEnter onReqImportPin(EPinType ePinType) {
                        AppLog.d(TAG, "onConfirmPan ======" + ePinType);
                        if (emvProcessListener != null) {
                            return emvProcessListener.onReqGetPinProc(ePinType,0x00);
                        }
                        return null;
                    }

                    @Override
                    public EmvCapk onFindCapkProc(byte[] rid, byte index) {
                        AppLog.d(TAG, "onSelectCapk ======" );
                        AppLog.d(TAG, "onSelectCapk rid: " + BytesUtil.bytes2HexString(rid));
                        AppLog.d(TAG, "onSelectCapk ridindex: " + BytesUtil.byte2HexString(index));
                        byte[] ridData = new byte[5];
                        System.arraycopy(rid, 0, ridData, 0, ridData.length);
                        return emvProcessListener.onFindIssCapkParamProc(TopTool.getInstance().getConvert().bcdToStr(ridData), index);
                    }

                    @Override
                    public void onRemoveCardProc() {
                        AppLog.d(TAG, "onRemoveCardProc====== begin" );
                        if (emvProcessListener != null) {
                            emvProcessListener.onRemoveCardProc();
                        }
                        AppLog.d(TAG, "onRemoveCardProc====== end" );
                    }

                    @Override
                    public EmvOnlineResp onReqOnlineProc() {
                        AppLog.d(TAG, "onOnlineRequest======" );
                        if (emvProcessListener != null) {
                            return emvProcessListener.onReqOnlineProc();
                        }
                        return null;
                    }

                    @Override
                    public boolean onSecondSearchCardProc() {
                        if (emvProcessListener != null) {
                            return emvProcessListener.onSecCheckCardProc();
                        }
                        return false;
                    }
                });

                // Set input Params
                aClssKernelBaseTrans.setInputParam(clssTransParam);

                // Start Trans process
                emvOutCome = aClssKernelBaseTrans.StartKernelTransProc();
                AppLog.d(TAG, "emvProcess StartKernelTransProc emvOutCome " + emvOutCome.toString());
                if (emvOutCome.getnErrorCodeL2() == EmvErrorCode.CLSS_RESELECT_APP) { // If return select aid again will continue
                    AppLog.d(TAG, "emvProcess Select aid again ");
                    continue;
                } else {
                    return emvOutCome;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new EmvOutCome(EmvErrorCode.CLSS_TERMINATE);
    }

    @Override
    public byte[] getTLV(int tag) {
        if (aClssKernelBaseTrans != null) {
            AppLog.e(TAG,"getTlv TAG = " + tag);
            return aClssKernelBaseTrans.getTLV(tag);
        }
        return new byte[0];
    }

    @Override
    public boolean setTLV(int tag, byte[] datas) {
        if (aClssKernelBaseTrans !=null) {
            return aClssKernelBaseTrans.setTLV(tag,datas);
        }
        return false;
    }

    @Override
    public void getDebugInfo() {
        if (aClssKernelBaseTrans !=null){
            aClssKernelBaseTrans.getDebugInfo();
        }
    }

    /**
     * Call preProcessing and get preProcResult without initialization
     *
     * From preProcessing to get preProcResult list
     *
     * @return
     */
    private EmvOutCome init() {
        try {
            int emvRest = -1;
            if (mPreProcResultList != null) {
                mPreProcResultList.clear();
            }

            long amount = emvTransData.getAmount();
            long amountOther = emvTransData.getAmountOther();
            AppLog.d(TAG, "init preProcessing amount " + amount);
            AppLog.d(TAG, "init preProcessing amountOther " + amountOther);
            AppLog.d(TAG, "init preProcessing check mTransParam ");

            curTransParam = new TransParam();
            curTransParam.setAucAmount(convert.strToBcd(String.format("%012d", amount), IConvert.EPaddingPosition.PADDING_RIGHT));
            curTransParam.setAucAmountOther(convert.strToBcd(String.format("%012d", amountOther), IConvert.EPaddingPosition.PADDING_RIGHT));
            curTransParam.setUcTransType(emvTransData.getTransType());
            curTransParam.setAucTransCurCode(convert.strToBcd(emvTransData.getAucTransCurCode(), IConvert.EPaddingPosition.PADDING_RIGHT));
            curTransParam.setAucTransTime(convert.strToBcd(emvTransData.getAucTransTime(), IConvert.EPaddingPosition.PADDING_RIGHT));
            curTransParam.setAucTransDate(convert.strToBcd(emvTransData.getAucTransDate(), IConvert.EPaddingPosition.PADDING_RIGHT));
            curTransParam.setUlTransNo(emvTransData.getTransNo());

            // PreProcessing
            emvRest = entryL2.preProcessing(curTransParam);
            AppLog.d(TAG, "init preProcessing: " + emvRest);
            if (emvRest != EmvErrorCode.CLSS_OK) {
                // Use other interface
                AppLog.e(TAG, "init ErrorCode: " + entryL2.getErrorCode());
                return new EmvOutCome(emvRest, ETransStatus.TRY_ANOTHER_INTERFACE, ETransStep.CLSS_ENTRY_PREPROC);
            }

            // Build candidate app list
            Log.d("Jeremy","init buildCandidate begin ");
            emvRest = entryL2.buildCandidate(0, 0);
            Log.d("Jeremy","init buildCandidate end " + emvRest);
            if (emvRest != EmvErrorCode.CLSS_OK) {
                // need to handle 6A82 error
                int errorCode = entryL2.getErrorCode();
                AppLog.d(TAG, "init buildCandidate getErrorCode: " + errorCode);
                if (EmvErrorCode.ICC_CMD_ERR == emvRest) {
                    return new EmvOutCome(emvRest, ETransStatus.TRY_AGAIN, ETransStep.CLSS_ENTRY_BUILD_CANDI_DATE);
                } else {
                    return new EmvOutCome(emvRest, ETransStatus.END_APPLICATION, ETransStep.CLSS_ENTRY_BUILD_CANDI_DATE);
                }
            }
            AppLog.d(TAG, " preProcResult begin ");

            // Get candidate list
            CandList[] candLists = new CandList[5];
            int [] nSize = new int[1];
            emvRest = entryL2.getCandListNew(candLists, nSize);
            AppLog.e(TAG, "init getCandListNew: emvRest:" + emvRest + " nSize:" +nSize[0]);
            for (int nI = 0; nI < nSize[0]; nI++) {
                PreProcResult preProcResult = new PreProcResult();
                // Get candidate item's AID and kernel ID
                byte [] aucAID = new byte[candLists[nI].getUcIccAidLen()];
                byte [] aucKernelID = new byte[candLists[nI].getUcKernIDLen()];
                System.arraycopy(candLists[nI].getAucIccAID(),0, aucAID,0, aucAID.length);
                System.arraycopy(candLists[nI].getAucKernelID(),0, aucKernelID,0, aucKernelID.length);
                // get preProcessResult by AID
                emvRest = entryL2.getPreProcResultByAid(aucAID, candLists[nI].getUcIccAidLen(),
                        aucKernelID, candLists[nI].getUcKernIDLen(), preProcResult);
                // Add candidate's preProcessResult to list
                if (emvRest == 0x00 && preProcResult.getUcAidLen() > 0) { // Success
                    ClssPreProcResult clssPreProcResult =  new ClssPreProcResult();
                    clssPreProcResult.setPreProcResult(preProcResult);
                    clssPreProcResult.setAucCandListAID(candLists[nI].getAucIccAID());
                    clssPreProcResult.setUnCandListAIDLen( candLists[nI].getUcIccAidLen());
                    mPreProcResultList.add(clssPreProcResult);
                } else { // Try again with another API
                    entryL2.getPreProcResult(preProcResult);
                    ClssPreProcResult clssPreProcResult =  new ClssPreProcResult();
                    clssPreProcResult.setPreProcResult(preProcResult);
                    clssPreProcResult.setAucCandListAID(candLists[nI].getAucIccAID());
                    clssPreProcResult.setUnCandListAIDLen( candLists[nI].getUcIccAidLen());
                    mPreProcResultList.add(clssPreProcResult);
                }
            }
            AppLog.d(TAG, " preProcResult end ");

            if (mPreProcResultList.size() == 0x00) { // No preProcResult data, abort EMV process
                return new EmvOutCome(EmvErrorCode.EMV_NOT_FOUND, ETransStatus.END_APPLICATION, ETransStep.CLSS_ENTRY_GET_PRE_PROC_RES);
            }
            // End with Success status
            return new EmvOutCome(EmvErrorCode.CLSS_OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // End with error status
        return new EmvOutCome(EmvErrorCode.CLSS_PARAM_ERR);
    }

    /**
     * Get current kernel by kernel type
     * @param kernalType
     * @return
     */
    private AClssKernelBaseTrans checkKernalType(byte kernalType) {
        AppLog.d(TAG, " checkKernalType kernalType==== " + kernalType);
        switch (kernalType) {
            case EmvErrorCode.KERNTYPE_QPBOC:
                return new TransUnionPay();
            case EmvErrorCode.KERNTYPE_MC:
                return new TransPayPass();
            case EmvErrorCode.KERNTYPE_VISA:
                return new TransPayWave();
            case EmvErrorCode.KERNTYPE_PURE:
                return new TransPurePay();
            case EmvErrorCode.KERNTYPE_AMEX:
                return new TransAmexPay();
            case EmvErrorCode.KERNTYPE_DPAS:
                return new TransDpasPay();
            case EmvErrorCode.KERNTYPE_MIR:
                return new TransMirPay();
            case EmvErrorCode.KERNTYPE_RUPAY:
                return new TransRuPay();
            case EmvErrorCode.KERNTYPE_JCB:
                return new TransJcbPay();
            default:
                return null;
        }
    }

    /**
     * Directly call finalSelect method and get current preProcResult data, finally save transaction param.
     * @return
     */
    private EmvOutCome AppFinalSelectAid() {
        try {
            int emvRest = -1;
            AppLog.d(TAG, " finalSelect Again");

            while (true) {
                byte[] outData = new byte[300];
                byte[] ucKernType = new byte[1];
                int[] unFCIlen = new int[1];
                AppLog.d(TAG, " finalSelect begin");
                Log.d(TAG, " finalSelect begin");

                // Final select
                emvRest = entryL2.finalSelect(ucKernType, outData, unFCIlen);
                Log.d(TAG, " finalSelect end");
                AppLog.d(TAG, " finalSelect end");
                if (emvRest == EmvErrorCode.CLSS_OK) {
                    clssTransParam = new ClssTransParam();
                    // Get FCI data
                    byte[] aucFCIData = new byte[unFCIlen[0]];
                    System.arraycopy(outData , 0, aucFCIData, 0, aucFCIData.length);
                    AppLog.d(TAG, "emvProcess Aid outData: " + BytesUtil.bytes2HexString(aucFCIData));
                    AppLog.d(TAG, "emvProcess onUpdateKernelType: " + ucKernType[0]);

                    // Get AID from FCI
                    byte aucFinalSelectAid [] = new byte[aucFCIData[0]];
                    System.arraycopy(aucFCIData, 1, aucFinalSelectAid, 0, aucFinalSelectAid.length);
                    AppLog.d(TAG, "emvProcess aucFinalSelectAid: " + Convert.getInstance().bcdToStr(aucFinalSelectAid));

                    // Search for selected preProcResult by AID (returned by final select api)
                    for (ClssPreProcResult procResult : mPreProcResultList) {
                        byte aucPreProcResultAid [] = new byte[procResult.getUnCandListAIDLen()];
                        System.arraycopy(procResult.getAucCandListAID(), 0, aucPreProcResultAid, 0, aucPreProcResultAid.length);

                        AppLog.d(TAG, "emvProcess preProcResult.getAucReaderTTQ(): " + Convert.getInstance().bcdToStr(procResult.getPreProcResult().getAucReaderTTQ()));
                        AppLog.d(TAG, "emvProcess preProcResult.getAucAID: " + Convert.getInstance().bcdToStr(aucPreProcResultAid));

                        if (Convert.getInstance().bcdToStr(aucPreProcResultAid).contentEquals(Convert.getInstance().bcdToStr(aucFinalSelectAid))) {
                            clssTransParam.setPreProcResult(procResult.getPreProcResult());
                            break;
                        }
                    }

                    // 如果无法根据candidate list的AID获取到预处理结果，应添加当前对应的aid到aid.xm by wwc 20230630
                    if (clssTransParam.getPreProcResult() == null) {
                        // Abort with CLSS_NO_APP
                        return new EmvOutCome(EmvErrorCode.CLSS_NO_APP, ETransStep.CLSS_ENTRY_GET_PRE_PROC_RES);
                    }

                    // Set input Param
                    clssTransParam.setnFinalSelectFCIdataLen(aucFCIData.length);
                    clssTransParam.setAucFinalSelectFCIdata(aucFCIData);
                    clssTransParam.setTransParam(curTransParam);
                    clssTransParam.setKernType(ucKernType[0]);
                    if (emvTransData != null) {
                        clssTransParam.setClssForceOnlinePin(emvTransData.isClssForceOnlinePin());
                        clssTransParam.setbSupSimpleProc(emvTransData.isbSupSimpleProc());
                    }

                    AppLog.d(TAG, "emvProcess update ClssTransParam: " + clssTransParam.toString());

                    // End with CLSS_OK
                    return new EmvOutCome(EmvErrorCode.CLSS_OK);
                } else { // Failed to final select
                    if ( (emvRest == EmvErrorCode.EMV_RSP_ERR) || (emvRest == EmvErrorCode.EMV_APP_BLOCK)
                            || (emvRest == EmvErrorCode.CLSS_RESELECT_APP) || (emvRest == EmvErrorCode.ICC_BLOCK)) {
                        int errorCode = entryL2.getErrorCode();
                        AppLog.d(TAG, "finalSelect Again  getErrorCode: " + errorCode);
                        emvRest = entryL2.delCandListCurApp();
                        AppLog.d(TAG, "finalSelect Again  delCandListCurApp : " + emvRest);
                        if (emvRest == EmvErrorCode.CLSS_OK) {
                            continue;
                        }
                    } // End of Failed branch
                    return new EmvOutCome(emvRest, ETransStatus.TRY_ANOTHER_INTERFACE, ETransStep.CLSS_ENTRY_FINAL_SELECT);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new EmvOutCome(EmvErrorCode.CLSS_PARAM_ERR);
    }

    /**
     * assets 文件保存
     */
    private TlvList handleKernalData() {
        // 获取默认参数
        AppLog.d(TAG,"handleKernalData begin");
        TlvList list = EmvAidParam.getTlvMap(curAidParam.getAid());
        if (list == null) {
            list = new TlvList();
        }
        //Amount, Authorised (Numeric)
        long amout = emvTransData.getAmount();
        long amoutOther = emvTransData.getAmountOther();

        list.addTlv("9F02",String.format("%012d", amout));
        //add other amount wwc
        list.addTlv("9F03",String.format("%012d", amoutOther));
        //Transaction Type
        list.addTlv("9C",String.format("%02x", emvTransData.getTransType()));
        //Transaction Sequence Counter
        list.addTlv("9F41",String.format("%08d",emvTransData.getTransNo()));
        //Transaction Date
        String date = emvTransData.getAucTransDate();
        list.addTlv("9A",date);
        //Transaction Time
        String time = emvTransData.getAucTransTime();
        list.addTlv("9F21",time);
        //rupay TLV
        //Max Fill Volume
        list.addTlv("DF16","02");

        list.addTlv("DF3A","0040000000");

        list.addTlv("9F39","07");
        list.addTlv("9F1A","0566");
        list.addTlv("5F2A","0566");
        AppLog.d(TAG,"handleKernalData end");
        AppLog.d(TAG,"list "+list.toString());

        return list;
    }
}
