package com.adpstore.flutter_smart_pin_pad_cards.entity;

/*****User Interface Request Data --DF8116 Len=22*****/
public class ClssUserInterRequestData {
    byte ucUIMessageID = (byte) 0xFF;               /*UI Request on Outcome Present*/
    byte ucUIStatus= (byte) 0xFF;                   /*UI Request on Outcome Present*/
    byte [] aucUIHoldTime = new byte[3];
    byte [] aucUILanguagePreference = new byte[8];
    byte ucUIValueQualifier;
    byte [] aucUIValue = new byte[6];
    byte [] aucUICurrencyCode = new byte[2];

    public ClssUserInterRequestData() {
    }

    public ClssUserInterRequestData(byte [] aucUserInterReqData) {
        this.ucUIMessageID      =  (byte)(aucUserInterReqData[0] & 0xF0);;
        this.ucUIStatus         = (byte)(aucUserInterReqData[1] & 0xF0);;
    }

    public byte getUcUIMessageID() {
        return ucUIMessageID;
    }

    public void setUcUIMessageID(byte ucUIMessageID) {
        this.ucUIMessageID = ucUIMessageID;
    }

    public byte getUcUIStatus() {
        return ucUIStatus;
    }

    public void setUcUIStatus(byte ucUIStatus) {
        this.ucUIStatus = ucUIStatus;
    }

    public byte[] getAucUIHoldTime() {
        return aucUIHoldTime;
    }

    public void setAucUIHoldTime(byte[] aucUIHoldTime) {
        this.aucUIHoldTime = aucUIHoldTime;
    }

    public byte[] getAucUILanguagePreference() {
        return aucUILanguagePreference;
    }

    public void setAucUILanguagePreference(byte[] aucUILanguagePreference) {
        this.aucUILanguagePreference = aucUILanguagePreference;
    }

    public byte getUcUIValueQualifier() {
        return ucUIValueQualifier;
    }

    public void setUcUIValueQualifier(byte ucUIValueQualifier) {
        this.ucUIValueQualifier = ucUIValueQualifier;
    }

    public byte[] getAucUIValue() {
        return aucUIValue;
    }

    public void setAucUIValue(byte[] aucUIValue) {
        this.aucUIValue = aucUIValue;
    }

    public byte[] getAucUICurrencyCode() {
        return aucUICurrencyCode;
    }

    public void setAucUICurrencyCode(byte[] aucUICurrencyCode) {
        this.aucUICurrencyCode = aucUICurrencyCode;
    }
}
