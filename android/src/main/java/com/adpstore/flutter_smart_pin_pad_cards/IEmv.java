package com.adpstore.flutter_smart_pin_pad_cards;

import android.os.RemoteException;

import com.adpstore.flutter_smart_pin_pad_cards.entity.EinputType;
import com.adpstore.flutter_smart_pin_pad_cards.entity.EmvOutCome;
import com.adpstore.flutter_smart_pin_pad_cards.entity.EmvTransPraram;
import com.topwise.cloudpos.aidl.emv.level2.EmvKernelConfig;
import com.topwise.cloudpos.aidl.emv.level2.EmvTerminalInfo;

/**
 * Creation date: 2021/6/10 on 16:36
 * describe:
 * Author: Adam Permana
 */
public interface IEmv {

    /**
     * initialize
     *
     * @param einputType card type
     */
    void init(EinputType einputType);

    /**
     * Get version
     *
     * @return
     */
    String getVersion();

    /**
     * Set EMV process listener
     *
     * @param ITransProcessListener
     */
    void setProcessListener(ITransProcessListener ITransProcessListener);

    /**
     * Set kernel config param
     *
     * @param emvKernelConfig
     */
    void setKernelConfig(EmvKernelConfig emvKernelConfig);

    /**
     * Set terminal info param
     *
     * @param emvTerminalInfo
     */
    void setTerminalInfo(EmvTerminalInfo emvTerminalInfo);

    /**
     * @param emvTransData
     */
    void setTransPraram(EmvTransPraram emvTransData);

    /**
     * get TLV data from kernel
     *
     * @param paramInt
     * @return
     */
    byte[] getTlv(int paramInt);

    /**
     * set TLV to kernel
     *
     * @param paramInt
     * @param paramArrayOfbyte
     */
    boolean setTlv(int paramInt, byte[] paramArrayOfbyte);

    /**
     * set TLV data list to kernel
     *
     * @param paramArrayOfbyte
     */
    boolean setTlvList(byte[] paramArrayOfbyte);

    /**
     * get TLV data list from kernel
     *
     * @param tagList
     * @return
     */
    byte[] getTlvList(int[] tagList);

    /**
     * Start EMV process
     *
     * @return
     */
    EmvOutCome StartEmvProcess();

    /**
     * Complete EMV process
     *
     * @param online
     * @param respCode
     * @param icc55
     * @return
     */
    EmvOutCome CompleteEmvProcess(boolean online, String respCode, String icc55);

    /**
     * Update AID param
     *
     * @param cmd
     * @param data
     * @return
     * @throws RemoteException
     */
    boolean updateAID(int cmd, String data) throws RemoteException;

    /**
     * Update CA public key param
     *
     * @param cmd
     * @param data
     * @return
     * @throws RemoteException
     */
    boolean updateCAPK(int cmd, String data) throws RemoteException;

    /**
     * Get debug info from kernel
     */
    void getDebugInfo();

    /**
     * Finish EMV Process
     */
    void EndEmvProcess();
}
