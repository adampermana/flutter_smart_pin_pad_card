package com.adpstore.flutter_smart_pin_pad_cards.emv.enums;

/**
 * Creation date: 2021/6/15 on 11:18
 * describe:
 * Author: Adam Permana
 */
public enum EOnlineResult {
    ONLINE_APPROVE((byte) 0), //Online Return code (Online Approved)
    ONLINE_FAILED((byte) 1),  //Online Return code (Online Failed)
    ONLINE_REFER((byte) 2),   //Online Return code (Online Reference)
    ONLINE_DENIAL((byte) 3),  //Online Return code (Online Denial)
    ONLINE_ABORT((byte) 4);  //Compatible PBOC(Transaction Terminate)

    private byte onlineResult;

    EOnlineResult(byte onlineResult) {
        this.onlineResult = onlineResult;
    }

    public byte getOnlineResult() {
        return this.onlineResult;
    }

    public byte index() {
        return (byte)ordinal();
    }

    @Override
    public String toString() {
        return "EOnlineResult{" + "onlineResult=" + onlineResult + '}';
    }
}
