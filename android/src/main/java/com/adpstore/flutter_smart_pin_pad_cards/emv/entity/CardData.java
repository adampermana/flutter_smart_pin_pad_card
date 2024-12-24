/*============================================================
 Module Name       : CardData.java
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

public class CardData {

    /**
     * Card type
     */
    public enum ECardType {
        IC, RF, MAG;
    }

    /**
     * Return error type
     */
    private EReturnType eReturnType;

    /**
     * Card type
     */
    private ECardType eCardType;

    /**
     * Track 1 data
     */
    private String track1;

    /**
     * Track 2 data
     */
    private String track2;

    /**
     * Track 3 data
     */
    private String track3;

    private String pan = "";

    /**
     * Expiry date
     */
    private String expiryDate = "";

    /**
     * Service code
     */
    private String serviceCode = "";

    public CardData(EReturnType eReturnType) {
        this.eReturnType = eReturnType;
    }

    public CardData(EReturnType eReturnType, ECardType eCardType) {
        this.eReturnType = eReturnType;
        this.eCardType = eCardType;
    }

    public CardData(EReturnType ereturnType, ECardType eCardType, String track1, String track2, String track3) {
        this.eReturnType = ereturnType;
        this.eCardType = eCardType;
        this.track1 = track1;
        this.track2 = track2;
        this.track3 = track3;
    }

    public enum EReturnType {
        OK(0, "SUCCESS"),
        TIMEOUT(-1, "TIMEOUT"),
        CANCEL(-2, "CANCEL"),
        OPEN_MAG_ERR(-3, "OPEN_MAG_ERR"),
        OPEN_IC_ERR(-4, "OPEN_IC_ERR"),
        OPEN_RF_ERR(-5, "OPEN_RF_ERR"),
        OPEN_MAG_RESET_ERR(-6, "OPEN_MAG_RESET_ERR"),
        OPEN_IC_RESET_ERR(-7, "OPEN_IC_RESET_ERR"),
        OPEN_RF_RESET_ERR(-8, "OPEN_RF_RESET_ERR"),
        RF_MULTI_CARD(-9, "CLS_MULTI_CARD"),
        OTHER_ERR(-10, "OTHER_ERR");

        private int code;
        private String msg;

        EReturnType(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        @Override
        public String toString() {
            return "EReturnType{" +
                    "code=" + code +
                    ", msg='" + msg + '\'' +
                    '}';
        }
    }

    public EReturnType getEreturnType() {
        return eReturnType;
    }

    public void setEreturnType(EReturnType ereturnType) {
        this.eReturnType = ereturnType;
    }

    public ECardType getEcardType() {
        return eCardType;
    }

    public void setEcardType(ECardType eCardType) {
        this.eCardType = eCardType;
    }

    public String getTrack1() {
        return track1;
    }

    public void setTrack1(String track1) {
        this.track1 = track1;
    }

    public String getTrack2() {
        return track2;
    }

    public void setTrack2(String track2) {
        this.track2 = track2;
    }

    public String getTrack3() {
        return track3;
    }

    public void setTrack3(String track3) {
        this.track3 = track3;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String toString() {
        return "CardData{" +
                "eReturnType=" + eReturnType.toString() +
                ", eCardType=" + eCardType +
                ", track1='" + track1 + '\'' +
                ", track2='" + track2 + '\'' +
                ", track3='" + track3 + '\'' +
                '}';
    }

}
