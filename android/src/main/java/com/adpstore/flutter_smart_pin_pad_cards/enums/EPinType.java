/*============================================================
 Module Name       : EPinType.java
 Date of Creation  : 18/12/2024
 Name of Creator   : Adam Permana
 History of Modifications:
 18/12/2024- Lorem Ipsum

 Summary           :


 Functions         :
 -

 Variables         :
 -

 ============================================================*/

package com.adpstore.flutter_smart_pin_pad_cards.enums;

public enum EPinType {
    ONLINE_PIN_REQ((byte) 0x00),
    OFFLINE_PLAIN_TEXT_PIN_REQ((byte) 0x01),
    PCI_MODE_REQ((byte) 0x02),
    ;


    private byte type;

    EPinType(byte type) {
        this.type = type;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "EPinType{" + "type=" + type + '}';
    }
}
