/*============================================================
 Module Name       : ClssOutComeData.java
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

/**
 * Outcome Parameter Set --DF8129
 */
public class ClssOutComeData {
    private byte ucOCStatus = EmvErrorCode.CLSS_OC_END_APPLICATION;
    private byte ucOCStart = (byte) 0xF0;
    private byte ucOCOnlineResponseData = (byte) 0xF0;
    private byte ucOCCVM = (byte) 0xF0;
    private byte ucOCPresent; /*outcome/restart/discretionary/data record/receipte present*/
    private byte ucOCAlternateInterfacePreference = (byte) 0xF0;  /*Contact chip or Mag-stripe*/
    private byte ucOCFieldOffRequest = (byte) 0xF0;
    private byte ucOCRemovalTimeout;

    public ClssOutComeData() {

    }

    public ClssOutComeData(byte[] aucOutcomeBuf) {
        this.ucOCStatus = (byte) (aucOutcomeBuf[0] & 0xF0);
        this.ucOCStart = (byte) (aucOutcomeBuf[1] & 0xF0);
        this.ucOCOnlineResponseData = (byte) (aucOutcomeBuf[2] & 0xF0);
        this.ucOCCVM = (byte) (aucOutcomeBuf[3] & 0xF0);
        this.ucOCPresent = (byte) (aucOutcomeBuf[4] & 0xF0);
        this.ucOCAlternateInterfacePreference = (byte) (aucOutcomeBuf[5] & 0xF0);
        this.ucOCFieldOffRequest = (byte) (aucOutcomeBuf[6] & 0xF0);
        this.ucOCRemovalTimeout = (byte) (aucOutcomeBuf[7] & 0xF0);
    }

    public byte getUcOCStatus() {
        return ucOCStatus;
    }

    public void setUcOCStatus(byte ucOCStatus) {
        this.ucOCStatus = ucOCStatus;
    }

    public byte getUcOCStart() {
        return ucOCStart;
    }

    public void setUcOCStart(byte ucOCStart) {
        this.ucOCStart = ucOCStart;
    }

    public byte getUcOCOnlineResponseData() {
        return ucOCOnlineResponseData;
    }

    public void setUcOCOnlineResponseData(byte ucOCOnlineResponseData) {
        this.ucOCOnlineResponseData = ucOCOnlineResponseData;
    }

    public byte getUcOCCVM() {
        return ucOCCVM;
    }

    public void setUcOCCVM(byte ucOCCVM) {
        this.ucOCCVM = ucOCCVM;
    }

    public byte getUcOCPresent() {
        return ucOCPresent;
    }

    public void setUcOCPresent(byte ucOCPresent) {
        this.ucOCPresent = ucOCPresent;
    }

    public byte getUcOCAlternateInterfacePreference() {
        return ucOCAlternateInterfacePreference;
    }

    public void setUcOCAlternateInterfacePreference(byte ucOCAlternateInterfacePreference) {
        this.ucOCAlternateInterfacePreference = ucOCAlternateInterfacePreference;
    }

    public byte getUcOCFieldOffRequest() {
        return ucOCFieldOffRequest;
    }

    public void setUcOCFieldOffRequest(byte ucOCFieldOffRequest) {
        this.ucOCFieldOffRequest = ucOCFieldOffRequest;
    }

    public byte getUcOCRemovalTimeout() {
        return ucOCRemovalTimeout;
    }

    public void setUcOCRemovalTimeout(byte ucOCRemovalTimeout) {
        this.ucOCRemovalTimeout = ucOCRemovalTimeout;
    }

    @Override
    public String toString() {
        return "ClssOutComeData{" +
                "ucOCStatus=" + String.format("%02X", ucOCStatus) +
                ", ucOCStart=" + String.format("%02X", ucOCStart) +
                ", ucOCOnlineResponseData=" + String.format("%02X", ucOCOnlineResponseData) +
                ", ucOCCVM=" + String.format("%02X", ucOCCVM) +
                ", ucOCPresent=" + String.format("%02X", ucOCPresent) +
                ", ucOCAlternateInterfacePreference=" + String.format("%02X", ucOCAlternateInterfacePreference) +
                ", ucOCFieldOffRequest=" + String.format("%02X", ucOCFieldOffRequest) +
                ", ucOCRemovalTimeout=" + String.format("%02X", ucOCRemovalTimeout) +
                '}';
    }


}
