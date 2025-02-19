/*============================================================
 Module Name       : EmvOnlineResp.java
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

import com.adpstore.flutter_smart_pin_pad_cards.enums.EOnlineResult;
import com.topwise.toptool.api.convert.IConvert;
import com.topwise.toptool.api.packer.ITlv;
import com.topwise.toptool.api.packer.TlvException;
import com.topwise.toptool.impl.TopTool;

public class EmvOnlineResp {
    private EOnlineResult eOnlineResult = EOnlineResult.ONLINE_ABORT; //online Result
    private boolean existAuthCode;
    byte[] authCode; //89 Authorisation Code
    private boolean existAuthRespCode;
    byte[] authRespCode; //8A Authorisation Response Code
    private boolean existIssAuthData;
    byte[] issueAuthData; //91 Issuer Authentication Data
    private boolean existIssScr71;
    byte[] issueScript71; //71 Issuer Script
    private boolean existIssScr72;
    byte[] issueScript72; //72 Issuer Script

    public EOnlineResult geteOnlineResult() {
        return eOnlineResult;
    }

    public void seteOnlineResult(EOnlineResult eOnlineResult) {
        this.eOnlineResult = eOnlineResult;
    }


    public boolean isExistAuthCode() {
        return existAuthCode;
    }

    public void setExistAuthCode(boolean existAuthCode) {
        this.existAuthCode = existAuthCode;
    }

    public boolean isExistAuthRespCode() {
        return existAuthRespCode;
    }

    public void setExistAuthRespCode(boolean existAuthRespCode) {
        this.existAuthRespCode = existAuthRespCode;
    }

    public boolean isExistIssAuthData() {
        return existIssAuthData;
    }

    public void setExistIssAuthData(boolean existIssAuthData) {
        this.existIssAuthData = existIssAuthData;
    }

    public boolean isExistIssScr71() {
        return existIssScr71;
    }

    public void setExistIssScr71(boolean existIssScr71) {
        this.existIssScr71 = existIssScr71;
    }

    public boolean isExistIssScr72() {
        return existIssScr72;
    }

    public void setExistIssScr72(boolean existIssScr72) {
        this.existIssScr72 = existIssScr72;
    }

    public byte[] getAuthCode() {
        return authCode;
    }

    public byte[] getAuthRespCode() {
        return authRespCode;
    }

    public void setAuthCode(byte[] authCode) {
        this.authCode = authCode;
    }

    public void setAuthRespCode(byte[] authRespCode) {
        this.authRespCode = authRespCode;
    }

    public byte[] getIssueAuthData() {
        return issueAuthData;
    }

    public void setIssueAuthData(byte[] issueAuthData) {
        this.issueAuthData = issueAuthData;
    }

    public byte[] getIssueScript71() {
        return issueScript71;
    }

    public void setIssueScript71(byte[] issueScript71) {
        this.issueScript71 = issueScript71;
    }

    public byte[] getIssueScript72() {
        return issueScript72;
    }

    public void setIssueScript72(byte[] issueScript72) {
        this.issueScript72 = issueScript72;
    }

    public EmvOnlineResp parseFiled55(String rspF55) {

        if (TextUtils.isEmpty(rspF55)) {
            return this;
        }
        IConvert convert = TopTool.getInstance().getConvert();
        ITlv tlv = TopTool.getInstance().getPacker().getTlv();
        try {
            byte[] resp55 = convert.strToBcd(rspF55, IConvert.EPaddingPosition.PADDING_LEFT);
            ITlv.ITlvDataObjList list = null;
            list = tlv.unpack(resp55);
            byte[] value91 = list.getValueByTag(0x91);
            if (value91 != null && value91.length > 0) {
                this.issueAuthData = value91;
                this.existIssAuthData = true;
            }
            // set script  71
            byte[] value71 = list.getValueByTag(0x71);
            if (value71 != null && value71.length > 0) {

                ITlv.ITlvDataObj obj = tlv.createTlvDataObject();
                obj.setTag(0x71);
                obj.setValue(value71);
                this.issueScript71 = tlv.pack(obj);
                this.existIssScr71 = true;
            }

            //  set script  72
            byte[] value72 = list.getValueByTag(0x72);
            if (value72 != null && value72.length > 0) {
                ITlv.ITlvDataObj obj = tlv.createTlvDataObject();
                obj.setTag(0x72);
                obj.setValue(value72);
                this.issueScript72 = tlv.pack(obj);
                this.existIssScr72 = true;
            }
            //  set script  89
            byte[] value89 = list.getValueByTag(0x89);
            if (value89 != null && value89.length > 0) {
                this.authCode = value89;
                this.existAuthCode = true;
            }
            //  set script  8A
            if (!existAuthCode) {
                byte[] value8A = list.getValueByTag(0x8A);
                if (value8A != null && value8A.length > 0) {
                    this.authRespCode = value8A;
                    this.existAuthRespCode = true;
                }
            }
        } catch (TlvException e) {
            e.printStackTrace();
        }
        return this;


    }

    private String toSrc(byte[] bcd) {
        if (bcd == null) {
            return null;
        }
        return TopTool.getInstance().getConvert().bcdToStr(bcd);
    }

    @Override
    public String toString() {
        return "EmvOnlineResp{" +
                "eOnlineResult=" + eOnlineResult +
                ", existAuthCode=" + existAuthCode +
                ", authCode=" + toSrc(authCode) +
                ", existAuthRespCode=" + existAuthRespCode +
                ", authRespCode=" + toSrc(authRespCode) +
                ", existIssAuthData=" + existIssAuthData +
                ", issueAuthData=" + toSrc(issueAuthData) +
                ", existIssScr71=" + existIssScr71 +
                ", issueScript71=" + toSrc(issueScript71) +
                ", existIssScr72=" + existIssScr72 +
                ", issueScript72=" + toSrc(issueScript72) +
                '}';
    }
}
