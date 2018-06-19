package com.github.leoluheng.blog.utility;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class Encodes {

    public static String urlDecode(String str) {
        return urlDecode(str, "UTF-8");
    }

    public static String urlDecode(String str, String encode) {
        try {
            return URLDecoder.decode(str, encode);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String urlEncode(String str) {
        return urlEncode(str, null);
    }

    public static String urlEncode(String str, String encode) {
        try {
            return URLEncoder.encode(str, encode);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }


}
