/*============================================================
 Module Name       : ECVMStatus.java
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

public enum ECVMStatus {
    ENTER_OK("OK"), ENTER_BYPASS("BYPASS"), ENTER_CANCEL("CANCEL"), ENTER_TIME_OUT("TIME OUT"), ENTER_RFU("RFU"),
    ;

    private String msg;

    ECVMStatus(String msg) {
        this.msg = msg;
    }

    public byte index() {
        return (byte) ordinal();
    }

    @Override
    public String toString() {
        return "ECVMStatus{" + "msg='" + msg + '}';
    }
}
