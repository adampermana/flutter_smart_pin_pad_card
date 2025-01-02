/*============================================================
 Module Name       : ETransStatus.java
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

package com.adpstore.flutter_smart_pin_pad_cards.enums;

public enum ETransStep {
    EMV_NA(""),
    EMV_INIT("EMV_Initialize"),
    EMV_SET_CALLBACK("EMV_SetCallback"),
    EMV_LOAD_CONBINATION_PARAM("RMV_LoadCombinationParam"),
    EMV_SET_KERNEL_CONFIG("EMV_SetKernelConfig"),
    EMV_SET_TERMINAL_INFO("EMV_SetTerminalInfo"),
    EMV_SET_SUP_PBOC("EMV_SetSupport_PBOC"),
    EMV_CANDI_DATA_BUILD("EMV_AppCandidateBuild"),
    EMV_APP_SELECT_APP("APP_SelectAppliaction"),
    EMV_GET_CAND_LIST("EMV_AppGetCandListItem"),
    EMV_APP_FINAL_SELECT("EMV_AppFinalSelect"),
    EMV_APP_SET_AID_PARAMS("UpdadteAidParams"),
    EMV_GPO("EMV_GPOProc"),
    EMV_READ_RECORD_DATA("EMV_ReadRecordData"),
    EMV_CONFIRM_CARD_PAN("onConfirmCardInfo"),
    EMV_ONLY_READ_CARD("OnlyReadProc"),
    EMV_OFFLINE_DATA_AUTH("EMV_OfflineDataAuth"),
    EMV_TER_RISK_MANAGEMENT("EMV_TerminalRiskManagement"),
    EMV_PROC_RESTRICTIONS("EMV_ProcessingRestrictions"),
    EMV_CARD_HOLDER_VERIFY("EMV_CardHolderVerify"),
    EMV_TER_ACTION_ANALYZE("EMV_TermActionAnalyze"),
    EMV_APP_CON_2GAC("AppControlExe2GAC"),
    EMV_TRANS_COMPLETE("TransComplete"),

    CLSS_ENTRY_INIT("EntryInitialize"),
    CLSS_LOAD_COMBINATION_PARAM(" LoadCombinationParam"),
    CLSS_ENTRY_PREPROC(" EntrypreProcessing"),
    CLSS_ENTRY_BUILD_CANDI_DATE(" EntrybuildCandidate"),
    CLSS_ENTRY_FINAL_SELECT(" EntryFinalSelect"),
    CLSS_ENTRY_GET_PRE_PROC_RES(" EntrygetPreProcResult"),
    CLSS_KERNEL_INIT(" KernelTrannsInit"),
    CLSS_KERNEL_SET_FINAL_SELECT_DATA(" KernelsetFinalSelectData"),
    CLSS_SET_AID_PARAMS_TO_KERNEL(" KernelSetAidParamsToKernel"),
    CLSS_SET_TRANSDATA_TO_KERNEL(" KernelSetTransData"),
    CLSS_KERNEL_GPO_PROC(" KernelGPO"),
    CLSS_KERNEL_READ_DATA_PROC(" KernelReadData"),
    CLSS_APP_CONFIRM_PAN("AppConfirmPan"),
    CLSS_KERNEL_CARD_AUTH("KernelCardAuth"),
    CLSS_KERNEL_TRANS_PROC("KerneTransProc"),
    CLSS_KERNEL_TRANS_CVM("KerneCardHolderVerify"),
    CLSS_KERNEL_TRANS_COMPLETE("KerneComplete"),

    EMV_END("EMV_END");
    private String messsage;

    public String getMesssage() {
        return messsage;
    }

    ETransStep(String messsage) {
        this.messsage = messsage;
    }

    public byte index() {
        return (byte) ordinal();
    }

    @Override
    public String toString() {
        return "ETransStep{" +
                "messsage='" + messsage + '\'' +
                '}';
    }
}
