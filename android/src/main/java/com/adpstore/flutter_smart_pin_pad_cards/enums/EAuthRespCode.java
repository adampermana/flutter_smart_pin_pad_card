/*============================================================
 Module Name       : EAuthRespCode.java
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

import com.adpstore.flutter_smart_pin_pad_cards.AppLog;

/**
 * Y1 = Offline approved
 * • Z1 = Offline declined
 * • Y3 = Unable to go online (offline approved)
 * • Z3 = Unable to go online (offline declined)
 */
public enum EAuthRespCode {
    ISS_APPROVE_00("3030"),
    ISS_APPROVE_10("3130"),
    ISS_APPROVE_11("3131"),
    OFFLINE_APPROVE_Y1("5931"),
    OFFLINE_DECLINED_Z1("5A31"),
    UNABLE_GOTO_ONLINE_APPROVE_Y3("5933"),
    UNABLE_GOTO_ONLINE_DECLINED_Z3("5A33"),
    ;
    private static final EAuthRespCode[] VALUES = EAuthRespCode.values();
    private final String respCode;

    EAuthRespCode(String respCode) {
        this.respCode = respCode;
    }

    public static boolean checkTransResSatus(String respCode, EKernelType eKernelType) {
        AppLog.d("checkTransResSatus", respCode);
        if (ISS_APPROVE_00.respCode.equals(respCode) ||
                ISS_APPROVE_10.respCode.equals(respCode) ||
                ISS_APPROVE_11.respCode.equals(respCode)) {
            return true;
        }
        return false;
    }

    public String getRespCode() {
        return respCode;
    }

    @Override
    public String toString() {
        return "EAuthRespCode{" + "respCode='" + respCode + '\'' + '}';
    }

}
