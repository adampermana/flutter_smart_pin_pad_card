/*============================================================
 Module Name       : AppLog.java
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

package com.adpstore.flutter_smart_pin_pad_cards;

import android.util.Log;

public class AppLog {
    public enum EDebugLevel {
        DEBUG_V, DEBUG_D, DEBUG_I, DEBUG_W, DEBUG_E,
    }

    /**
     * Control whether android.util.Log.v outputs
     */
    public static boolean BDEUBG = true;
    public static boolean DEBUG_V = BDEUBG;

    /*
     * Control whether android.util.Log.d outputs
     */
    public static boolean DEBUG_D = BDEUBG;

    /**
     * Control whether android.util.Log.i outputs
     */
    public static boolean DEBUG_I = BDEUBG;
    /**
     * Control whether android.util.Log.w outputs
     */
    public static boolean DEBUG_W = BDEUBG;
    /**
     * Control whether android.util.Log.e outputs
     */
    public static boolean DEBUG_E = BDEUBG;

    /**
     * Simultaneously control V/D/I/W/E 5 output switches
     *
     * @param debugFlag switch, true to open, false to close
     */
    public static void debug(boolean debugFlag) {
        DEBUG_V = debugFlag;
        DEBUG_D = debugFlag;
        DEBUG_I = debugFlag;
        DEBUG_W = debugFlag;
        DEBUG_E = debugFlag;
    }

    /**
     * Control 5 types of output switches V/D/I/W/E respectively
     *
     * @param debugFlag switch, true to open, false to close
     */

    public static void debug(EDebugLevel debugLevel, boolean debugFlag) {
        if (debugLevel == EDebugLevel.DEBUG_V) {
            DEBUG_V = debugFlag;
        } else if (debugLevel == EDebugLevel.DEBUG_D) {
            DEBUG_D = debugFlag;
        } else if (debugLevel == EDebugLevel.DEBUG_I) {
            DEBUG_I = debugFlag;
        } else if (debugLevel == EDebugLevel.DEBUG_W) {
            DEBUG_W = debugFlag;
        } else if (debugLevel == EDebugLevel.DEBUG_E) {
            DEBUG_E = debugFlag;
        }
    }

    /**
     * Output v level log, internally determine whether to actually output log according to the set switch
     *
     * @param tag Tag definition of android.util.log in the same system
     * @param msg Information to be output
     */
    public static void v(String tag, String msg) {
        if (DEBUG_V) {
            String[] infos = getAutoJumpLogInfos();
            Log.v(tag, infos[1] + infos[2] + "= " + msg);
        }
    }

    /**
     * Output d level log, internally determine whether to actually output log according to the set switch
     *
     * @param tag Tag definition of android.util.log in the same system
     * @param msg Information to be output
     */
    public static void d(String tag, String msg) {
        if (DEBUG_D) {
            String[] infos = getAutoJumpLogInfos();
            Log.d(tag, infos[1] + infos[2] + "= " + msg);
        }
    }

    public static void emvd(String tag, String msg) {
        if (DEBUG_D) {
            String[] infos = getAutoJumpLogInfos();
            Log.d("emv==" + tag, infos[1] + infos[2] + "= " + msg);
        }
    }

    public static void emvd(String msg) {
        if (DEBUG_D) {
            String[] infos = getAutoJumpLogInfos();
            Log.d("emv==", infos[1] + infos[2] + "= " + msg);
        }
    }

    /**
     * Output i level log, internally determine whether to actually output log according to the set switch
     *
     * @param tag Tag definition of android.util.log in the same system
     * @param msg Information to be output
     */
    public static void i(String tag, String msg) {
        if (DEBUG_I) {
            String[] infos = getAutoJumpLogInfos();
            Log.i(tag, infos[1] + infos[2] + "= " + msg);
        }
    }

    /**
     * Output w level log, internally determine whether to actually output log according to the set switch
     *
     * @param tag Tag definition of android.util.log in the same system
     * @param msg Information to be output
     */
    public static void w(String tag, String msg) {
        if (DEBUG_W) {
            String[] infos = getAutoJumpLogInfos();
            Log.w(tag, infos[1] + infos[2] + "= " + msg);
        }
    }

    /**
     * Output e-level log, internally determine whether to actually output log according to the set switch
     *
     * @param tag Tag definition of android.util.log in the same system
     * @param msg Information to be output
     */
    public static void e(String tag, String msg) {
        if (DEBUG_E) {
            String[] infos = getAutoJumpLogInfos();
            Log.e(tag, infos[1] + infos[2] + "= " + msg);
        }
    }

    /**
     * Get the method name, line number and other information where the printing information is located
     *
     * @return
     */
    private static String[] getAutoJumpLogInfos() {
        String[] infos = new String[]{"", "", ""};
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        if (elements.length < 5) {
            Log.e("AppLog", "Stack is too shallow!!!");
            return infos;
        } else {
            infos[0] = elements[4].getClassName().substring(elements[4].getClassName().lastIndexOf(".") + 1);
            infos[1] = elements[4].getMethodName() + "()";
            infos[2] = " at (" + elements[4].getClassName() + ".java:" + elements[4].getLineNumber() + ")";
            return infos;
        }
    }

}
