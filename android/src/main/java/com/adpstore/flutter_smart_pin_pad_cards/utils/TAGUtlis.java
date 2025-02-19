package com.adpstore.flutter_smart_pin_pad_cards.utils;

import com.topwise.toptool.api.convert.IConvert;
import com.topwise.toptool.impl.TopTool;

public class TAGUtlis {

    /**
     *
     * @param len
     * @return
     */
    public static byte[] genLen(int len) {
        byte[] ret;
        if (len <= 127) {
            ret = new byte[1];
            ret[0] = (byte) len;
            return ret;
        }

        int tmp = len;
        int b = 0;
        while (tmp != 0) {
            b++;
            tmp = (tmp >> 8);
        }

        ret = new byte[b + 1];
        ret[0] = (byte) (0x80 + b);
        byte[] lenBytes = new byte[4];

        TopTool.getInstance().getConvert().intToByteArray(len, lenBytes, 0, IConvert.EEndian.BIG_ENDIAN);
        System.arraycopy(lenBytes, 4 - b, ret, 1, b);

        return ret;
    }

    /**
     *
     * @param tag
     * @return
     */
    public static byte [] tagFromInt(int tag) {
        IConvert convert = TopTool.getInstance().getConvert();
        byte[] t = convert.intToByteArray(tag, IConvert.EEndian.BIG_ENDIAN);
        int realTagLen = t.length;
        for (int i = 0; i < t.length; i++) {
            if (t[i] == 0) {
                realTagLen--;
            }
        }
        byte[] ret = new byte[realTagLen];
        System.arraycopy(t, t.length - realTagLen, ret, 0, realTagLen);
        return ret;
    }
}
