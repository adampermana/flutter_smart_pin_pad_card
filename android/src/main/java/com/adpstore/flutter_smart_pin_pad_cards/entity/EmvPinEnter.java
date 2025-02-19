/*============================================================
 Module Name       : EmvPinEnter.java
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

package com.adpstore.flutter_smart_pin_pad_cards.entity;

import com.adpstore.flutter_smart_pin_pad_cards.enums.ECVMStatus;

public class EmvPinEnter {
    private ECVMStatus ecvmStatus; // CVM status
    private String PlainTextPin; // Offline plain PIN

    public ECVMStatus getEcvmStatus() {
        return ecvmStatus;
    }

    public void setEcvmStatus(ECVMStatus ecvmStatus) {
        this.ecvmStatus = ecvmStatus;
    }

    public String getPlainTextPin() {
        return PlainTextPin;
    }

    public void setPlainTextPin(String plainTextPin) {
        PlainTextPin = plainTextPin;
    }

    @Override
    public String toString() {
        return "PinEnter{" + "ecvmStatus=" + ecvmStatus + '}';
    }
}
