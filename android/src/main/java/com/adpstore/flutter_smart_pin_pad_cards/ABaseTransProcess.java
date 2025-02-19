package com.adpstore.flutter_smart_pin_pad_cards;

import com.adpstore.flutter_smart_pin_pad_cards.entity.EmvAidParam;
import com.adpstore.flutter_smart_pin_pad_cards.entity.EmvOutCome;
import com.adpstore.flutter_smart_pin_pad_cards.entity.EmvTransPraram;
import com.topwise.cloudpos.aidl.emv.level2.EmvKernelConfig;
import com.topwise.cloudpos.aidl.emv.level2.EmvTerminalInfo;
import com.topwise.cloudpos.aidl.emv.level2.TransParam;

/**
 * abstract ABaseTransProcess
 *
 * ABaseTransProcess is the abstract class of EMV process, both include contact and contactless emv process.
 * TopManager generate the concrete object according to current card type.
 */
public abstract class ABaseTransProcess {
    protected EmvKernelConfig emvKernelConfig;
    protected EmvTerminalInfo emvTerminalInfo;
    protected EmvTransPraram emvTransData;
    protected ITransProcessListener emvProcessListener;
    protected EmvAidParam curAidParam;
    protected TransParam curTransParam;


    /**
     * Start the EMV transaction process
     * @return
     */
    public abstract EmvOutCome StartTransProcess();

    /**
     * get TLV data from kernel
     * @param tag specific TAG
     * @return
     */
    public abstract byte [] getTLV(int tag);

    /**
     * Set TAG data to kernel
     *
     * @param tag TAG
     * @param datas Value
     * @return
     */
    public abstract boolean setTLV(int tag, byte [] datas);

    /**
     * Get debug info from kernel
     */
    public abstract void getDebugInfo();

    /**
     * Set EMV transaction process listener
     * @param emvProcessListener
     */
    public void setEmvProcessListener(ITransProcessListener emvProcessListener) {
        this.emvProcessListener = emvProcessListener;
    }

    /**
     * Get kernel config param
     * @return
     */
    public EmvKernelConfig getEmvKernelConfig() {
        return emvKernelConfig;
    }

    /**
     * Set kernel config param
     * @param emvKernelConfig
     */
    public void setEmvKernelConfig(EmvKernelConfig emvKernelConfig) {
        this.emvKernelConfig = emvKernelConfig;
    }

    /**
     * Get terminal info param
     * @return
     */
    public EmvTerminalInfo getEmvTerminalInfo() {
        return emvTerminalInfo;
    }

    /**
     * Set terminal info param to kernel
     * @param emvTerminalInfo
     */
    public void setEmvTerminalInfo(EmvTerminalInfo emvTerminalInfo) {
        this.emvTerminalInfo = emvTerminalInfo;
    }

    /**
     * Get EMV transaction data
     * @return
     */
    public EmvTransPraram getEmvTransData() {
        return emvTransData;
    }

    /**
     * Set EMV transaction data
     * @param emvTransData
     */
    public void setEmvTransData(EmvTransPraram emvTransData) {
        this.emvTransData = emvTransData;
    }

    /**
     * Set current AID param
     * @param curAidParam
     */
    protected void setCurAidParam(EmvAidParam curAidParam) {
        this.curAidParam = curAidParam;
    }
}
