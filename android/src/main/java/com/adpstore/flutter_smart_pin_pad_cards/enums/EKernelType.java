/*============================================================
 Module Name       : EKernelType.java
 Date of Creation  : 19/12/2024
 Name of Creator   : Adam Permana
 History of Modifications:
 19/12/2024 - Initial creation by Adam Permana.

 Summary           :
 This enum defines various kernel types used in the EMV and payment processing context.
 Each kernel type is associated with a unique kernel ID, a descriptive kernel type,
 and an optional RID (Registered Application Provider Identifier).

 Functions         :
 - Retrieve kernel information based on ID or RID.
 - Enumerate all kernel types.
 - Provide string representation for kernel details.

 Variables         :
 - kernelID : The unique identifier for the kernel type.
 - kernelType : The descriptive name of the kernel.
 - RID : The Registered Application Provider Identifier.

 ============================================================*/

package com.adpstore.flutter_smart_pin_pad_cards.enums;

/**
 * Enum representing various kernel types used in payment processing.
 * Each kernel type is associated with a unique kernel ID, a descriptive name, and a RID.
 */
public enum EKernelType {
    KERNTYPE_DEF((byte) 0x00, "DEF", ""),
    KERNTYPE_VISAAP((byte) 0x01, "VISAAP", "A000000003"),
    KERNTYPE_MC((byte) 0x02, "MC", "A000000004"),
    KERNTYPE_VISA((byte) 0x03, "VISA", "A000000003"),
    KERNTYPE_AMEX((byte) 0x04, "AMEX", "A000000025"),
    KERNTYPE_JCB((byte) 0x05, "JCB", "A000000065"),
    KERNTYPE_DPAS((byte) 0x06, "DPAS", "A000000152"),
    KERNTYPE_QPBOC((byte) 0x07, "QPBOC", "A000000333"),
    KERNTYPE_RUPAY((byte) 0x0D, "RUPAY", "A000000524"),
    KERNTYPE_FLASH((byte) 0x10, "FLASH", "A000000277"),
    KERNTYPE_EFT((byte) 0x11, "EFT", "A000000384"),
    KERNTYPE_PURE((byte) 0x12, "PURE", ""),
    KERNTYPE_PAGO((byte) 0x13, "PAGO", "A000000141"),
    KERNTYPE_MIR((byte) 0x14, "MIR", "A000000658"),
    KERNTYPE_QUICS((byte) 0x17, "QUICS", ""),
    KERNTYPE_PBOC((byte) 0xE1, "PBOC", "A000000333"),
    KERNTYPE_NSICC((byte) 0xE2, "NSICC", ""),
    KERNTYPE_RFU((byte) 0xFF, "RFU", ""),
    ;

    private static final EKernelType[] VALUES = EKernelType.values();
    private byte kernelID;
    private String kernelType;
    private String RID;

    /**
     * Constructor for EKernelType.
     *
     * @param kernelID   The unique identifier for the kernel.
     * @param kernelType The descriptive name of the kernel.
     * @param RID        The Registered Application Provider Identifier (optional).
     */
    EKernelType(byte kernelID, String kernelType, String RID) {
        this.kernelID = kernelID;
        this.kernelType = kernelType;
        this.RID = RID;
    }

    /**
     * Retrieves the index of the kernel in the enum declaration.
     *
     * @return The index as a byte.
     */
    public byte index() {
        return (byte) ordinal();
    }

    /**
     * Gets the kernel ID.
     *
     * @return The kernel ID as a byte.
     */
    public byte getKernelID() {
        return kernelID;
    }

    /**
     * Sets the kernel ID.
     *
     * @param kernelID The new kernel ID.
     */
    public void setKernelID(byte kernelID) {
        this.kernelID = kernelID;
    }

    /**
     * Gets the kernel type.
     *
     * @return The kernel type as a string.
     */
    public String getKernelType() {
        return kernelType;
    }

    /**
     * Sets the kernel type.
     *
     * @param kernelType The new kernel type.
     */
    public void setKernelType(String kernelType) {
        this.kernelType = kernelType;
    }

    /**
     * Gets the RID (Registered Application Provider Identifier).
     *
     * @return The RID as a string.
     */
    public String getRID() {
        return RID;
    }

    /**
     * Sets the RID.
     *
     * @param RID The new RID.
     */
    public void setRID(String RID) {
        this.RID = RID;
    }

    /**
     * @param bKernelID
     * @return
     */
    public static EKernelType getKernelType(byte bKernelID) {
        for (EKernelType kernelType : VALUES) {
            if (kernelType.getKernelID() == bKernelID) {
                return kernelType;
            }
        }
        return KERNTYPE_DEF;
    }

    /**
     * Get kernel type by RID
     *
     * @param RID
     * @return
     */
    public static EKernelType getKernelType(String RID) {
        for (EKernelType kernelType : VALUES) {
            if (RID.equals(kernelType.getRID())) {
                return kernelType;
            }
        }
        return KERNTYPE_DEF;
    }

    /**
     * Provides a string representation of the kernel type.
     *
     * @return A string containing the kernel ID, kernel type, and RID.
     */
    @Override
    public String toString() {
        return "EKernelType{ kernelID=" + String.format("%02X", kernelID) + ", kernelType= " + kernelType + ", RID=" + RID + "}";
    }
}
