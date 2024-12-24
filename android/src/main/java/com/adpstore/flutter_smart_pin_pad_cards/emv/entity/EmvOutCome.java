package com.adpstore.flutter_smart_pin_pad_cards.emv.entity;

import com.adpstore.flutter_smart_pin_pad_cards.emv.enums.ETransStatus;
import com.adpstore.flutter_smart_pin_pad_cards.emv.enums.ETransStep;

/**
 * Creation date: 2021/6/10 on 16:55
 * describe:
 * Author: Adam Permana
 */
public class EmvOutCome {
    private ETransStatus eTransStatus;
    private int nErrorCodeL2;
    private ETransStep eTransStep;
    private String note;

    public EmvOutCome(int nErrorCodeL2) {
        this.nErrorCodeL2 = nErrorCodeL2;
        this.eTransStatus = ETransStatus.NA;
        this.eTransStep = ETransStep.EMV_NA;
    }

    public EmvOutCome(int nErrorCodeL2, ETransStep eTransStep) {
        this.nErrorCodeL2 = nErrorCodeL2;
        this.eTransStatus = ETransStatus.NA;
        this.eTransStep = eTransStep;
    }

    public EmvOutCome(int nErrorCodeL2, ETransStatus eTransStatus, ETransStep eTransStep) {
        this.nErrorCodeL2 = nErrorCodeL2;
        this.eTransStatus = eTransStatus;
        this.eTransStep = eTransStep;

    }

    public EmvOutCome(ETransStatus eTransStatus, int nErrorCodeL2, ETransStep eTransStep, String note) {
        this.eTransStatus = eTransStatus;
        this.nErrorCodeL2 = nErrorCodeL2;
        this.eTransStep = eTransStep;
        this.note = note;
    }

    public ETransStatus geteTransStatus() {
        return eTransStatus;
    }

    public void seteTransStatus(ETransStatus eTransStatus) {
        this.eTransStatus = eTransStatus;
    }

    public int getnErrorCodeL2() {
        return nErrorCodeL2;
    }

    public void setnErrorCodeL2(int nErrorCodeL2) {
        this.nErrorCodeL2 = nErrorCodeL2;
    }

    public ETransStep geteTransStep() {
        return eTransStep;
    }

    public void seteTransStep(ETransStep eTransStep) {
        this.eTransStep = eTransStep;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "TransStep=" + eTransStep.getMesssage() +
                "\nTransStatus=" + eTransStatus.getTransStatus() +
                "\n ErrorCodeL2=" + nErrorCodeL2;
    }
}
