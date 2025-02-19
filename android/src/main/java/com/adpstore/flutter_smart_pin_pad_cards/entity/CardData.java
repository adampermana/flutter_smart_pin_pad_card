package com.adpstore.flutter_smart_pin_pad_cards.entity;

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

    /**
     * ICC specific data
     */
    private byte[] iccData;

    /**
     * ICC card serial number
     */
    private String iccSerialNumber;

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

    // Existing getters and setters...

    /**
     * Get ICC data
     * @return byte array containing ICC data
     */
    public byte[] getIccData() {
        return iccData;
    }

    /**
     * Set ICC data
     * @param iccData byte array containing ICC data
     */
    public void setIccData(byte[] iccData) {
        this.iccData = iccData;
    }

    /**
     * Get ICC card serial number
     * @return String containing ICC serial number
     */
    public String getIccSerialNumber() {
        return iccSerialNumber;
    }

    /**
     * Set ICC card serial number
     * @param iccSerialNumber String containing ICC serial number
     */
    public void setIccSerialNumber(String iccSerialNumber) {
        this.iccSerialNumber = iccSerialNumber;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CardData{")
                .append("eReturnType=").append(eReturnType.toString())
                .append(", eCardType=").append(eCardType)
                .append(", track1='").append(track1).append('\'')
                .append(", track2='").append(track2).append('\'')
                .append(", track3='").append(track3).append('\'');

        if (eCardType == ECardType.IC) {
            sb.append(", iccSerialNumber='").append(iccSerialNumber).append('\'');
            if (iccData != null) {
                sb.append(", iccDataLength=").append(iccData.length);
            }
        }

        sb.append('}');
        return sb.toString();
    }
}