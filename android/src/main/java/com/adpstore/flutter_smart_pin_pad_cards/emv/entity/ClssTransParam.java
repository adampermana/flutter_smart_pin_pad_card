package com.adpstore.flutter_smart_pin_pad_cards.emv.entity;

import com.topwise.cloudpos.aidl.emv.level2.PreProcResult;
import com.topwise.cloudpos.aidl.emv.level2.TransParam;
import com.topwise.toptool.impl.TopTool;

/**
 * Creation date: 2021/6/10 on 16:36
 * describe:
 * Author: Adam Permana
 */
public class ClssTransParam {
    private byte kernType;
    private int nFinalSelectFCIdataLen;
    private byte[] aucFinalSelectFCIdata;
    private PreProcResult preProcResult;
    private TransParam transParam;

    private boolean clssForceOnlinePin; //是否强制联机 ture 强制
    private boolean bSupSimpleProc; //Is it a simple process


    public boolean isClssForceOnlinePin() {
        return clssForceOnlinePin;
    }

    public void setClssForceOnlinePin(boolean clssForceOnlinePin) {
        this.clssForceOnlinePin = clssForceOnlinePin;
    }

    public byte getKernType() {
        return kernType;
    }

    public void setKernType(byte kernType) {
        this.kernType = kernType;
    }

    public int getnFinalSelectFCIdataLen() {
        return nFinalSelectFCIdataLen;
    }

    public void setnFinalSelectFCIdataLen(int nFinalSelectFCIdataLen) {
        this.nFinalSelectFCIdataLen = nFinalSelectFCIdataLen;
    }

    public byte[] getAucFinalSelectFCIdata() {
        return aucFinalSelectFCIdata;
    }

    public void setAucFinalSelectFCIdata(byte[] aucFinalSelectFCIdata) {
        this.aucFinalSelectFCIdata = aucFinalSelectFCIdata;
    }

    public PreProcResult getPreProcResult() {
        return preProcResult;
    }

    public void setPreProcResult(PreProcResult preProcResult) {
        this.preProcResult = preProcResult;
    }

    public TransParam getTransParam() {
        return transParam;
    }

    public void setTransParam(TransParam transParam) {
        this.transParam = transParam;
    }

    public boolean isbSupSimpleProc() {
        return bSupSimpleProc;
    }

    public void setbSupSimpleProc(boolean bSupSimpleProc) {
        this.bSupSimpleProc = bSupSimpleProc;
    }

    private String getFinalSelectFCIData(){
        if (aucFinalSelectFCIdata != null){
           return TopTool.getInstance().getConvert().bcdToStr(aucFinalSelectFCIdata);
        }
        return "null";
    }

    @Override
    public String toString() {
        return "ClssTransParam{" +
                "kernType=" + kernType +
                ", nFinalSelectFCIdataLen=" + nFinalSelectFCIdataLen +
                ", aucFinalSelectFCIdata=" + getFinalSelectFCIData() +
                ", preProcResult=" + preProcResult +
                ", transParam=" + transParam +
                ", clssForceOnlinePin=" + clssForceOnlinePin +
                ", bSupSimpleProc=" + bSupSimpleProc +
                '}';
    }
}
