/*============================================================
 Module Name       : IAdpUsdk.java
 Date of Creation  : 18/12/2024
 Name of Creator   : Adam Permana
 History of Modifications:
 18/12/2024 - Initial creation by Adam Permana

 Summary           :
 IAdpUsdk.java defines interfaces for interacting with various EMV card processing hardware and systems
 in the context of an Android application. It provides methods for initializing and managing communications
 with hardware components such as ICCard, RFCard, MagCard, PSAM, Serialport, and others. This interface
 facilitates integration with POS Devices.

 Functions         :
 - Provides hardware interaction methods for card processing and system utilities.
 - Supports EMV card operations such as PayWave, AmexPay, UnionPay, etc.
 - Includes methods to manage serial ports and PSAM communication.

 Variables         :
 - None declared in the interface itself.

 ============================================================*/

package com.adpstore.flutter_smart_pin_pad_cards.emv;

import android.content.Context;

import com.topwise.cloudpos.aidl.card.AidlCheckCard;
import com.topwise.cloudpos.aidl.cpucard.AidlCPUCard;
import com.topwise.cloudpos.aidl.emv.level2.AidlAmex;
import com.topwise.cloudpos.aidl.emv.level2.AidlDpas;
import com.topwise.cloudpos.aidl.emv.level2.AidlEmvL2;
import com.topwise.cloudpos.aidl.emv.level2.AidlEntry;
import com.topwise.cloudpos.aidl.emv.level2.AidlJcb;
import com.topwise.cloudpos.aidl.emv.level2.AidlMir;
import com.topwise.cloudpos.aidl.emv.level2.AidlPaypass;
import com.topwise.cloudpos.aidl.emv.level2.AidlPaywave;
import com.topwise.cloudpos.aidl.emv.level2.AidlPure;
import com.topwise.cloudpos.aidl.emv.level2.AidlQpboc;
import com.topwise.cloudpos.aidl.emv.level2.AidlRupay;
import com.topwise.cloudpos.aidl.iccard.AidlICCard;
import com.topwise.cloudpos.aidl.magcard.AidlMagCard;
import com.topwise.cloudpos.aidl.pinpad.AidlPinpad;
import com.topwise.cloudpos.aidl.psam.AidlPsam;
import com.topwise.cloudpos.aidl.rfcard.AidlRFCard;
import com.topwise.cloudpos.aidl.serialport.AidlSerialport;
import com.topwise.cloudpos.aidl.shellmonitor.AidlShellMonitor;
import com.topwise.cloudpos.aidl.system.AidlSystem;

public interface IAdpUsdk {

    /**
     * Retrieves the current version of the SDK or implementation.
     *
     * @return A string representing the version number.
     */
    String getVersion();

    /**
     * Initializes the hardware components and SDK with the given context.
     *
     * @param mContext     The application context.
     * @param initListener Listener to receive initialization callbacks.
     */
    void init(Context mContext, AdpUsdkManage.InitListener initListener);

    /**
     * Retrieves the EMV Level 2 interface.
     *
     * @return AidlEmvL2 instance for EMV operations.
     */
    AidlEmvL2 getEmv();

    /**
     * Retrieves the PURE payment interface.
     *
     * @return AidlPure instance for PURE payment operations.
     */
    AidlPure getPurePay();

    /**
     * Retrieves the PayPass interface for MasterCard contactless payments.
     *
     * @return AidlPaypass instance for PayPass operations.
     */
    AidlPaypass getPaypass();

    /**
     * Retrieves the PayWave interface for contactless payments.
     *
     * @return AidlPaywave instance for PayWave operations.
     */

    AidlPaywave getPaywave();

    /**
     * Retrieves the EMV Entry interface for managing card entry operations.
     *
     * @return AidlEntry instance.
     */
    AidlEntry getEntry();

    /**
     * Retrieves the AmexPay interface for American Express card processing.
     *
     * @return AidlAmex instance.
     */
    AidlAmex getAmexPay();

    /**
     * Retrieves the UnionPay interface for QPBOC transactions.
     *
     * @return AidlQpboc instance for UnionPay operations.
     */
    AidlQpboc getUnionPay();

    /**
     * Retrieves the RuPay interface for RuPay card processing.
     *
     * @return AidlRupay instance.
     */
    AidlRupay getRupay();

    /**
     * Retrieves the MirPay interface for Mir card processing.
     *
     * @return AidlMir instance.
     */
    AidlMir getMirPay();

    /**
     * Retrieves the DPAS interface for Discover card processing.
     *
     * @return AidlDpas instance.
     */
    AidlDpas getDpasPay();

    /**
     * Retrieves the JCB payment interface.
     *
     * @return AidlJcb instance.
     */
    AidlJcb getJcbPay();

    /**
     * Retrieves the system interface for accessing system utilities.
     *
     * @return AidlSystem instance.
     */
    AidlSystem getSystem();

    AidlPinpad getPinpad(int type); //pinpad


    /**
     * Retrieves the shell monitor interface for managing shell operations.
     *
     * @return AidlShellMonitor instance.
     */
    AidlShellMonitor getShellMonitor();

    /**
     * Retrieves the ICCard interface for interacting with chip cards.
     *
     * @return AidlICCard instance.
     */
    AidlICCard getIcc();

    /**
     * Retrieves the RFCard interface for interacting with RFID cards.
     *
     * @return AidlRFCard instance.
     */
    AidlRFCard getRf();

    /**
     * Retrieves the MagCard interface for interacting with magnetic stripe cards.
     *
     * @return AidlMagCard instance.
     */
    AidlMagCard getMag();

    /**
     * Retrieves the PSAM interface for accessing a specific PSAM device.
     *
     * @param devid The ID of the PSAM device.
     * @return AidlPsam instance for the specified device.
     */
    AidlPsam getPsam(int devid);

    /**
     * Retrieves the Serialport interface for a specified port.
     *
     * @param port The port number.
     * @return AidlSerialport instance for the specified port.
     */
    AidlSerialport getSerialport(int port);

    /**
     * Retrieves the CheckCard interface for card presence detection.
     *
     * @return AidlCheckCard instance.
     */
    AidlCheckCard getCheckCard();

    /**
     * Retrieves the CardReader interface for managing card reader operations.
     *
     * @return ICardReader instance.
     */
    ICardReader getCardReader();

    /**
     * Retrieves the EMVHelper interface for assisting with EMV operations.
     *
     * @return IEmv instance.
     */
    IEmv getEmvHelper();

    /**
     * Sets the mode of operation for the SDK.
     *
     * @param mode The mode to set.
     */
    void setMode(int mode);

    /**
     * Closes all active sessions and releases resources.
     */

    AidlCPUCard getCpu();

    void close();
}
