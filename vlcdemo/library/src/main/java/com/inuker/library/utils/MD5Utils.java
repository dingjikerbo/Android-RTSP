package com.inuker.library.utils;

import java.security.MessageDigest;

/**
 * Created by liwentian on 2017/10/12.
 */

public class MD5Utils {

    public static String getMD5(String val) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(val.getBytes());
            byte[] m = md5.digest();
            return getString(m);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return val;
    }

    private static String getString(byte[] b) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            sb.append(String.format("%02X", (int) (b[i] & 0xff)));
        }
        return sb.toString();
    }
}
