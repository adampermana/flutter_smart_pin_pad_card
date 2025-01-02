/*============================================================
 Module Name       : ETransStatus.java
 Date of Creation  : 18/12/2024
 Name of Creator   : Adam Permana
 History of Modifications:
 23/12/2024- Initial creation by Adam Permana.

 Summary           :
 This enum represents various transaction statuses for EMV (Europay, Mastercard, Visa) card operations.
 Each status is associated with a specific hexadecimal value that represents its unique code.

 Functions         :
 - getTransStatus(): Retrieves the hexadecimal value of the transaction status.
 - toString(): Provides a string representation of the transaction status.
 - index(): Retrieves the ordinal value of the enum constant as a byte.

 Variables         :
 - transStatus: Stores the hexadecimal code representing the transaction status.

 ============================================================*/

package com.adpstore.flutter_smart_pin_pad_cards.enums;

/**
 * Enumeration for EMV transaction statuses.
 * Each status represents a distinct outcome or step in an EMV transaction process.
 */
public enum ETransStatus {
    /**
     * Indicates the transaction is approved offline.
     */
    OFFLINE_APPROVE(0x01),

    /**
     * Indicates the transaction is approved online.
     */
    ONLINE_APPROVE(0x02),

    /**
     * Indicates the transaction is declined offline.
     */
    OFFLINE_DECLINED(0x03),

    /**
     * Indicates the transaction is declined online.
     */
    ONLINE_DECLINED(0x04),

    /**
     * Indicates an online request is required for further processing.
     */
    ONLINE_REQUEST(0x05),

    /**
     * Indicates the application process has ended.
     */
    END_APPLICATION(0x06),

    /**
     * Indicates that the next AID (Application Identifier) should be selected.
     */
    SELECT_NEXT_AID(0x07),

    /**
     * Indicates another card should be tried.
     */
    TRY_ANOTHER_CARD(0x08),

    /**
     * Indicates another interface should be tried (e.g., chip, NFC, or swipe).
     */
    TRY_ANOTHER_INTERFACE(0x09),

    /**
     * Indicates the user should try the transaction again.
     */

    TRY_AGAIN(0x0A),

    /**
     * Indicates the user should try the transaction again using their phone.
     */
    SEE_PHONE_TRY_AGAIN(0x0B),

    /**
     * Indicates the status is not available or undefined.
     */
    NA(0xFF),
    ;

    // Hexadecimal value representing the transaction status.

    private int transStatus;

    /**
     * Constructor to assign a specific transaction status code to each enum constant.
     *
     * @param transStatus The hexadecimal code representing the transaction status.
     */
    ETransStatus(int transStatus) {
        this.transStatus = transStatus;
    }

    /**
     * Retrieves the hexadecimal code of the transaction status.
     *
     * @return The hexadecimal code of the transaction status.
     */
    public int getTransStatus() {
        return transStatus;
    }

    /**
     * Provides a string representation of the transaction status.
     *
     * @return A string containing the name and hexadecimal code of the transaction status.
     */
    @Override
    public String toString() {
        return "ETransStatus{" + "nStatus=" + transStatus + '}';
    }

    /**
     * Retrieves the ordinal value of the transaction status as a byte.
     *
     * @return The ordinal value of the transaction status as a byte.
     */
    public byte index() {
        return (byte) ordinal();
    }
}
