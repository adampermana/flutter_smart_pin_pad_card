/*============================================================
 Module Name       : EmvAidParam.java
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

package com.adpstore.flutter_smart_pin_pad_cards.entity;

import android.text.TextUtils;

import com.adpstore.flutter_smart_pin_pad_cards.AppLog;
import com.topwise.cloudpos.struct.TlvList;
import com.topwise.toptool.api.convert.IConvert;
import com.topwise.toptool.impl.TopTool;

import java.util.HashMap;
import java.util.Map;

public class EmvAidParam {
    /**
     * aid, application logo
     */
    private String aid; //9F06
    /**
     * Selection flag (PART_MATCH-partial match FULL_MATCH-full match)
     */
    private boolean bPartMatch; //DF01
    /**
     * Kernel Type. EMV default 00
     */
    private String aucKernType; //Kernel Type. EMV default 00
    /**
     * 9F33 or DF811E
     */
    private String aucTmCap;  //9F33 or DF811E
    private boolean bTmCapFlg;

    private String aucTmCapAd;
    private boolean bTmCapAdFlg;////9F40 3 byte

    private String aucTACDefault;
    private boolean bTACDefaultFlg;//DF8120  DF11

    private String aucTACDenail;
    private boolean bTACDenailFlg;//DF8121  DF13

    private String aucTACOnline;
    private boolean bTACOnlineFlg;//DF8122  DF12

    private String aucAPPVer;
    private boolean bAPPVerFlg;////9F09

    //Contact parameter
    private boolean bTermTypeFlg;
    private byte ucTermType;//9F35


    private String aucTermDDOL;
    private boolean bTermDDOLFlg;//DF14  Max length=252

    private String aucTermTDOL;
    private boolean bTermTDOLFlg;//DF8102  Max length=252

    private String aucCountryCode;
    private boolean bCountryCodeFlg;//9F1A

    private String aucCurrencyCode;
    private boolean bCurrencyCodeFlg;//5F2A

    private byte ucCurrencyExp;
    private boolean bCurrencyExpFlg;//5F36

    private String aucRefCurrencyCode;
    private boolean bRefCurrencyCodeExt;//9F3C

    private byte ucRefCurrencyExp;
    private boolean bRefCurrencyExpExt;//9F3D

    private String aucRefCurrencyCon;
    private boolean bRefCurrencyConExt;//DF8101

    private String aucFloorLimit;
    private boolean bFloorLimitFlg;//9F1B

    private String aucThreshold;
    private boolean bThresholdFlg;//DF15

    private byte ucMaxTP;
    private boolean bMaxTPFlg;//DF16

    private byte ucTP;
    private boolean bTPFlg;//DF17

    private byte ucPosEntryMode;
    private boolean bPosEntryModeFlg;//9F39 IC 05,RF 07

    private String aucIFD;
    private boolean bIFDFlg;//9F1E

    private String aucTermID;
    private boolean bTermIDFlg;//9F1C

    private String aucAcquireID;
    private boolean bAcquireIDFlg;//9F01

    private byte ucStatusCheckVal;
    private boolean bStatusCheckFlg;////DFC108, 00-not check status, 01-check

    private byte ucZeroCheckVal;
    private boolean bZeroCheckFlg;////DFC109, 0-alow(Option1), 1-not alow(Option2), 2-Deactivated

    private String aucReaderTTQ;
    private boolean bReaderTTQFlg;//9F66

    private String aucRdClssTxnLmt;
    private boolean bRdClssTxnLmtFlg;//DF8124

    private String aucRdClssTxnLmtOnDevice;
    private boolean bRdClssTxnLmtOnDeviceFlg;//DF8125 //9F7B EC transaction limit

    private String aucRdClssFLmt;
    private boolean bRdClssFLmtFlg;//DF8123

    private String aucRdCVMLmt;
    private boolean bRdCVMLmtFlg;//DF8126

    private String aucBalBeforeGAC;
    private boolean bBalBeforeGACFlg;//DF8104 - PayPass

    private String aucBalAfterGAC;
    private boolean bBalAfterGACFlg;//DF8105 - PayPass

    private String aucUDOL;
    private boolean bUDOLFlg;//DF811A

    private String aucTmCapNoCVM;
    private boolean bTmCapNoCVMFlg;//DF812C - MagStripe Terminal Capability

    private String aucMagStripeVer;
    private boolean bMagStripeVerFlg;//9F6D

    private byte ucMagStripeSupp;
    private boolean bMagStripeSuppFlg;//

    private String aucMerchID;
    private boolean bMerchIDFlg;//9F16

    private String aucTRMData;
    private boolean bTRMDataFlg;//9F1D, Terminal Risk Management Data

    private String aucMerchNameLoc;
    private boolean bMerchNameLocFlg;//9F4E

    private String aucMerchCateCode;
    private boolean bMerchCateCodeFlg;//9F15

    private String aucMerchCustomData;
    private boolean bMerchCustomDataFlg;//9F7C

    private byte ucAccountType;
    private boolean bAccountTypeFlg;//5F57

    private boolean ucCrypto17Flg;//VISA

    private String aucEnhancedCapa;
    private boolean bEnhancedCapaFlg;//9F6E,Enhanced Contactless Reader Capabilities(AMEX)

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public boolean isbPartMatch() {
        return bPartMatch;
    }

    public void setbPartMatch(boolean bPartMatch) {
        this.bPartMatch = bPartMatch;
    }

    public String getAucKernType() {
        return aucKernType;
    }

    public void setAucKernType(String aucKernType) {
        this.aucKernType = aucKernType;
    }

    public String getAucTmCap() {
        return aucTmCap;
    }

    public void setAucTmCap(String aucTmCap) {
        this.aucTmCap = aucTmCap;
    }

    public boolean isbTmCapFlg() {
        return bTmCapFlg;
    }

    public void setbTmCapFlg(boolean bTmCapFlg) {
        this.bTmCapFlg = bTmCapFlg;
    }

    public String getAucTmCapAd() {
        return aucTmCapAd;
    }

    public void setAucTmCapAd(String aucTmCapAd) {
        this.aucTmCapAd = aucTmCapAd;
    }

    public boolean isbTmCapAdFlg() {
        return bTmCapAdFlg;
    }

    public void setbTmCapAdFlg(boolean bTmCapAdFlg) {
        this.bTmCapAdFlg = bTmCapAdFlg;
    }

    public String getAucTACDefault() {
        return aucTACDefault;
    }

    public void setAucTACDefault(String aucTACDefault) {
        this.aucTACDefault = aucTACDefault;
    }

    public boolean isbTACDefaultFlg() {
        return bTACDefaultFlg;
    }

    public void setbTACDefaultFlg(boolean bTACDefaultFlg) {
        this.bTACDefaultFlg = bTACDefaultFlg;
    }

    public String getAucTACDenail() {
        return aucTACDenail;
    }

    public void setAucTACDenail(String aucTACDenail) {
        this.aucTACDenail = aucTACDenail;
    }

    public boolean isbTACDenailFlg() {
        return bTACDenailFlg;
    }

    public void setbTACDenailFlg(boolean bTACDenailFlg) {
        this.bTACDenailFlg = bTACDenailFlg;
    }

    public String getAucTACOnline() {
        return aucTACOnline;
    }

    public void setAucTACOnline(String aucTACOnline) {
        this.aucTACOnline = aucTACOnline;
    }

    public boolean isbTACOnlineFlg() {
        return bTACOnlineFlg;
    }

    public void setbTACOnlineFlg(boolean bTACOnlineFlg) {
        this.bTACOnlineFlg = bTACOnlineFlg;
    }

    public String getAucAPPVer() {
        return aucAPPVer;
    }

    public void setAucAPPVer(String aucAPPVer) {
        this.aucAPPVer = aucAPPVer;
    }

    public boolean isbAPPVerFlg() {
        return bAPPVerFlg;
    }

    public void setbAPPVerFlg(boolean bAPPVerFlg) {
        this.bAPPVerFlg = bAPPVerFlg;
    }

    public boolean isbTermTypeFlg() {
        return bTermTypeFlg;
    }

    public void setbTermTypeFlg(boolean bTermTypeFlg) {
        this.bTermTypeFlg = bTermTypeFlg;
    }

    public byte getUcTermType() {
        return ucTermType;
    }

    public void setUcTermType(byte ucTermType) {
        this.ucTermType = ucTermType;
    }

    public String getAucTermDDOL() {
        return aucTermDDOL;
    }

    public void setAucTermDDOL(String aucTermDDOL) {
        this.aucTermDDOL = aucTermDDOL;
    }

    public boolean isbTermDDOLFlg() {
        return bTermDDOLFlg;
    }

    public void setbTermDDOLFlg(boolean bTermDDOLFlg) {
        this.bTermDDOLFlg = bTermDDOLFlg;
    }

    public String getAucTermTDOL() {
        return aucTermTDOL;
    }

    public void setAucTermTDOL(String aucTermTDOL) {
        this.aucTermTDOL = aucTermTDOL;
    }

    public boolean isbTermTDOLFlg() {
        return bTermTDOLFlg;
    }

    public void setbTermTDOLFlg(boolean bTermTDOLFlg) {
        this.bTermTDOLFlg = bTermTDOLFlg;
    }

    public String getAucCountryCode() {
        return aucCountryCode;
    }

    public void setAucCountryCode(String aucCountryCode) {
        this.aucCountryCode = aucCountryCode;
    }

    public boolean isbCountryCodeFlg() {
        return bCountryCodeFlg;
    }

    public void setbCountryCodeFlg(boolean bCountryCodeFlg) {
        this.bCountryCodeFlg = bCountryCodeFlg;
    }

    public String getAucCurrencyCode() {
        return aucCurrencyCode;
    }

    public void setAucCurrencyCode(String aucCurrencyCode) {
        this.aucCurrencyCode = aucCurrencyCode;
    }

    public boolean isbCurrencyCodeFlg() {
        return bCurrencyCodeFlg;
    }

    public void setbCurrencyCodeFlg(boolean bCurrencyCodeFlg) {
        this.bCurrencyCodeFlg = bCurrencyCodeFlg;
    }

    public byte getUcCurrencyExp() {
        return ucCurrencyExp;
    }

    public void setUcCurrencyExp(byte ucCurrencyExp) {
        this.ucCurrencyExp = ucCurrencyExp;
    }

    public boolean isbCurrencyExpFlg() {
        return bCurrencyExpFlg;
    }

    public void setbCurrencyExpFlg(boolean bCurrencyExpFlg) {
        this.bCurrencyExpFlg = bCurrencyExpFlg;
    }

    public String getAucRefCurrencyCode() {
        return aucRefCurrencyCode;
    }

    public void setAucRefCurrencyCode(String aucRefCurrencyCode) {
        this.aucRefCurrencyCode = aucRefCurrencyCode;
    }

    public boolean isbRefCurrencyCodeExt() {
        return bRefCurrencyCodeExt;
    }

    public void setbRefCurrencyCodeExt(boolean bRefCurrencyCodeExt) {
        this.bRefCurrencyCodeExt = bRefCurrencyCodeExt;
    }

    public byte getUcRefCurrencyExp() {
        return ucRefCurrencyExp;
    }

    public void setUcRefCurrencyExp(byte ucRefCurrencyExp) {
        this.ucRefCurrencyExp = ucRefCurrencyExp;
    }

    public boolean isbRefCurrencyExpExt() {
        return bRefCurrencyExpExt;
    }

    public void setbRefCurrencyExpExt(boolean bRefCurrencyExpExt) {
        this.bRefCurrencyExpExt = bRefCurrencyExpExt;
    }

    public String getAucRefCurrencyCon() {
        return aucRefCurrencyCon;
    }

    public void setAucRefCurrencyCon(String aucRefCurrencyCon) {
        this.aucRefCurrencyCon = aucRefCurrencyCon;
    }

    public boolean isbRefCurrencyConExt() {
        return bRefCurrencyConExt;
    }

    public void setbRefCurrencyConExt(boolean bRefCurrencyConExt) {
        this.bRefCurrencyConExt = bRefCurrencyConExt;
    }

    public String getAucFloorLimit() {
        return aucFloorLimit;
    }

    public void setAucFloorLimit(String aucFloorLimit) {
        this.aucFloorLimit = aucFloorLimit;
    }

    public boolean isbFloorLimitFlg() {
        return bFloorLimitFlg;
    }

    public void setbFloorLimitFlg(boolean bFloorLimitFlg) {
        this.bFloorLimitFlg = bFloorLimitFlg;
    }

    public String getAucThreshold() {
        return aucThreshold;
    }

    public void setAucThreshold(String aucThreshold) {
        this.aucThreshold = aucThreshold;
    }

    public boolean isbThresholdFlg() {
        return bThresholdFlg;
    }

    public void setbThresholdFlg(boolean bThresholdFlg) {
        this.bThresholdFlg = bThresholdFlg;
    }

    public byte getUcMaxTP() {
        return ucMaxTP;
    }

    public void setUcMaxTP(byte ucMaxTP) {
        this.ucMaxTP = ucMaxTP;
    }

    public boolean isbMaxTPFlg() {
        return bMaxTPFlg;
    }

    public void setbMaxTPFlg(boolean bMaxTPFlg) {
        this.bMaxTPFlg = bMaxTPFlg;
    }

    public byte getUcTP() {
        return ucTP;
    }

    public void setUcTP(byte ucTP) {
        this.ucTP = ucTP;
    }

    public boolean isbTPFlg() {
        return bTPFlg;
    }

    public void setbTPFlg(boolean bTPFlg) {
        this.bTPFlg = bTPFlg;
    }

    public byte getUcPosEntryMode() {
        return ucPosEntryMode;
    }

    public void setUcPosEntryMode(byte ucPosEntryMode) {
        this.ucPosEntryMode = ucPosEntryMode;
    }

    public boolean isbPosEntryModeFlg() {
        return bPosEntryModeFlg;
    }

    public void setbPosEntryModeFlg(boolean bPosEntryModeFlg) {
        this.bPosEntryModeFlg = bPosEntryModeFlg;
    }

    public String getAucIFD() {
        return aucIFD;
    }

    public void setAucIFD(String aucIFD) {
        this.aucIFD = aucIFD;
    }

    public boolean isbIFDFlg() {
        return bIFDFlg;
    }

    public void setbIFDFlg(boolean bIFDFlg) {
        this.bIFDFlg = bIFDFlg;
    }

    public String getAucAcquireID() {
        return aucAcquireID;
    }

    public void setAucAcquireID(String aucAcquireID) {
        this.aucAcquireID = aucAcquireID;
    }

    public boolean isbAcquireIDFlg() {
        return bAcquireIDFlg;
    }

    public void setbAcquireIDFlg(boolean bAcquireIDFlg) {
        this.bAcquireIDFlg = bAcquireIDFlg;
    }

    public byte getUcStatusCheckVal() {
        return ucStatusCheckVal;
    }

    public void setUcStatusCheckVal(byte ucStatusCheckVal) {
        this.ucStatusCheckVal = ucStatusCheckVal;
    }

    public boolean isbStatusCheckFlg() {
        return bStatusCheckFlg;
    }

    public void setbStatusCheckFlg(boolean bStatusCheckFlg) {
        this.bStatusCheckFlg = bStatusCheckFlg;
    }

    public byte getUcZeroCheckVal() {
        return ucZeroCheckVal;
    }

    public void setUcZeroCheckVal(byte ucZeroCheckVal) {
        this.ucZeroCheckVal = ucZeroCheckVal;
    }

    public boolean isbZeroCheckFlg() {
        return bZeroCheckFlg;
    }

    public void setbZeroCheckFlg(boolean bZeroCheckFlg) {
        this.bZeroCheckFlg = bZeroCheckFlg;
    }

    public String getAucReaderTTQ() {
        return aucReaderTTQ;
    }

    public void setAucReaderTTQ(String aucReaderTTQ) {
        this.aucReaderTTQ = aucReaderTTQ;
    }

    public boolean isbReaderTTQFlg() {
        return bReaderTTQFlg;
    }

    public void setbReaderTTQFlg(boolean bReaderTTQFlg) {
        this.bReaderTTQFlg = bReaderTTQFlg;
    }

    public String getAucRdClssTxnLmt() {
        return aucRdClssTxnLmt;
    }

    public void setAucRdClssTxnLmt(String aucRdClssTxnLmt) {
        this.aucRdClssTxnLmt = aucRdClssTxnLmt;
    }

    public boolean isbRdClssTxnLmtFlg() {
        return bRdClssTxnLmtFlg;
    }

    public void setbRdClssTxnLmtFlg(boolean bRdClssTxnLmtFlg) {
        this.bRdClssTxnLmtFlg = bRdClssTxnLmtFlg;
    }

    public String getAucRdClssTxnLmtOnDevice() {
        return aucRdClssTxnLmtOnDevice;
    }

    public void setAucRdClssTxnLmtOnDevice(String aucRdClssTxnLmtOnDevice) {
        this.aucRdClssTxnLmtOnDevice = aucRdClssTxnLmtOnDevice;
    }

    public boolean isbRdClssTxnLmtOnDeviceFlg() {
        return bRdClssTxnLmtOnDeviceFlg;
    }

    public void setbRdClssTxnLmtOnDeviceFlg(boolean bRdClssTxnLmtOnDeviceFlg) {
        this.bRdClssTxnLmtOnDeviceFlg = bRdClssTxnLmtOnDeviceFlg;
    }

    public String getAucRdClssFLmt() {
        return aucRdClssFLmt;
    }

    public void setAucRdClssFLmt(String aucRdClssFLmt) {
        this.aucRdClssFLmt = aucRdClssFLmt;
    }

    public boolean isbRdClssFLmtFlg() {
        return bRdClssFLmtFlg;
    }

    public void setbRdClssFLmtFlg(boolean bRdClssFLmtFlg) {
        this.bRdClssFLmtFlg = bRdClssFLmtFlg;
    }

    public String getAucRdCVMLmt() {
        return aucRdCVMLmt;
    }

    public void setAucRdCVMLmt(String aucRdCVMLmt) {
        this.aucRdCVMLmt = aucRdCVMLmt;
    }

    public boolean isbRdCVMLmtFlg() {
        return bRdCVMLmtFlg;
    }

    public void setbRdCVMLmtFlg(boolean bRdCVMLmtFlg) {
        this.bRdCVMLmtFlg = bRdCVMLmtFlg;
    }

    public String getAucBalBeforeGAC() {
        return aucBalBeforeGAC;
    }

    public void setAucBalBeforeGAC(String aucBalBeforeGAC) {
        this.aucBalBeforeGAC = aucBalBeforeGAC;
    }

    public boolean isbBalBeforeGACFlg() {
        return bBalBeforeGACFlg;
    }

    public void setbBalBeforeGACFlg(boolean bBalBeforeGACFlg) {
        this.bBalBeforeGACFlg = bBalBeforeGACFlg;
    }

    public String getAucBalAfterGAC() {
        return aucBalAfterGAC;
    }

    public void setAucBalAfterGAC(String aucBalAfterGAC) {
        this.aucBalAfterGAC = aucBalAfterGAC;
    }

    public boolean isbBalAfterGACFlg() {
        return bBalAfterGACFlg;
    }

    public void setbBalAfterGACFlg(boolean bBalAfterGACFlg) {
        this.bBalAfterGACFlg = bBalAfterGACFlg;
    }

    public String getAucUDOL() {
        return aucUDOL;
    }

    public void setAucUDOL(String aucUDOL) {
        this.aucUDOL = aucUDOL;
    }

    public boolean isbUDOLFlg() {
        return bUDOLFlg;
    }

    public void setbUDOLFlg(boolean bUDOLFlg) {
        this.bUDOLFlg = bUDOLFlg;
    }

    public String getAucTmCapNoCVM() {
        return aucTmCapNoCVM;
    }

    public void setAucTmCapNoCVM(String aucTmCapNoCVM) {
        this.aucTmCapNoCVM = aucTmCapNoCVM;
    }

    public boolean isbTmCapNoCVMFlg() {
        return bTmCapNoCVMFlg;
    }

    public void setbTmCapNoCVMFlg(boolean bTmCapNoCVMFlg) {
        this.bTmCapNoCVMFlg = bTmCapNoCVMFlg;
    }

    public String getAucMagStripeVer() {
        return aucMagStripeVer;
    }

    public void setAucMagStripeVer(String aucMagStripeVer) {
        this.aucMagStripeVer = aucMagStripeVer;
    }

    public boolean isbMagStripeVerFlg() {
        return bMagStripeVerFlg;
    }

    public void setbMagStripeVerFlg(boolean bMagStripeVerFlg) {
        this.bMagStripeVerFlg = bMagStripeVerFlg;
    }

    public byte getUcMagStripeSupp() {
        return ucMagStripeSupp;
    }

    public void setUcMagStripeSupp(byte ucMagStripeSupp) {
        this.ucMagStripeSupp = ucMagStripeSupp;
    }

    public boolean isbMagStripeSuppFlg() {
        return bMagStripeSuppFlg;
    }

    public void setbMagStripeSuppFlg(boolean bMagStripeSuppFlg) {
        this.bMagStripeSuppFlg = bMagStripeSuppFlg;
    }

    public String getAucMerchID() {
        return aucMerchID;
    }

    public void setAucMerchID(String aucMerchID) {
        this.aucMerchID = aucMerchID;
    }

    public boolean isbMerchIDFlg() {
        return bMerchIDFlg;
    }

    public void setbMerchIDFlg(boolean bMerchIDFlg) {
        this.bMerchIDFlg = bMerchIDFlg;
    }

    public String getAucTRMData() {
        return aucTRMData;
    }

    public void setAucTRMData(String aucTRMData) {
        this.aucTRMData = aucTRMData;
    }

    public boolean isbTRMDataFlg() {
        return bTRMDataFlg;
    }

    public void setbTRMDataFlg(boolean bTRMDataFlg) {
        this.bTRMDataFlg = bTRMDataFlg;
    }

    public String getAucMerchNameLoc() {
        return aucMerchNameLoc;
    }

    public void setAucMerchNameLoc(String aucMerchNameLoc) {
        this.aucMerchNameLoc = aucMerchNameLoc;
    }

    public boolean isbMerchNameLocFlg() {
        return bMerchNameLocFlg;
    }

    public void setbMerchNameLocFlg(boolean bMerchNameLocFlg) {
        this.bMerchNameLocFlg = bMerchNameLocFlg;
    }

    public String getAucMerchCateCode() {
        return aucMerchCateCode;
    }

    public void setAucMerchCateCode(String aucMerchCateCode) {
        this.aucMerchCateCode = aucMerchCateCode;
    }

    public boolean isbMerchCateCodeFlg() {
        return bMerchCateCodeFlg;
    }

    public void setbMerchCateCodeFlg(boolean bMerchCateCodeFlg) {
        this.bMerchCateCodeFlg = bMerchCateCodeFlg;
    }

    public String getAucMerchCustomData() {
        return aucMerchCustomData;
    }

    public void setAucMerchCustomData(String aucMerchCustomData) {
        this.aucMerchCustomData = aucMerchCustomData;
    }

    public boolean isbMerchCustomDataFlg() {
        return bMerchCustomDataFlg;
    }

    public void setbMerchCustomDataFlg(boolean bMerchCustomDataFlg) {
        this.bMerchCustomDataFlg = bMerchCustomDataFlg;
    }

    public byte getUcAccountType() {
        return ucAccountType;
    }

    public void setUcAccountType(byte ucAccountType) {
        this.ucAccountType = ucAccountType;
    }

    public boolean isbAccountTypeFlg() {
        return bAccountTypeFlg;
    }

    public void setbAccountTypeFlg(boolean bAccountTypeFlg) {
        this.bAccountTypeFlg = bAccountTypeFlg;
    }

    public boolean isUcCrypto17Flg() {
        return ucCrypto17Flg;
    }

    public void setUcCrypto17Flg(boolean ucCrypto17Flg) {
        this.ucCrypto17Flg = ucCrypto17Flg;
    }

    public String getAucEnhancedCapa() {
        return aucEnhancedCapa;
    }

    public void setAucEnhancedCapa(String aucEnhancedCapa) {
        this.aucEnhancedCapa = aucEnhancedCapa;
    }

    public boolean isbEnhancedCapaFlg() {
        return bEnhancedCapaFlg;
    }

    public void setbEnhancedCapaFlg(boolean bEnhancedCapaFlg) {
        this.bEnhancedCapaFlg = bEnhancedCapaFlg;
    }

    public String getAucTermID() {
        return aucTermID;
    }

    public void setAucTermID(String aucTermID) {
        this.aucTermID = aucTermID;
    }

    public boolean isbTermIDFlg() {
        return bTermIDFlg;
    }

    public void setbTermIDFlg(boolean bTermIDFlg) {
        this.bTermIDFlg = bTermIDFlg;
    }

    public static Map<String, TlvList> tlvListMap = new HashMap<>();

    public static void clearTlvMap() {
        tlvListMap.clear();
    }

    public static void putTlvMap(String aid, EmvAidParam aidParam) {
        tlvListMap.put(aid, genTlvListForAidParam(aidParam));
    }

    public static TlvList getTlvMap(String aid) {
        return tlvListMap.get(aid);
    }

    /**
     * @param emvAidParam
     * @return
     */
    public static TlvList genTlvListForAidParam(EmvAidParam emvAidParam) {
        IConvert convert = TopTool.getInstance().getConvert();
        TlvList tlvList = new TlvList();
        tlvList.addTlv("9F53", "01");
        tlvList.addTlv("DF8117", "00");
        tlvList.addTlv("DF8118", "40");
        tlvList.addTlv("DF8119", "08");
        tlvList.addTlv("DF811F", "08");
        tlvList.addTlv("DF811A", "9F6A04");
        tlvList.addTlv("9F6D", "C0");
        tlvList.addTlv("DF811E", "10");
        tlvList.addTlv("DF812C", "00");

        if (emvAidParam.isbTmCapAdFlg()) {
            tlvList.addTlv("9F40", emvAidParam.getAucTmCapAd());
        } else {
            tlvList.addTlv("9F40", "0000000000");
        }

        if (emvAidParam.isbAPPVerFlg()) {
            tlvList.addTlv("9F09", emvAidParam.getAucAPPVer());
        } else {
            tlvList.addTlv("9F09", "0002");
        }
        if (emvAidParam.isbFloorLimitFlg()) {
            //            tlvList.addTlv("9F1B", BytesUtil.int2Bytes(Integer.valueOf(emvAidParam.getFloorLimit() + ""),true));
            tlvList.addTlv("9F1B", emvAidParam.getAucFloorLimit());
        }
        if (emvAidParam.isbRdClssFLmtFlg()) {
            tlvList.addTlv("DF8123", emvAidParam.getAucRdClssFLmt());
            tlvList.addTlv("DF51", emvAidParam.getAucRdClssFLmt()); //mir
        } else {
            tlvList.addTlv("DF8123", "000000030000");
            tlvList.addTlv("DF51", "000000030000"); //mir
        }
        if (emvAidParam.isbRdClssTxnLmtFlg()) {
            tlvList.addTlv("DF8124", emvAidParam.getAucRdClssTxnLmt());
            tlvList.addTlv("DF4C", emvAidParam.getAucRdClssTxnLmt()); //rupay tag
            tlvList.addTlv("DF53", emvAidParam.getAucRdClssTxnLmt()); //mir
        }
        if (emvAidParam.isbRdClssTxnLmtOnDeviceFlg()) {
            tlvList.addTlv("DF8125", convert.strToBcd(emvAidParam.getAucRdClssTxnLmtOnDevice(), IConvert.EPaddingPosition.PADDING_RIGHT));
        }
        if (emvAidParam.isbRdCVMLmtFlg()) {
            tlvList.addTlv("DF8126", convert.strToBcd(emvAidParam.getAucRdCVMLmt(), IConvert.EPaddingPosition.PADDING_RIGHT));
            tlvList.addTlv("DF4D", emvAidParam.getAucRdCVMLmt()); // rupay
            tlvList.addTlv("DF52", emvAidParam.getAucRdCVMLmt()); // mir
        }
        if (emvAidParam.isbMaxTPFlg()) {
            tlvList.addTlv("DF8131", String.format("%02x", emvAidParam.getUcMaxTP()));

        }
        if (emvAidParam.isbTPFlg()) {
            tlvList.addTlv("DF8132", String.format("%02x", emvAidParam.getUcTP()));
        }
        if (emvAidParam.isbThresholdFlg() && !TextUtils.isEmpty(emvAidParam.getAucThreshold())) {
            long lThreshold = convert.strToLong(emvAidParam.getAucThreshold(), IConvert.EPaddingPosition.PADDING_LEFT);
            tlvList.addTlv("DF8133", String.format("%012d", lThreshold));
        }

        /**
         *  FF03,FF02,FF01;MIR
         * Visa TACs	TAC Denial-0010000000
         * 	            TAC Online-DC4004F800
         * 	            TAC Default-DC4000A800
         *
         * MasterCard	TAC Denial-0010000000
         * 	            TAC Online-FE50BCF800
         * 	            TAC Default-FE50BCA000
         * Masetro	TAC Denial-0018000000
         * 	        TAC Online-FE50BCF800
         * 	        TAC Default-FE50BCA000
         */

        if (emvAidParam.isbTACDefaultFlg()) {
            tlvList.addTlv("DF8120", convert.strToBcd(emvAidParam.getAucTACDefault(), IConvert.EPaddingPosition.PADDING_RIGHT));
        }
        if (emvAidParam.isbTACDenailFlg()) {
            tlvList.addTlv("DF8121", convert.strToBcd(emvAidParam.getAucTACDenail(), IConvert.EPaddingPosition.PADDING_RIGHT));
        }
        if (emvAidParam.isbTACOnlineFlg()) {
            tlvList.addTlv("DF8122", convert.strToBcd(emvAidParam.getAucTACOnline(), IConvert.EPaddingPosition.PADDING_RIGHT));
        }
        if (emvAidParam.isbAcquireIDFlg()) {
            tlvList.addTlv("9F01", convert.strToBcd(emvAidParam.getAucAcquireID(), IConvert.EPaddingPosition.PADDING_RIGHT));
        }
        if (emvAidParam.isbIFDFlg()) {
            tlvList.addTlv("9F1E", emvAidParam.getAucIFD());
        } else {
            tlvList.addTlv("9F1E", "3030303030303031");
        }
        if (emvAidParam.isbMerchCateCodeFlg()) {
            tlvList.addTlv("9F15", emvAidParam.getAucMerchCateCode());
        } else {
            tlvList.addTlv("9F15", "0001");
        }
        if (emvAidParam.isbMerchIDFlg()) {
            tlvList.addTlv("9F16", convert.strToBcd(emvAidParam.getAucMerchID(), IConvert.EPaddingPosition.PADDING_RIGHT));
        }
        if (emvAidParam.isbMerchNameLocFlg()) {
            tlvList.addTlv("9F4E", convert.strToBcd(emvAidParam.getAucMerchNameLoc(), IConvert.EPaddingPosition.PADDING_RIGHT));
        }
        if (emvAidParam.isbCountryCodeFlg()) {
            tlvList.addTlv("9F1A", convert.strToBcd(emvAidParam.getAucCountryCode(), IConvert.EPaddingPosition.PADDING_RIGHT));
        }
        if (emvAidParam.isbTermIDFlg()) {
            tlvList.addTlv("9F1C", convert.strToBcd(emvAidParam.getAucTermID(), IConvert.EPaddingPosition.PADDING_RIGHT));
        }
        if (emvAidParam.isbTermTypeFlg()) {
            tlvList.addTlv("9F35", String.format("%02d", emvAidParam.getUcTermType()));
        } else {
            tlvList.addTlv("9F35", "22");
        }
        if (emvAidParam.isbCurrencyCodeFlg()) {
            tlvList.addTlv("5F2A", convert.strToBcd(emvAidParam.getAucCurrencyCode(), IConvert.EPaddingPosition.PADDING_RIGHT));
        }
        if (emvAidParam.isbCurrencyExpFlg()) {
            tlvList.addTlv("5F36", String.format("%02d", emvAidParam.getUcCurrencyExp()));
        } else {
            tlvList.addTlv("5F36", "02");
        }
        if (emvAidParam.isbReaderTTQFlg()) {
            tlvList.addTlv("9F66", convert.strToBcd(emvAidParam.getAucReaderTTQ(), IConvert.EPaddingPosition.PADDING_RIGHT));
        }
        AppLog.d("EmvAidParam", tlvList.toString());
        return tlvList;
    }

    @Override
    public String toString() {
        return "EmvAidParam{" + "aid='" + aid + '\'' + ", bPartMatch=" + bPartMatch + ", aucKernType='" + aucKernType + '\'' + ", aucTmCap='" + aucTmCap + '\'' + ", bTmCapFlg=" + bTmCapFlg + ", aucTmCapAd='" + aucTmCapAd + '\'' + ", bTmCapAdFlg=" + bTmCapAdFlg + ", aucTACDefault='" + aucTACDefault + '\'' + ", bTACDefaultFlg=" + bTACDefaultFlg + ", aucTACDenail='" + aucTACDenail + '\'' + ", bTACDenailFlg=" + bTACDenailFlg + ", aucTACOnline='" + aucTACOnline + '\'' + ", bTACOnlineFlg=" + bTACOnlineFlg + ", aucAPPVer='" + aucAPPVer + '\'' + ", bAPPVerFlg=" + bAPPVerFlg + ", bTermTypeFlg=" + bTermTypeFlg + ", ucTermType=" + ucTermType + ", aucTermDDOL='" + aucTermDDOL + '\'' + ", bTermDDOLFlg=" + bTermDDOLFlg + ", aucTermTDOL='" + aucTermTDOL + '\'' + ", bTermTDOLFlg=" + bTermTDOLFlg + ", aucCountryCode='" + aucCountryCode + '\'' + ", bCountryCodeFlg=" + bCountryCodeFlg + ", aucCurrencyCode='" + aucCurrencyCode + '\'' + ", bCurrencyCodeFlg=" + bCurrencyCodeFlg + ", ucCurrencyExp=" + ucCurrencyExp + ", bCurrencyExpFlg=" + bCurrencyExpFlg + ", aucRefCurrencyCode='" + aucRefCurrencyCode + '\'' + ", bRefCurrencyCodeExt=" + bRefCurrencyCodeExt + ", ucRefCurrencyExp=" + ucRefCurrencyExp + ", bRefCurrencyExpExt=" + bRefCurrencyExpExt + ", aucRefCurrencyCon='" + aucRefCurrencyCon + '\'' + ", bRefCurrencyConExt=" + bRefCurrencyConExt + ", aucFloorLimit='" + aucFloorLimit + '\'' + ", bFloorLimitFlg=" + bFloorLimitFlg + ", aucThreshold='" + aucThreshold + '\'' + ", bThresholdFlg=" + bThresholdFlg + ", ucMaxTP=" + ucMaxTP + ", bMaxTPFlg=" + bMaxTPFlg + ", ucTP=" + ucTP + ", bTPFlg=" + bTPFlg + ", ucPosEntryMode=" + ucPosEntryMode + ", bPosEntryModeFlg=" + bPosEntryModeFlg + ", aucIFD='" + aucIFD + '\'' + ", bIFDFlg=" + bIFDFlg + ", aucTermID='" + aucTermID + '\'' + ", bTermIDFlg=" + bTermIDFlg + ", aucAcquireID='" + aucAcquireID + '\'' + ", bAcquireIDFlg=" + bAcquireIDFlg + ", ucStatusCheckVal=" + ucStatusCheckVal + ", bStatusCheckFlg=" + bStatusCheckFlg + ", ucZeroCheckVal=" + ucZeroCheckVal + ", bZeroCheckFlg=" + bZeroCheckFlg + ", aucReaderTTQ='" + aucReaderTTQ + '\'' + ", bReaderTTQFlg=" + bReaderTTQFlg + ", aucRdClssTxnLmt='" + aucRdClssTxnLmt + '\'' + ", bRdClssTxnLmtFlg=" + bRdClssTxnLmtFlg + ", aucRdClssTxnLmtOnDevice='" + aucRdClssTxnLmtOnDevice + '\'' + ", bRdClssTxnLmtOnDeviceFlg=" + bRdClssTxnLmtOnDeviceFlg + ", aucRdClssFLmt='" + aucRdClssFLmt + '\'' + ", bRdClssFLmtFlg=" + bRdClssFLmtFlg + ", aucRdCVMLmt='" + aucRdCVMLmt + '\'' + ", bRdCVMLmtFlg=" + bRdCVMLmtFlg + ", aucBalBeforeGAC='" + aucBalBeforeGAC + '\'' + ", bBalBeforeGACFlg=" + bBalBeforeGACFlg + ", aucBalAfterGAC='" + aucBalAfterGAC + '\'' + ", bBalAfterGACFlg=" + bBalAfterGACFlg + ", aucUDOL='" + aucUDOL + '\'' + ", bUDOLFlg=" + bUDOLFlg + ", aucTmCapNoCVM='" + aucTmCapNoCVM + '\'' + ", bTmCapNoCVMFlg=" + bTmCapNoCVMFlg + ", aucMagStripeVer='" + aucMagStripeVer + '\'' + ", bMagStripeVerFlg=" + bMagStripeVerFlg + ", ucMagStripeSupp=" + ucMagStripeSupp + ", bMagStripeSuppFlg=" + bMagStripeSuppFlg + ", aucMerchID='" + aucMerchID + '\'' + ", bMerchIDFlg=" + bMerchIDFlg + ", aucTRMData='" + aucTRMData + '\'' + ", bTRMDataFlg=" + bTRMDataFlg + ", aucMerchNameLoc='" + aucMerchNameLoc + '\'' + ", bMerchNameLocFlg=" + bMerchNameLocFlg + ", aucMerchCateCode='" + aucMerchCateCode + '\'' + ", bMerchCateCodeFlg=" + bMerchCateCodeFlg + ", aucMerchCustomData='" + aucMerchCustomData + '\'' + ", bMerchCustomDataFlg=" + bMerchCustomDataFlg + ", ucAccountType=" + ucAccountType + ", bAccountTypeFlg=" + bAccountTypeFlg + ", ucCrypto17Flg=" + ucCrypto17Flg + ", aucEnhancedCapa='" + aucEnhancedCapa + '\'' + ", bEnhancedCapaFlg=" + bEnhancedCapaFlg + '}';
    }
}
