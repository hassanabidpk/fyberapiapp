package com.hassanabid.fyberapiapp.data;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by hassanabid on 9/26/16.
 *  Reference : https://codebutchery.wordpress.com/2014/08/27/how-to-get-the-sha1-hash-sum-of-a-string-in-android/
 */

public class Utility {

    public static String SHA1(String url_address,String timestamp) {

        url_address = url_address + "/feed/v1/offers.json?appid=" + Constants.APP_ID + "&device_id="+Constants.DEVICE_ID
                + "&ip="+Constants.IP_ADDRESS + "&locale=" + Constants.locale + "&page=1"
                + "&ps_time=" + timestamp + "&pub0=campaign2&timestamp=" + timestamp + "&uid=" + Constants.UID
                + "&" + Constants.API_KEY;

        Log.d("Utility", "url_for_hash_key " + url_address);
        try {

            MessageDigest md;
            md = MessageDigest.getInstance("SHA-1");
            md.update(url_address.getBytes("UTF-8"),
                    0, url_address.length());
            byte[] sha1hash = md.digest();

            return toHex(sha1hash);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String toHex(byte[] buf) {

        if (buf == null) return "";

        int l = buf.length;
        StringBuffer result = new StringBuffer(2 * l);

        for (int i = 0; i < buf.length; i++) {
            appendHex(result, buf[i]);
        }

        return result.toString();

    }

    private final static String HEX = "0123456789ABCDEF";

    private static void appendHex(StringBuffer sb, byte b) {

        sb.append(HEX.charAt((b >> 4) & 0x0f))
                .append(HEX.charAt(b & 0x0f));

    }
}
