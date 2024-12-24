/*============================================================
 Module Name       : EmvErrorCode.java
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

package com.adpstore.flutter_smart_pin_pad_cards.emv.entity;

public class EmvErrorCode {
    private static final String TAG = EmvErrorCode.class.getSimpleName();
    public static final String BYPASS = "bypass";

    public static final int EMV_TRUE                   = 1;
    public static final int EMV_FALSE                  = 0;

    //kernel Rid
    public static final String KERNTYPE_VISA_RID    = "A000000003";
    public static final String KERNTYPE_MC_RID      = "A000000004";
    public static final String KERNTYPE_AE_RID      = "A000000025";
    public static final String KERNTYPE_JCB_RID     = "A000000065";
    public static final String KERNTYPE_DPAS_RID    = "A000000152";
    public static final String KERNTYPE_PBOC_RID    = "A000000333";
    public static final String KERNTYPE_FLASH_RID   = "A000000277";
    public static final String KERNTYPE_EFT_RID     = "A000000384";
    public static final String KERNTYPE_PAGO_RID    = "A000000141";
    public static final String KERNTYPE_MIR_RID     = "A000000658";
    public static final String KERNTYPE_RUPAY_RID   = "A000000524";


    //Kernel Type
    public static final int KERNTYPE_DEF = 0x00;
    public static final int KERNTYPE_EMV = 0x00;
    public static final int KERNTYPE_VISAAP = 0x01;
    public static final int KERNTYPE_MC = 0x02; //TransPayPass
    public static final int KERNTYPE_VISA = 0x03; //qVSDC
    public static final int KERNTYPE_AMEX = 0x04;
    public static final int KERNTYPE_JCB = 0x05; //J/Speedy
    public static final int KERNTYPE_ZIP = 0x06; //Discover ZIP or 16
    public static final int KERNTYPE_DPAS = 0x06; //Discover DPAS
    public static final int KERNTYPE_QPBOC = 0x07;
    public static final int KERNTYPE_QUICS = 0x17;// with qPBOC
    public static final int KERNTYPE_RUPAY = 0x0D;
    public static final int KERNTYPE_FLASH = 0x10;
    public static final int KERNTYPE_EFT = 0x11;
    public static final int KERNTYPE_PURE = 0x12;
    public static final int KERNTYPE_PAGO = 0x13;
    public static final int KERNTYPE_MIR = 0x14;
    public static final int KERNTYPE_PBOC = 0xE1; //Contact PBOC
    public static final int KERNTYPE_NSICC = 0xE2;
    public static final int KERNTYPE_RFU = 0xFF;

    //Transaction type
    public static final int EMV_TRANS_TYPE_GOODS = 0x00;
    public static final int EMV_TRANS_TYPE_SERVICE = 0x00;//The same with Goods 00
    public static final int EMV_TRANS_TYPE_CASH = 0x01;
    public static final int EMV_TRANS_TYPE_CASHBACK = 0x09;
    public static final int EMV_TRANS_TYPE_REFUND = 0x20;
    public static final int EMV_TRANS_TYPE_INQUIRY = 0x31;
    public static final int EMV_TRANS_TYPE_TRANSFER = 0x40;
    public static final int EMV_TRANS_TYPE_PAYMENT = 0x50;
    public static final int EMV_TRANS_TYPE_ADMIN = 0x60;
    public static final int EMV_TRANS_TYPE_DEPOSIT = 0x21;

    //CDA mode
    public static final int EMV_CDA_MODE1 = 0x00; //CDA on ARQC _ Req, CDA on 2nd GenAC TC after approved Online authorization
    public static final int EMV_CDA_MODE2 = 0x01; //CDA on ARQC _ No Req CDA on 2 2nd Gen Ac TC after appproved online authorization
    public static final int EMV_CDA_MODE3 = 0x02; //No Req. CDA on ARQC + No Req CDA on 2nd GenAc after approved Online authorization
    public static final int EMV_CDA_MODE4 = 0x03; //No Req CDA on ARQC + req CDA on 2nd GenAcC after approved Online Authorization

    //
    public static final int EMV_DEFAULT_ACTION_CODE_BEFOR_GAC1 = 0x00;    //Default Action before 1st AC
    public static final int EMV_DEFAULT_ACTION_CODE_AFTER_GAC1 = 0x01;    //Default Action after  1st AC

    //Terminal defined Authorisation Response Code
    public static final String OFFLINE_APPROVED = "Y1";
    public static final String OFFLINE_DECLINE = "Z1";
    public static final String ONLINE_APPROVED = "Y2";
    public static final String ONLINE_DECLINE = "Z2";
    public static final String ONLINE_ERROR_OFFLINE_APPROVED = "Y3";
    public static final String ONLINE_ERROR_OFFLINE_DECLINE = "Z3";

    //Online processing results
    public static final int EMV_ONLINE_APPROVED = 0x00;
    public static final int EMV_ONLINE_REJECT = 0x01;
    public static final int EMV_ONLINE_VOICE_PREFER = 0x02;
    public static final int EMV_ONLINE_ERROR = 0x03;
    public static final int EMV_ONLINE_TIME_OUT = 0x04;
    public static final int EMV_ONLINE_CONNECT_FAILED = 0x05;

    //EMV error code definition
    public static final int EMV_OK                  = 0;
    public static final int EMV_APPROVED            = 1; //Approved
    public static final int EMV_FORCE_APPROVED      = 2;
    public static final int EMV_DECLINED            = 3; //Declined
    public static final int EMV_NOT_ALLOWED         = 5;
    public static final int EMV_NO_ACCEPTED         = 6;
    public static final int EMV_TERMINATED          = 7; //Terminated
    public static final int EMV_CARD_BLOCKED        = 8;
    public static final int EMV_APP_BLOCKED         = 9;
    public static final int EMV_NO_APP              = 10;
    public static final int EMV_FALLBACK            = 11;
    public static final int EMV_CAPK_EXPIRED        = 12;
    public static final int EMV_CAPK_CHECKSUM_ERROR = 13;
    public static final int EMV_AID_DUPLICATE       = 14;
    public static final int EMV_CERTIFICATE_RECOVER_FAILED = 15;
    public static final int EMV_DATA_AUTH_FAILED    = 16;
    public static final int EMV_UN_RECOGNIZED_TAG   = 17;
    public static final int EMV_DATA_NOT_EXISTS     = 18;
    public static final int EMV_DATA_LENGTH_ERROR   = 19;
    public static final int EMV_INVALID_TLV         = 20;
    public static final int EMV_INVALID_RESPONSE    = 21;
    public static final int EMV_DATA_DUPLICATE      = 22;
    public static final int EMV_MEMORY_NOT_ENOUGH   = 23;
    public static final int EMV_MEMORY_OVERFLOW     = 24;
    public static final int EMV_PARAMETER_ERROR     = 25; //parameter error
    public static final int EMV_ICC_ERROR           = 26; //ICC error
    public static final int EMV_NO_MORE_DATA        = 27;
    public static final int EMV_CAPK_NO_FOUND       = 28;
    public static final int EMV_AID_NO_FOUND        = 29;
    public static final int EMV_FORMAT_ERROR        = 30;
    public static final int EMV_ONLINE_REQUEST      = 31;//online request -by wfh20190805
    public static final int EMV_SELECT_NEXT_AID     = 32;//Select next AID
    public static final int EMV_TRY_AGAIN           = 33;//Try Again. ICC read failed.
    public static final int EMV_SEE_PHONE           = 34;//Status Code returned by IC card is 6986, please see phone. GPO 6986 CDCVM.
    public static final int EMV_TRY_OTHER_INTERFACE = 35;//Try other interface -by wfh20190805
    public static final int EMV_ICC_ERR_LAST_RECORD = 36;


    public static final int EMV_CALL_BACK              = 253;
    public static final int EMV_CANCEL              = 254;
    public static final int EMV_OTHER_ERROR         = 255;

    //---------------------------------------------------------------------------
    /*************************Error Code for qPBOC Trans*************************/
//---------------------------------------------------------------------------
    public static final int QPBOC_AMOUNT_BIG_THAN_CL_LIMIT          =301;
    public static final int QPBOC_OTHER_INTERFACE                   =302;//Try other interface
    public static final int QPBOC_NEED_RETRY                        =303;//Try Again
    public static final int QPBOC_IN_EXCEPTION_FILE                 =304;
    public static final int QPBOC_APP_EXPIRED                       =305;
    public static final int QPBOC_CANT_GO_ONLINE_DECLINE            =306;
    public static final int QPBOC_TERMINATED_CONTACT                =307;
    public static final int QPBOC_TERMINATED_SWIPE                  =308;
    public static final int QPBOC_APP_EXPIRED_APPROVED              =309;
    public static final int QPBOC_APP_EXPIRED_OFFLINE_DECLINED      =310;
    public static final int QPBOC_APP_EXPIRED_ONLINE_DECLINED       =311;
    public static final int QPBOC_OFFLINE_APPROVED                  =312;//Offline approved
    public static final int QPBOC_ONLINE_APPROVED                   =313;//Online approved
    public static final int QPBOC_OFFLINE_DECLINED                  =314;//Offline Declined
    public static final int QPBOC_ONLINE_DECLINED                   =315;//Online Declined
    public static final int QPBOC_GPO_SW_6986                       =316;//GPO 6986
    public static final int QPBOC_SELECT_NEXT_APP                   =317;//Select next AID
    public static final int QPBOC_SELECT_PPSE                       =318;//?? == try again
    public static final int QPBOC_FDDA_FAIL_ONLINE                  =319;
    public static final int QPBOC_APP_EXPIRED_ONLINE                =320;
    public static final int QPBOC_PDOL_MISSING_9F66                 =321;
    public static final int QPBOC_FCI_MISSING_9F38                  =323;
    public static final int QPBOC_USE_CONTACT                       =324;


    //CLSS=========
    public static final int CLSS_TAG_NOT_EXIST             = 0; //Tag is not present
    public static final int CLSS_TAG_EXIST_WITHVAL         = 1; //Tag is present and not empty
    public static final int CLSS_TAG_EXIST_NOVAL           = 2;//Tag is present but empty


    public static final int CLSS_OK                    =    0;
    public static final int ICC_RESET_ERR              =    -1 ;       //IC card reset is failed
    public static final int ICC_CMD_ERR                =    -2 ;       //IC card command is failed
    public static final int ICC_BLOCK                  =    -3 ;      //IC card is blocked
    public static final int EMV_RSP_ERR                =    -4 ;       //Status Code returned by IC card is not 9000
    public static final int EMV_APP_BLOCK              =    -5 ;      //The Application selected is blocked

    public static final int EMV_USER_CANCEL            =    -7 ;      //The Current operation or transaction was canceled by user
    public static final int EMV_TIME_OUT               =    -8 ;    //User?ˉs operation is timeout
    public static final int EMV_DATA_ERR               =    -9 ;    //Data error is found
    public static final int EMV_NOT_ACCEPT             =    -10 ;    //Transaction is not accepted
    public static final int EMV_DENIAL                 =    -11 ;   //Transaction is denied
    public static final int EMV_KEY_EXP                =    -12 ;   //Certification Authority Public Key is Expired
    public static final int EMV_NO_PINPAD              =    -13 ;   //PIN enter is required, but PIN pad is not present or not working
    public static final int EMV_NO_PASSWORD            =    -14 ;    //PIN enter is required, PIN pad is present, but there is no PIN entered
    public static final int EMV_SUM_ERR                =    -15 ;     //Checksum of CAPK is error
    public static final int EMV_NOT_FOUND              =    -16 ;  //Appointed Data Element can?ˉt be found
    public static final int EMV_NO_DATA                =    -17 ;      //The length of the appointed Data Element is 0
    public static final int EMV_OVERFLOW               =    -18 ;   //Memory is overflow
    public static final int NO_TRANS_LOG               =    -19 ;     //There is no Transaction log
    public static final int RECORD_NOTEXIST            =    -20 ;     //Appointed log is not existed
    public static final int LOGITEM_NOTEXIST           =    -21 ;     //Appointed Label is not existed in current log record
    public static final int ICC_RSP_6985               =    -22 ;     //Status Code returned by IC card for GPO/GAC is 6985
    public static final int EMV_FILE_ERR               =    -24 ;     //There is file error found
    public static final int EMV_PARAM_ERR              =    -30 ;     //Parameter error.

    public static final int CLSS_NO_APP                =    -6 ;     //There is no AID matched between ICC and terminal

    public static final int CLSS_USE_CONTACT           =    -23 ;     //Must use other interface for the transaction
    public static final int CLSS_FILE_ERR              =    -24 ;     //There is file error found
    public static final int CLSS_TERMINATE             =    -25 ;     //Must terminate the transaction
    public static final int CLSS_FAILED                =    -26 ;     //Contactless transaction is failed
    public static final int CLSS_DECLINE               =    -27 ;     //Transaction should be declined.
    public static final int CLSS_TRY_ANOTHER_CARD      =    -28 ;     //Try another card
    public static final int CLSS_PARAM_ERR             =    -30 ;     //Parameter is error

    public static final int CLSS_RESELECT_APP          =    -35 ;     //Select the next AID in candidate list
    public static final int CLSS_CARD_EXPIRED          =    -36 ;     //IC card is expired
    public static final int CLSS_NO_APP_PPSE_ERR       =    -37 ;     //No application is supported(Select PPSE is error)
    public static final int CLSS_USE_VSDC              =    -38 ;     //Switch to contactless PBOC
    public static final int CLSS_CVMDECLINE            =    -39 ;     //CVM result in decline for AE

    public static final int CLSS_REFER_CONSUMER_DEVICE  =    -40 ;    //Status Code returned by IC card is 6986, please see phone
    public static final int CLSS_LAST_CMD_ERR          =    -41 ;     //The last read record command is error(qPBOC Only)
    public static final int CLSS_API_ORDER_ERR         =    -42 ;     //APIs are called in wrong order. Please call GetDebugInfo_xxx to get error codes.
    public static final int CLSS_TORN_CARDNUM_ERR      =    -43 ;     //torn log's pan is different from the reselect card's pan
    public static final int CLSS_TRON_AID_ERR          =    -44 ;    //torn log's AID is different from the reselect card's AID
    public static final int CLSS_TRON_AMT_ERR          =    -45 ;     //torn log's amount is different from the reselect card's amount
    public static final int CLSS_CARD_EXPIRED_REQ_ONLINE   =    -46 ;     //IC card is expired and should continue go online
    public static final int CLSS_FILE_NOT_FOUND        =    -47 ;     //ICC return 6A82 (File not found) in response to the SELECT command
    public static final int CLSS_TRY_AGAIN             =    -48 ;    //Try again for AE3.1
    public static final int CLSS_TORN_RECOVER          =    -49 ;     //1stGAC failed need torn recovery transaction
    public static final int CLSS_TRON_NULL             =    -50 ;    //torn log's is Null
    public static final int CLSS_DEVICE_NOT_AUTH       =    -51 ;    //Status Code returned by IC card is 6987, Please authenticate yourself to your device and try again

    public static final int CLSS_PAYMENT_NOT_ACCEPT    =    -200 ;     // Payment Type Not Accepted for flash

    public static final int CLSS_INSERTED_ICCARD       =    -301 ;    // IC card is detected during contactless transaction
    public static final int CLSS_SWIPED_MAGCARD        =    -302 ;    // Magnetic stripe card is detected during contactless transaction
    public static final int CLSS_MORE_CARD             =    -303 ;   // VCAS More Card


    /**
     * AC Type
     */
    public static final int AC_AAC = 0x00;
    public static final int AC_TC = 0x01;
    public static final int AC_ARQC = 0x02;

    /**
     * Byte 4 bit8-5 CVM
     */
    public static final byte CLSS_OC_NO_CVM = 0x00;
    public static final byte CLSS_OC_OBTAIN_SIGNATURE = 0x10;
    public static final byte CLSS_OC_ONLINE_PIN = 0x20;
    /**
     * Byte 1 bit8-5 Status
     */
    public static final byte CLSS_OC_APPROVED = 0x10;
    public static final byte CLSS_OC_DECLINED = 0x20;
    public static final byte CLSS_OC_ONLINE_REQUEST = 0x30;
    public static final byte CLSS_OC_END_APPLICATION = 0x40;
    public static final byte CLSS_OC_SELECT_NEXT = 0x50;
    public static final byte CLSS_OC_TRY_ANOTHER_INTERFACE = 0x60;
    public static final byte CLSS_OC_TRY_AGAIN = 0x70;
    public static final byte CLSS_OC_NA = (byte) 0xF0;

    public static final byte PINTYPE_OFFLINE = 0x01;
    public static final byte PINTYPE_OFFLINE_LASTTIME = 0x02;
    public static final byte PINTYPE_ONLINE = 0x03;
    /**
     * Transaction Path
     */
    public static final int CLSS_TRANSPATH_EMV = 0;
    public static final int CLSS_TRANSPATH_MAG = 0x10;
    public static final int CLSS_TRANSPATH_LEGACY = 0x20;
    /***Paypass define Message Identifier *****/
    public static final int CLSS_UI_MSGID_SEE_PHONE = 0x20;
}
