package com.github.leoluheng.blog.service;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

public class EncryptUtil {

    //Base64
    public static String base64Encode(String data) {
        return Base64.encodeBase64String(data.getBytes());
    }

    public static String base64Encode(byte[] bytes) {
        return Base64.encodeBase64String(bytes);
    }

    public static byte[] base64Decode(String data) {
        return Base64.decodeBase64(data.getBytes());
    }

    //sha1
    public static String sha1(String data) {

        return DigestUtils.sha1Hex(data);
    }

    //sha256Hex
    public static String sha256Hex(String data) {

        return DigestUtils.sha256Hex(data);
    }
}
