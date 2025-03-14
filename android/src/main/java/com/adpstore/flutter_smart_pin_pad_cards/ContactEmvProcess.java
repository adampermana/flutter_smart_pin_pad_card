package com.adpstore.flutter_smart_pin_pad_cards;

import android.os.RemoteException;
import android.text.TextUtils;

import com.adpstore.flutter_smart_pin_pad_cards.entity.EmvAidParam;
import com.adpstore.flutter_smart_pin_pad_cards.entity.EmvErrorCode;
import com.adpstore.flutter_smart_pin_pad_cards.entity.EmvOnlineResp;
import com.adpstore.flutter_smart_pin_pad_cards.entity.EmvOutCome;
import com.adpstore.flutter_smart_pin_pad_cards.enums.EKernelType;
import com.adpstore.flutter_smart_pin_pad_cards.enums.EOnlineResult;
import com.adpstore.flutter_smart_pin_pad_cards.enums.ETransStatus;
import com.adpstore.flutter_smart_pin_pad_cards.enums.ETransStep;
import com.adpstore.flutter_smart_pin_pad_cards.impl.EContactCallback;
import com.adpstore.flutter_smart_pin_pad_cards.utils.DataUtils;
import com.topwise.cloudpos.aidl.emv.level2.AidlEmvL2;
import com.topwise.cloudpos.aidl.emv.level2.Combination;
import com.topwise.cloudpos.aidl.emv.level2.EmvCandidateItem;
import com.topwise.cloudpos.aidl.emv.level2.EmvCapk;
import com.topwise.cloudpos.aidl.emv.level2.EmvTerminalInfo;
import com.topwise.cloudpos.aidl.pinpad.AidlPinpad;
import com.topwise.cloudpos.struct.BytesUtil;
import com.topwise.toptool.api.convert.IConvert;
import com.topwise.toptool.api.packer.ITlv;
import com.topwise.toptool.api.packer.TlvException;
import com.topwise.toptool.impl.TopTool;

import java.util.List;

/**
 * Creation date: 2021/6/10 on 16:36
 * describe:
 * Author: Adam Permana
 */
public class ContactEmvProcess extends ABaseTransProcess {
    private static final String TAG = "ContactEmvProcess";
    private ITransProcessListener emvProcessListener;
    private AidlPinpad pinPad = DeviceServiceManagers.getInstance().getPinpadManager(0);
    private AidlEmvL2 emvL2 = DeviceServiceManagers.getInstance().getEmvL2();

    public ContactEmvProcess() {
        byte [] aucVersion = new byte[64];
        try {
            // Get kernel version number
            if (EmvErrorCode.EMV_OK == emvL2.EMV_GetVersion(aucVersion,64)) {
                AppLog.d(TAG, "EMV Lib Version: " + new String(aucVersion).trim());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void setEmvProcessListener(ITransProcessListener emvProcessListener) {
        this.emvProcessListener = emvProcessListener;
    }

    /**
     * Start EMV process
     * @return
     */
    @Override
    public EmvOutCome StartTransProcess() {
        IConvert convert = TopTool.getInstance().getConvert();

        // Initialize kernel until building candidate app list
        EmvOutCome emvOutCome = init();
        AppLog.d(TAG,"StartTransProcess init " + emvOutCome.toString());
        if (emvOutCome.getnErrorCodeL2() != EmvErrorCode.EMV_OK) {
            return emvOutCome;
        }

        try {
            int emvRest = -1;
            while (true) {
                int candListCount = 0;
                int selectedAppIndex = 0;
                // Get candidate app list size
                candListCount = emvL2.EMV_AppGetCandListCount();
                if ((candListCount > 1) && (emvKernelConfig != null && EmvErrorCode.EMV_TRUE == emvKernelConfig.getbCardHolderConfirm())) {                    String strDisplayName[] = new String[candListCount];
                    // Get all candidate app display names
                    for (int i = 0; i < candListCount; i++) {
                        EmvCandidateItem emvCandidateItem = emvL2.EMV_AppGetCandListItem(i);
                        if (emvCandidateItem != null ) {
                            strDisplayName[i] = new String(emvCandidateItem.getAucDisplayName()).trim();
                            AppLog.d(TAG,"AucDisplayName= " + strDisplayName[i]);
                            AppLog.d(TAG,"getAucAID= " + convert.bcdToStr(emvCandidateItem.getAucAID()));
                        }
                    }
                    // Request cardholder to select an application from candidate list
                    int AidIndex = emvProcessListener.onReqAppAidSelect(strDisplayName);
                    AppLog.d(TAG,"requestAidSelect AidIndex= " + AidIndex);
                    if ((AidIndex < 0) || (AidIndex >= candListCount)) {
                        return new EmvOutCome(EmvErrorCode.EMV_TERMINATED, ETransStatus.END_APPLICATION, ETransStep.EMV_APP_SELECT_APP);
                    } else {
                        selectedAppIndex = AidIndex;
                    }
                } else {
                    selectedAppIndex = 0;
                }

                // Get selected candidate item
                EmvCandidateItem emvCandidateItem = emvL2.EMV_AppGetCandListItem(selectedAppIndex);
                if (emvCandidateItem == null) {
                    return new EmvOutCome(EmvErrorCode.EMV_TERMINATED, ETransStatus.END_APPLICATION, ETransStep.EMV_GET_CAND_LIST);
                }

                // Get AID of selected candidate item
                byte[] aucAID = emvCandidateItem.getAucAID();
                if (emvProcessListener != null) {
                    byte [] aucRID = new byte[5];
                    System.arraycopy(aucAID, 0, aucRID, 0, 5);
                    // Notice payment app about kernel type
                    emvProcessListener.onUpToAppKernelType(EKernelType.getKernelType(convert.bcdToStr(aucRID)));
                }

                // Notice payment app about selected candidate item
                AppLog.d(TAG,"onUpdateEmvCandidateItem = ");
                emvProcessListener.onUpToAppEmvCandidateItem(emvCandidateItem);

                // Final Select, the terminal issues the SELECT command
                AppLog.d(TAG, "emvProcess Start EMV_AppFinalSelect ");
                emvRest = emvL2.EMV_AppFinalSelect(emvCandidateItem);
                AppLog.d(TAG, "EMV_AppFinalSelect emvRet : " + emvRest);
                if (emvRest != EmvErrorCode.EMV_OK) {
                    if ((EmvErrorCode.EMV_APP_BLOCKED == emvRest)
                            || (EmvErrorCode.EMV_NO_APP == emvRest)
                            || (EmvErrorCode.EMV_INVALID_RESPONSE == emvRest)
                            || (EmvErrorCode.EMV_INVALID_TLV == emvRest)
                            || (EmvErrorCode.EMV_DATA_NOT_EXISTS == emvRest)) {
                        candListCount = emvL2.EMV_AppGetCandListCount();
                        AppLog.d(TAG, "emvProcess  EMV_AppGetCandListCount " + candListCount);
                        if (candListCount > 1) {
                            // Delete the data in the current candidate list
                            emvRest = emvL2.EMV_AppDelCandListItem(selectedAppIndex);
                            AppLog.e(TAG,"emvProcess EMV_AppFinalSelect EMV_AppDelCandListItem emvRest " + emvRest);
                            if (emvRest == EmvErrorCode.EMV_OK) {
                                AppLog.e(TAG,"emvProcess EMV_AppFinalSelect select next CandList ");
                                continue;
                            }
                        } else if ((EmvErrorCode.EMV_NO_APP == emvRest)
                                || (EmvErrorCode.EMV_INVALID_RESPONSE == emvRest)
                                || (EmvErrorCode.EMV_INVALID_TLV == emvRest)
                                || (EmvErrorCode.EMV_DATA_NOT_EXISTS == emvRest)) {
                            return new EmvOutCome(EmvErrorCode.EMV_FALLBACK,ETransStatus.TRY_AGAIN,ETransStep.EMV_APP_FINAL_SELECT);
                        }
                    } else {
                        return new EmvOutCome(EmvErrorCode.EMV_TERMINATED,ETransStatus.END_APPLICATION,ETransStep.EMV_APP_FINAL_SELECT);
                    }
                }

                // Get language
                byte[] aucLanguage = emvL2.EMV_GetTLVData(0x5F2D);
                if (aucLanguage != null) {
                    AppLog.d(TAG, "emvProcess check onUpdateKernelType " + convert.bcdToStr(aucLanguage));
                }

                // Set some transaction data. Transaction Type , Transaction Date , Transaction Time
                // Set parameters according to each AID param. Terminal floor limit, Trans currency code   ... ...
                emvRest = setTransDataFromAid();
                AppLog.d(TAG, "emvProcess setTransDataFromAid emvRest= " + emvRest);
                if (emvRest != EmvErrorCode.EMV_OK) {
                    AppLog.d(TAG,"emvProcess setTransDataFromAid is error " );
                    return new EmvOutCome(EmvErrorCode.EMV_PARAMETER_ERROR,ETransStatus.END_APPLICATION,ETransStep.EMV_APP_SET_AID_PARAMS);
                }

                // Callback after final Aid Select, We Can set the TLV parameters
                boolean finalAid = emvProcessListener.onReqFinalAidSelect();
                AppLog.d(TAG, "emvProcess ProcessListener finalAidSelect +finalAid " + finalAid);
                if (!finalAid) { // The default return is true, return false to exit the transaction
                    AppLog.d(TAG,"emvProcess finalAidSelect is error " );
                    return new EmvOutCome(EmvErrorCode.EMV_TERMINATED,ETransStatus.END_APPLICATION,ETransStep.EMV_APP_FINAL_SELECT);
                }

                // GPO: Initiate Application Processing
                // The terminal issues the GET PROCESSING OPTIONS command
                emvRest = emvL2.EMV_GPOProc();
                AppLog.d(TAG,"emvProcess EMV_GPOProc nRet: " + emvRest);
                if (emvRest != EmvErrorCode.EMV_OK) {
                    int lastSW = emvL2.EMV_GetLastStatusWord();
                    AppLog.d(TAG,"emvProcess EMV_GPOProc GetLastStatusWord lastSW " +lastSW );
                    if (lastSW != 0x9000) {
                        candListCount = emvL2.EMV_AppGetCandListCount();
                        AppLog.d(TAG,"emvProcess EMV_GPOProc GetCandListCount  " +candListCount );
                        if (candListCount > 1) {
                            emvRest = emvL2.EMV_AppDelCandListItem(selectedAppIndex);
                            AppLog.e(TAG,"emvProcess EMV_GPOProc EMV_AppDelCandListItem emvRest "+emvRest);
                            if (emvRest == EmvErrorCode.EMV_OK){
                                AppLog.e(TAG,"emvProcess EMV_GPOProc select next CandList ");
                                continue;
                            }
                        }
                    }
                    return new EmvOutCome(emvRest, ETransStatus.END_APPLICATION, ETransStep.EMV_GPO);
                }
                break;
            }

            // Read Application Data
            // The terminal shall read the files and records indicated in the AFL using the
            // READ RECORD command identifying the file by its SFI.
            emvRest = emvL2.EMV_ReadRecordData();
            AppLog.d(TAG, "emvProcess ReadRecordData emvRest: " + emvRest);
            if (emvRest != EmvErrorCode.EMV_OK) {
                return new EmvOutCome(emvRest, ETransStatus.END_APPLICATION, ETransStep.EMV_READ_RECORD_DATA);
            }

            // Get card number
            String cardNo = getCardNo();
            AppLog.i(TAG, "emvProcess cardNo " + cardNo);
            if (!DataUtils.isNullString(cardNo)) {
                AppLog.d(TAG, "emvProcess check onConfirmCardInfo ");
                // Wait for the cardholder to confirm the card number
                boolean confirmCard = emvProcessListener.onConfirmCardInfo(cardNo);
                AppLog.d(TAG,"emvProcess onConfirmCardInfo = " + confirmCard);
                if (!confirmCard) {
                    return new EmvOutCome(EmvErrorCode.EMV_CANCEL, ETransStatus.END_APPLICATION,ETransStep.EMV_CONFIRM_CARD_PAN);
                }
                //                return EmvResult.IC_GET_PAN_ERR;
            }

            // If it's a simple process, quit with success status.
            AppLog.d(TAG, "emvProcess check Simple ");
            if (emvTransData.isbSupSimpleProc()) {
                return new EmvOutCome(EmvErrorCode.EMV_APPROVED, ETransStatus.ONLINE_REQUEST, ETransStep.EMV_ONLY_READ_CARD);
            }

            // The terminal uses the RID and index to retrieve the terminal-stored CAPK
            emvRest = AppRetrieveCAPK();
            AppLog.d(TAG, "emvProcess AppRetrieveCAPK emvRest:" + emvRest);

            // Offline Data Authentication
            emvRest = emvL2.EMV_OfflineDataAuth();
            AppLog.d(TAG, "emvProcess  EMV_OfflineDataAuth emvRet : " + emvRest);
            if ((emvRest == EmvErrorCode.EMV_ICC_ERROR) || (emvRest == EmvErrorCode.EMV_TERMINATED)){
                return new EmvOutCome(emvRest, ETransStatus.END_APPLICATION,ETransStep.EMV_OFFLINE_DATA_AUTH);
            }

            // Terminal Risk Management
            emvRest = emvL2.EMV_TerminalRiskManagement();
            AppLog.d(TAG, "emvProcess EMV_TerminalRiskManagement emvRet : " + emvRest);
            if (emvRest != EmvErrorCode.EMV_OK) {
                AppLog.d(TAG, "emvProcess check EMV_TerminalRiskManagement  ");
                return new EmvOutCome(emvRest, ETransStatus.END_APPLICATION, ETransStep.EMV_TER_RISK_MANAGEMENT);
            }

            // Processing Restrictions
            emvRest = emvL2.EMV_ProcessingRestrictions();
            AppLog.d(TAG, "emvProcess EMV_ProcessingRestrictions emvRet : " + emvRest);
            if (emvRest != EmvErrorCode.EMV_OK) {
                return new EmvOutCome(emvRest, ETransStatus.END_APPLICATION, ETransStep.EMV_PROC_RESTRICTIONS);
            }

            // Card holder verify
            emvRest = emvL2.EMV_CardHolderVerify();
            AppLog.d(TAG, "emvProcess EMV_CardHolderVerify emvRet : " + emvRest);
            if (emvRest != EmvErrorCode.EMV_OK){
                return new EmvOutCome(emvRest, ETransStatus.END_APPLICATION, ETransStep.EMV_CARD_HOLDER_VERIFY);
            }

            // Terminal Action Analysis
            emvRest = emvL2.EMV_TermActionAnalyze();
            AppLog.d(TAG, "emvProcess EMV_TermActionAnalyze emvRet : " + emvRest);
            if (emvRest != EmvErrorCode.EMV_ONLINE_REQUEST) {
                return new EmvOutCome(emvRest, ETransStatus.END_APPLICATION, ETransStep.EMV_TER_ACTION_ANALYZE);
            }

            int onlineResult = EmvErrorCode.EMV_ONLINE_CONNECT_FAILED;
            byte[] authCode = null; // 89 Authorisation Code
            byte[] authRespCode = new byte[2]; // 8A Authorisation Response Code
            byte[] issueAuthData = null; // 91 Issuer Authentication Data
            byte[] issueScript71TLV = null; // 71 Issuer Script
            byte[] issueScript72TLV = null; // 72 Issuer Script
            ITlv tlv = TopTool.getInstance().getPacker().getTlv();

            // Check online
            emvRest = emvL2.EMV_OnlineTransEx();
            AppLog.d(TAG, "emvProcess EMV_OnlineTransEx emvRet : " + emvRest);
            if (emvRest == EmvErrorCode.EMV_OK) {
                // Request payment app for online processing

                EmvOnlineResp emvOnlineResp = emvProcessListener.onReqOnlineProc();
                AppLog.d(TAG, "emvProcess ProcessListener onRequestOnline emvEntity : " + emvOnlineResp.toString());

                // NosetAuthRespCode need second GAC, check authRespCode and abort here
                if (!emvTransData.isbSup2GAC()) { // add by wwc 2022 01 20 增加是否试下2GAC判断
                    String authRespCodes = BytesUtil.bytes2HexString(emvOnlineResp.getAuthRespCode());
                    if ("3030".equals(authRespCodes)) {
                        return new EmvOutCome(EmvErrorCode.EMV_APPROVED, ETransStatus.ONLINE_APPROVE, ETransStep.EMV_APP_CON_2GAC);
                    } else {
                        return new EmvOutCome(EmvErrorCode.EMV_DECLINED, ETransStatus.ONLINE_DECLINED,ETransStep.EMV_APP_CON_2GAC);
                    }
                }

                // Check ic card is existed or not
                boolean exist = DeviceServiceManagers.getInstance().getICCardReader().isExist();
                AppLog.d(TAG, "emvProcess  Check the IC card is in the slot isExist : " + exist);
                if (!exist) { // Card not existed, abort with DECLINED status here
                    // 先检卡判断下
                    if (EOnlineResult.ONLINE_APPROVE == emvOnlineResp.geteOnlineResult()) {
                        return new EmvOutCome(EmvErrorCode.EMV_DECLINED, ETransStatus.ONLINE_DECLINED, ETransStep.EMV_APP_CON_2GAC);
                    } else {
                        return new EmvOutCome(EmvErrorCode.EMV_DECLINED, ETransStatus.ONLINE_DECLINED, ETransStep.EMV_APP_CON_2GAC);
                    }
                }

                // Get online response data from issue bank
                EOnlineResult eOnlineResult = emvOnlineResp.geteOnlineResult();
                AppLog.d(TAG, "emvProcess check eOnlineResult " + eOnlineResult.toString());
                switch (eOnlineResult) {
                    case ONLINE_APPROVE:
                        onlineResult = EmvErrorCode.EMV_ONLINE_APPROVED;
                        break;
                    case ONLINE_DENIAL:
                    case ONLINE_FAILED:
                        onlineResult = EmvErrorCode.EMV_ONLINE_REJECT;
                        break;
                    case ONLINE_REFER:
                        onlineResult = EmvErrorCode.EMV_ONLINE_VOICE_PREFER;
                        break;
                    default:
                        onlineResult = EmvErrorCode.EMV_ONLINE_ERROR;
                        break;
                }
                AppLog.d(TAG, "emvProcess onlineResult : " + onlineResult);

                // Get authRespCode
                if (emvOnlineResp != null && emvOnlineResp.isExistAuthRespCode()) {
                    authRespCode = emvOnlineResp.getAuthRespCode();
                    AppLog.d(TAG, "emvProcess getAuthRespCode" + BytesUtil.bytes2HexString(authRespCode));
                }

                // Get issue auth data
                if(emvOnlineResp != null && emvOnlineResp.isExistIssAuthData()){
                    issueAuthData = emvOnlineResp.getIssueAuthData();
                    AppLog.d(TAG, "emvProcess getIssueAuthData" + BytesUtil.bytes2HexString(issueAuthData));
                }

                // Get auth code
                if(emvOnlineResp != null && emvOnlineResp.isExistAuthCode()){
                    authCode = emvOnlineResp.getAuthCode();
                    AppLog.d(TAG, "emvProcess authCode" + BytesUtil.bytes2HexString(authCode));
                }

                // Online data process
                emvRest = emvL2.EMV_ProcessOnlineRespData(onlineResult, issueAuthData, authRespCode, authCode);
                AppLog.d(TAG, "emvProcess EMV_ProcessOnlineRespData Result : " + emvRest);

                // Get 71 script
                if (emvOnlineResp != null && emvOnlineResp.isExistIssScr71()) {
                    ITlv.ITlvDataObj obj = tlv.createTlvDataObject();
                    obj.setTag(0x71);
                    obj.setValue(emvOnlineResp.getIssueScript71());
                    try {
                        issueScript71TLV = tlv.pack(obj);
                    } catch (TlvException e) {
                        e.printStackTrace();
                    }
                    AppLog.d(TAG, "emvProcess getIssueScript71" + BytesUtil.bytes2HexString(issueScript71TLV));
                }

                // Get 72 script
                if (emvOnlineResp != null && emvOnlineResp.isExistIssScr72()) {
                    ITlv.ITlvDataObj obj = tlv.createTlvDataObject();
                    obj.setTag(0x72);
                    obj.setValue(emvOnlineResp.getIssueScript72());
                    try {
                        issueScript72TLV = tlv.pack(obj);
                    } catch (TlvException e) {
                        e.printStackTrace();
                    }
                    AppLog.d(TAG, "emvProcess getIssueScript72" + BytesUtil.bytes2HexString(issueScript72TLV));
                }
            }

            AppLog.d(TAG, "emvProcess EMV_ProcessOnlineRespData/EMV_OnlineTransEx  emvRest: " + emvRest);
            AppLog.d(TAG, "emvProcess EMV_OnlineTransEx onlineResult : " + onlineResult);

            // Process 71 script
            if (emvRest != EmvErrorCode.EMV_TERMINATED && issueScript71TLV != null) {
                int i = emvL2.EMV_IssueToCardScript((byte) 1, issueScript71TLV);
                AppLog.d(TAG, "emvProcess EMV_IssueToCardScript i : " + i);
            }
            AppLog.d(TAG, "emvProcess check emvRest " + emvRest);

            // Call EMV_Completion method with different result
            if (emvRest == EmvErrorCode.EMV_OK) {
                if (onlineResult == EmvErrorCode.EMV_ONLINE_APPROVED) {
                    emvRest = emvL2.EMV_Completion((byte) 1);
                    AppLog.d(TAG, "emvProcess EMV_Completion EMV_ONLINE_APPROVED emvRet : " + emvRest);
                } else if (onlineResult == EmvErrorCode.EMV_ONLINE_VOICE_PREFER) {
                    emvRest = emvL2.EMV_Completion((byte) 1);
                    AppLog.d(TAG, "emvProcess EMV_Completion EMV_ONLINE_VOICE_PREFER emvRet : " + emvRest);
                } else {
                    AppLog.d(TAG, "emvProcess EMV_Completion 0  ");
                    emvRest = emvL2.EMV_Completion((byte) 0);
                    AppLog.d(TAG, "emvProcess EMV_Completion else emvRet : " + emvRest);
                }
            } else if (emvRest == EmvErrorCode.EMV_DECLINED) {
                emvRest = emvL2.EMV_Completion((byte) 0);
                AppLog.d(TAG, "emvProcess EMV_Completion EMV_DECLINED emvRet : " + emvRest);
            } else if (emvRest == EmvErrorCode.EMV_APPROVED) {
                emvRest = emvL2.EMV_Completion((byte) 1);
                AppLog.d(TAG, "emvProcess EMV_Completion EMV_APPROVED emvRet : " + emvRest);
            }

            // Process 72 script
            if (emvRest != EmvErrorCode.EMV_TERMINATED && issueScript72TLV != null) {
                AppLog.d(TAG, "emvProcess check EMV_IssueToCardScript  ");
                int i = emvL2.EMV_IssueToCardScript((byte) 0, issueScript72TLV);
                AppLog.d(TAG, "emvProcess EMV_Completion EMV_TERMINATED i : " + i);
            }
            AppLog.d(TAG, "emvProcess check EMV_GetScriptResult  " + emvRest);

            // Get script result
            byte[] scriptResult = emvL2.EMV_GetScriptResult();
            if (scriptResult != null && scriptResult.length > 0) {
                AppLog.d(TAG,"emvProcess check EMV_GetScriptResult 9F5B " + BytesUtil.bytes2HexString(scriptResult));
                emvL2.EMV_SetTLVData(0x9F5B, scriptResult);
            }

            AppLog.d(TAG, "emvProcess trans cpmplete emvRest:" + emvRest);
            AppLog.d(TAG, "emvProcess trans cpmplete onlineResult:" + onlineResult);

            // Finish EMV process here
            switch (emvRest) {
                case EmvErrorCode.EMV_APPROVED:
                case EmvErrorCode.EMV_FORCE_APPROVED:
                    if (onlineResult == EmvErrorCode.EMV_ONLINE_APPROVED) {
                        return new EmvOutCome(emvRest, ETransStatus.ONLINE_APPROVE,ETransStep.EMV_TRANS_COMPLETE);
                    } else {
                        return new EmvOutCome(emvRest, ETransStatus.OFFLINE_APPROVE,ETransStep.EMV_TRANS_COMPLETE);
                    }
                case EmvErrorCode.EMV_DECLINED:
                    if (onlineResult == EmvErrorCode.EMV_ONLINE_CONNECT_FAILED) {
                        return new EmvOutCome(emvRest, ETransStatus.OFFLINE_DECLINED,ETransStep.EMV_TRANS_COMPLETE);
                    } else {
                        return new EmvOutCome(emvRest, ETransStatus.ONLINE_DECLINED,ETransStep.EMV_TRANS_COMPLETE);
                    }
                default:
                    return new EmvOutCome(emvRest, ETransStatus.END_APPLICATION,ETransStep.EMV_TRANS_COMPLETE);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            AppLog.d(TAG,"emvProcess Exception = " + e.getMessage());
        }
        return new EmvOutCome(EmvErrorCode.EMV_TERMINATED, ETransStatus.NA,ETransStep.EMV_TRANS_COMPLETE);
    }

    @Override
    public byte[] getTLV(int paramInt) {
        try {
            AppLog.e(TAG,"getTlv TAG= " + paramInt );
            return emvL2.EMV_GetTLVData(paramInt);
        } catch (RemoteException e) {
            e.printStackTrace();
            AppLog.e(TAG,"getTlv Exception TAG= " + paramInt + " Exception= " + e.getMessage());
        }
        return new byte[0];
    }

    @Override
    public boolean setTLV(int paramInt, byte[] paramArrayOfbyte) {
        try {
            int nRet = emvL2.EMV_SetTLVData(paramInt, paramArrayOfbyte);
            AppLog.e(TAG,"EMV_SetTLVData nRet= " + nRet);
            if (nRet == EmvErrorCode.EMV_OK) {
                return true;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            AppLog.e(TAG,"EMV_SetTLVData Exception TAG= " + paramInt + " Exception= " + e.getMessage());
        }
        return false;
    }

    @Override
    public void getDebugInfo() {
        byte aucAssistInfo[] = new byte[4096];
        int nErrcode[] = new int[1];
        try {
            int nRet = emvL2.EMV_GetDebugInfo(aucAssistInfo.length, aucAssistInfo, nErrcode);
            AppLog.e(TAG, "getDebugInfo nRet: " + nRet);
            AppLog.e(TAG, "getDebugInfo nErrcode: " + nErrcode[0]);
            AppLog.e(TAG, "getDebugInfo aucAssistInfo: " + new String(aucAssistInfo).trim());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Initialize EMV kernel
     *
     * from init to building candidate app list
     *
     * @return
     */
    private EmvOutCome init() {
        AppLog.d(TAG,"init");
        int emvRest = -1;
        try {
            // Initialize kernel
            emvRest = emvL2.EMV_Initialize();
            AppLog.d(TAG,"init EMV_Initialize emvRest = " + emvRest);
            if (emvRest != EmvErrorCode.EMV_OK) {
                return new EmvOutCome(emvRest, ETransStep.EMV_INIT);
            }

            // Set kernel type
            emvRest = emvL2.EMV_SetKernelType((byte) 0xFF);
            AppLog.d(TAG,"init EMV_SetKernelType emvRest = " + emvRest);

            // Set kernel callback
            emvRest = emvL2.EMV_SetCallback(new EContactCallback(emvProcessListener));
            if (emvRest != EmvErrorCode.EMV_OK) {
                AppLog.d(TAG,"init SetCallback filed res = " + emvRest);
                return new EmvOutCome(emvRest, ETransStep.EMV_SET_CALLBACK);
            }

            // Delete all aid params in kernel
            emvL2.EMV_DelAllAIDList();
            AppLog.d(TAG,"init EMV_DelAllAIDList emvRest = " + emvRest);

            // Load combination list from AID params stored by payment app.
            List<Combination> combinations = emvProcessListener.onLoadCombinationParam();
            if (combinations != null && combinations.size() > 0){
                AppLog.d(TAG, "init AddAIDList size=: " + combinations.size());
                // Add AID params to kernel
                for (Combination aid : combinations) {
                    emvRest =  emvL2.EMV_AddAIDList(aid.getAucAID(), (byte) aid.getUcAidLen(), (byte) 1);
                    AppLog.d(TAG,"init EMV_AddAIDList emvRest = " + emvRest);
                    AppLog.d(TAG, "init AddAIDList===: " + TopTool.getInstance().getConvert().bcdToStr(aid.getAucAID()));
                }
            } else {
                return new EmvOutCome(EmvErrorCode.EMV_PARAMETER_ERROR, ETransStep.EMV_LOAD_CONBINATION_PARAM);
            }

            // Set EMV kenel config param to kernel
            if (emvKernelConfig != null) {
                emvRest = emvL2.EMV_SetKernelConfig(emvKernelConfig);
                AppLog.d(TAG,"init EMV_SetKernelConfig emvRest = " + emvRest);
                if (emvRest != EmvErrorCode.EMV_OK) {
                    return new EmvOutCome(emvRest, ETransStep.EMV_SET_KERNEL_CONFIG);
                }
            }

            // Set EMV terminal info param to kernel
            if (emvTerminalInfo != null) {
                emvRest = emvL2.EMV_SetTerminalInfo(emvTerminalInfo);
                AppLog.d(TAG,"init EMV_SetTerminalInfo emvRest = " + emvRest);
                if (emvRest != EmvErrorCode.EMV_OK) {
                    return new EmvOutCome(emvRest, ETransStep.EMV_SET_TERMINAL_INFO);
                }
            }

            // Set whether support PBOC or not
            emvRest = emvL2.EMV_SetSupport_PBOC((byte)0, (byte)0, 0);
            AppLog.d(TAG,"init EMV_SetSupport_PBOC emvRest = " + emvRest);
            if (emvRest != EmvErrorCode.EMV_OK) {
                return new EmvOutCome(emvRest, ETransStep.EMV_SET_SUP_PBOC);
            }

            // Building the Candidate List
            // Create a list of ICC applications that are supported by the terminal.
            AppLog.d(TAG,"init EMV_AppCandidateBuild Start= ");
            emvRest = emvL2.EMV_AppCandidateBuild((byte) 0);
            AppLog.d(TAG,"init AppCandidateBuild  emvRest = " + emvRest);
            if (emvRest != EmvErrorCode.EMV_OK) { // visa卡 ADVT case 8 fallback 返回码是10
                if (EmvErrorCode.EMV_NO_APP == emvRest) {
                    return new EmvOutCome(EmvErrorCode.EMV_FALLBACK, ETransStep.EMV_CANDI_DATA_BUILD);
                } else {
                    return new EmvOutCome(emvRest, ETransStep.EMV_CANDI_DATA_BUILD);
                }
            }
            return new EmvOutCome(EmvErrorCode.EMV_OK);
        } catch (RemoteException e) {
            e.printStackTrace();
            AppLog.d(TAG,"init Exception = " + e.getMessage());
        }
        return new EmvOutCome(emvRest);
    }

    /**
     * The terminal uses the RID and index to retrieve the terminal-stored CAPK
     * @return
     */
    private int AppRetrieveCAPK() {
        int emvRet = 0;
        AppLog.d(TAG, "emvProcess retrieveCAPK()================");
        try {
            // Delete all public key and revoked public key
            emvL2.EMV_DelAllCAPK();
            emvL2.EMV_DelAllRevoIPK();

            // Get current AID
            byte[] aid = emvL2.EMV_GetTLVData(0x9F06);
            if ((aid == null) || (aid.length < 5) || (aid.length > 16)) {
                AppLog.d(TAG, "emvProcess retrieveCAPK Get aid(9F06) failed!");
                return EmvErrorCode.EMV_PARAMETER_ERROR;
            }
            AppLog.d(TAG, "emvProcess retrieveCAPK aid(9F06): " + BytesUtil.bytes2HexString(aid));

            // Get public key index
            byte[] index = emvL2.EMV_GetTLVData(0x8F);
            if ((index == null) || (index.length != 1)) {
                AppLog.d(TAG, "emvProcess retrieveCAPK Get CAPK index(8F) failed!");
                return EmvErrorCode.EMV_PARAMETER_ERROR;
            }
            AppLog.d(TAG, "emvProcess retrieveCAPK CAPK index(8F): " + BytesUtil.bytes2HexString(index));

            // Get RID from first 5 bytes of AID
            byte[] rid = new byte[5];
            System.arraycopy(aid, 0, rid, 0, 5);

            // Search for public key specific by RID and index
            EmvCapk emvCapk = emvProcessListener.onFindIssCapkParamProc(BytesUtil.bytes2HexString(rid), (byte) (index[0] & 0xFF));
            if (null == emvCapk) {
                AppLog.d(TAG, "emvProcess retrieveCAPK findByRidIndex failed!");
                return EmvErrorCode.EMV_PARAMETER_ERROR;
            }
            AppLog.d(TAG, "retrieveCAPK onFindIssuerCapkParam EmvCapk: " + emvCapk.toString());

            // Add public key param to kernel
            emvRet = emvL2.EMV_AddCAPK(emvCapk);
            AppLog.d(TAG, "emvProcess retrieveCAPK EMV_AddCAPK emvRet : " + emvRet);
            if (emvRet != EmvErrorCode.EMV_OK) {
                return EmvErrorCode.EMV_PARAMETER_ERROR;
            }
            return  EmvErrorCode.EMV_OK;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return  EmvErrorCode.EMV_PARAMETER_ERROR;
    }

    /**
     * Get current card number
     * @return
     */
    private String getCardNo() {
        AppLog.d(TAG, "Into getCardNo()");
        try {
            byte[] PAN = emvL2.EMV_GetTLVData(0x5A);
            if (PAN == null) {
                AppLog.emvd(TAG, "emvProcess Get AID(5A) failed!");
                return null;
            }
            String cardNo = BytesUtil.bytes2HexString(PAN);
            if (cardNo == null) {
                AppLog.d(TAG, "emvProcess CardNo is null");
                return null;
            }
            cardNo = cardNo.toUpperCase().replace("F", "");
            AppLog.d(TAG, "emvProcess getCardNo(): " + cardNo);
            return cardNo;
        } catch (RemoteException e) {
            e.printStackTrace();
            AppLog.d(TAG,"emvProcess getCardNo Exception = " + e.getMessage());
            return null;
        }
    }

    /**
     * 设置内核的TAG和终端信息参数，从匹配到的AID参数或交易数据读取相应参数值，再设置到EMV内核
     *
     * @return
     */
    private int setTransDataFromAid() {
        AppLog.d(TAG,"emvProcess setTransDataFromAid Start = ");
        try {
            long amount = emvTransData.getAmount();
            long amountOther = emvTransData.getAmountOther();

            // Set amount and other amount
            String s9F02 = String.format("%012d", amount);
            String s9F03 = String.format("%012d", amountOther);
            String s81 = String.format("%08X", amount);
            String s9F04 = String.format("%08X", amountOther);

            AppLog.d(TAG, "amount TAG 9F02:========= " + s9F02);
            AppLog.d(TAG, "amountOther TAG 9F03:========= " + s9F03);
            AppLog.d(TAG, "amount TAG 81: =========" + s81);
            AppLog.d(TAG, "amountOther TAG 9F04:========= " + s9F04);

            emvL2.EMV_SetTLVData(0x9F02, BytesUtil.hexString2Bytes(s9F02));
            emvL2.EMV_SetTLVData(0x9F03, BytesUtil.hexString2Bytes(s9F03));
            emvL2.EMV_SetTLVData(0x81, BytesUtil.hexString2Bytes(s81));
            emvL2.EMV_SetTLVData(0x9F04, BytesUtil.hexString2Bytes(s9F04));

            // Set Transaction Type
            byte[] transType = new byte[1];
            transType[0] = emvTransData.getTransType();
            emvL2.EMV_SetTLVData(0x9C, transType);

            // The getRandom function returns a fixed 8 byte random number
            String temp = emvTransData.getAucUnNumber();
            if (!TextUtils.isEmpty(temp)){ // 随机数由应用层先获取
                byte[] unpredictableNum = TopTool.getInstance().getConvert().strToBcd(temp, IConvert.EPaddingPosition.PADDING_RIGHT);
                if (unpredictableNum != null && unpredictableNum.length > 0) {
                    emvL2.EMV_SetTLVData(0x9F37, unpredictableNum);
                }
            } else {
                AppLog.d(TAG,"emvProcess setTransData regetRandom");
                byte[] random = pinPad.getRandom();
                byte[] unpredictableNum = new byte[4];
                System.arraycopy(random, 0, unpredictableNum, 0, 4);
                emvL2.EMV_SetTLVData(0x9F37, unpredictableNum);
            }

            // Transaction Sequence Counter
            String tag9f41 = String.format("%08d",emvTransData.getTransNo());
            AppLog.emvd(TAG, "setTransData TAG9F41== " + tag9f41);
            emvL2.EMV_SetTLVData(0x9F41, BytesUtil.hexString2Bytes(tag9f41));
            //emvL2.EMV_SetTLVData(0x9F41, BytesUtil.int2Bytes(tsc, true));

            // Transaction Date YYMMDD
            String date = emvTransData.getAucTransDate();
            emvL2.EMV_SetTLVData(0x9A, BytesUtil.hexString2Bytes(date));

            // Transaction Time HHMMSS
            String time = emvTransData.getAucTransTime();
            emvL2.EMV_SetTLVData(0x9F21, BytesUtil.hexString2Bytes(time));

            // Check AID and get AID
            byte[] aid = emvL2.EMV_GetTLVData(0x9F06);
            if ((aid == null) || (aid.length < 5) || (aid.length > 16)) {
                AppLog.e(TAG,"emvProcess getaid(9F06)  failed! ");
                return -1;
            }
            AppLog.d(TAG, "Get AID(9F06)" + BytesUtil.bytes2HexString(aid));
            String strAid = BytesUtil.bytes2HexString(aid);

            // Find specific AID param
            EmvAidParam eaidParam = emvProcessListener.onFindCurAidParamProc(strAid);
            if (eaidParam == null) {
                AppLog.e(TAG,"emvProcess emvAidParams  is null!");
                return EmvErrorCode.EMV_PARAMETER_ERROR;
            }
            AppLog.e(TAG,"emvProcess onFindCurrentAidParam emvAidParams : " + eaidParam.toString());

            IConvert convert = TopTool.getInstance().getConvert();

            // Get the terminal info param from kernel firstly.
            EmvTerminalInfo emvTerminalInfo = emvL2.EMV_GetTerminalInfo();
            AppLog.d(TAG, "emvProcess  default emvTerminalInfo = " + emvTerminalInfo.toString());

            // Set terminal info field with current aid param
            if (eaidParam.isbFloorLimitFlg()) {
                AppLog.d(TAG, "emvProcess set aidParams getFloorLimit()(int) = " + eaidParam.getAucFloorLimit());
                emvL2.EMV_SetTLVData(0x9F1B, convert.strToBcd(eaidParam.getAucFloorLimit(), IConvert.EPaddingPosition.PADDING_RIGHT));
                emvTerminalInfo.setUnTerminalFloorLimit((int)convert.strToLong(eaidParam.getAucFloorLimit(), IConvert.EPaddingPosition.PADDING_RIGHT));
            }

            if (eaidParam.isbThresholdFlg()) {
                AppLog.d(TAG, "emvProcess set threshold = " + eaidParam.getAucThreshold());
                emvTerminalInfo.setUnThresholdValue(Integer.parseInt(eaidParam.getAucThreshold()));
            }

            if (eaidParam.isbTermIDFlg()) {
                AppLog.d(TAG, "emvProcess set TermId = " + eaidParam.getAucTermID());
                emvTerminalInfo.setAucTerminalID(eaidParam.getAucTermID());
            }

            if (eaidParam.isbMerchIDFlg()) {
                AppLog.d(TAG, "emvProcess set merchId = " + eaidParam.getAucMerchID());
                emvTerminalInfo.setAucMerchantID(eaidParam.getAucTermID());
            }

            if (eaidParam.isbMerchCateCodeFlg()) {
                AppLog.d(TAG, "emvProcess set merchCateCode = " + eaidParam.getAucMerchCateCode());
                emvTerminalInfo.setAucMerchantCategoryCode(convert.strToBcd(eaidParam.getAucMerchCateCode(), IConvert.EPaddingPosition.PADDING_RIGHT));
            }

            if (eaidParam.isbMerchNameLocFlg()) {
                AppLog.d(TAG, "emvProcess set AucMerchantNameLocation = " + eaidParam.getAucMerchNameLoc());
                emvTerminalInfo.setAucMerchantNameLocation(convert.strToBcd(eaidParam.getAucMerchNameLoc(), IConvert.EPaddingPosition.PADDING_RIGHT));
            }

            if (eaidParam.isbCurrencyCodeFlg()) {
                AppLog.d(TAG, "emvProcess set TransCurrencyCode = " + eaidParam.getAucCurrencyCode());
                emvTerminalInfo.setAucTransCurrencyCode(convert.strToBcd(eaidParam.getAucCurrencyCode(), IConvert.EPaddingPosition.PADDING_RIGHT));
            }

            if (eaidParam.isbCurrencyExpFlg()) {
                AppLog.d(TAG, "emvProcess set getTransCurrExp = " + eaidParam.getUcCurrencyExp());
                emvTerminalInfo.setUcTransCurrencyExp(eaidParam.getUcCurrencyExp());
            }

            if (eaidParam.isbRefCurrencyCodeExt()) {
                AppLog.d(TAG, "emvProcess set referCurrCode = " + eaidParam.getAucRefCurrencyCode());
                emvTerminalInfo.setAucTransRefCurrencyCode(convert.strToBcd(eaidParam.getAucRefCurrencyCode(), IConvert.EPaddingPosition.PADDING_RIGHT));
            }

            if (eaidParam.isbRefCurrencyExpExt()) {
                AppLog.d(TAG, "emvProcess set RefCurrencyExp = " + eaidParam.getUcRefCurrencyExp());
                emvTerminalInfo.setUcTransRefCurrencyExp(eaidParam.getUcRefCurrencyExp());
            }

            if (eaidParam.isbAcquireIDFlg()) {
                AppLog.d(TAG, "emvProcess set TerminalAcquireID = " + eaidParam.getAucAcquireID());
                emvTerminalInfo.setAucTerminalAcquireID(eaidParam.getAucAcquireID());
            }

            if (eaidParam.isbAPPVerFlg()) {
                AppLog.d(TAG, "emvProcess set AucAPPVer = " + eaidParam.getAucAPPVer());
                emvTerminalInfo.setAucAppVersion(convert.strToBcd(eaidParam.getAucAPPVer(), IConvert.EPaddingPosition.PADDING_RIGHT));
            }

            if (eaidParam.isbTermDDOLFlg()) {
                AppLog.d(TAG, "emvProcess set AucTermDDOL = " + eaidParam.getAucTermDDOL());
                emvTerminalInfo.setAucDefaultDDOL(convert.strToBcd(eaidParam.getAucTermDDOL(), IConvert.EPaddingPosition.PADDING_RIGHT));
            }

            if (eaidParam.isbTermTDOLFlg()) {
                AppLog.d(TAG, "emvProcess set AucTermTDOL = " + eaidParam.getAucTermTDOL());
                emvTerminalInfo.setAucDefaultTDOL(convert.strToBcd(eaidParam.getAucTermTDOL(), IConvert.EPaddingPosition.PADDING_RIGHT));
            }

            if (eaidParam.isbTACDenailFlg()) {
                AppLog.d(TAG, "emvProcess set TACDenial = " + eaidParam.getAucTACDenail());
                emvTerminalInfo.setAucTACDenial(convert.strToBcd(eaidParam.getAucTACDenail(), IConvert.EPaddingPosition.PADDING_RIGHT));
            }

            if (eaidParam.isbTACOnlineFlg()) {
                AppLog.d(TAG, "emvProcess set TACOnline = " + eaidParam.getAucTACOnline());
                emvTerminalInfo.setAucTACOnline(convert.strToBcd(eaidParam.getAucTACOnline(), IConvert.EPaddingPosition.PADDING_RIGHT));
            }

            if (eaidParam.isbTACDefaultFlg()) {
                AppLog.d(TAG, "emvProcess set TACDefualt = " + eaidParam.getAucTACDefault());
                emvTerminalInfo.setAucTACDefault(convert.strToBcd(eaidParam.getAucTACDefault(), IConvert.EPaddingPosition.PADDING_RIGHT));
            }

            if (eaidParam.isbTPFlg()) {
                AppLog.d(TAG, "emvProcess set targetPer = " + eaidParam.getUcTP());
                emvTerminalInfo.setUcTargetPercentage(eaidParam.getUcTP());
            }

            if (eaidParam.isbMaxTPFlg()) {
                AppLog.d(TAG, "emvProcess set maxTargetPer = " + eaidParam.getUcMaxTP());
                emvTerminalInfo.setUcMaxTargetPercentage(eaidParam.getUcMaxTP());
            }

            // Set TAG 9F39
            emvL2.EMV_SetTLVData(0x9F39, convert.strToBcd("05", IConvert.EPaddingPosition.PADDING_RIGHT));
            AppLog.d(TAG, "emvProcess update  emvTerminalInfo== " + emvTerminalInfo.toString());

            // Set terminal info param to kernel
            emvL2.EMV_SetTerminalInfo(emvTerminalInfo);
            return EmvErrorCode.EMV_OK;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return EmvErrorCode.EMV_PARAMETER_ERROR;
    }
}