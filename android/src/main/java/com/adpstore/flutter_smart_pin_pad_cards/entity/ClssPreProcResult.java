package com.adpstore.flutter_smart_pin_pad_cards.entity;

import com.topwise.cloudpos.aidl.emv.level2.PreProcResult;
import com.topwise.toptool.impl.Convert;

public class ClssPreProcResult {
    byte aucCandListAID[] = new byte[17];
    int unCandListAIDLen;
    PreProcResult preProcResult;

    public byte[] getAucCandListAID() {
        return aucCandListAID;
    }

    public void setAucCandListAID(byte[] aucCandListAID) {
        this.aucCandListAID = aucCandListAID;
    }

    public int getUnCandListAIDLen() {
        return unCandListAIDLen;
    }

    public void setUnCandListAIDLen(int unCandListAIDLen) {
        this.unCandListAIDLen = unCandListAIDLen;
    }

    public PreProcResult getPreProcResult() {
        return preProcResult;
    }

    public void setPreProcResult(PreProcResult preProcResult) {
        this.preProcResult = preProcResult;
    }

    @Override
    public String toString() {
        return "ClssPreProcResult{" +
                "aucCandListAID=" + Convert.getInstance().bcdToStr(aucCandListAID) +
                ", unCandListAIDLen=" + unCandListAIDLen +
                ", preProcResult=" + preProcResult.toString() +
                '}';
    }
}
