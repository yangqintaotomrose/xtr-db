package com.xtr.framework.utils;


import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class AESLOGINUtil
{
    private static final String KEY = "1234567890000000";
    private static final String IV = "1234567890000000";

    public static String encrypt(String data, String key, String iv)
            throws Exception
    {
        try
        {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            int blockSize = cipher.getBlockSize();
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            int plaintextLength = dataBytes.length;
            if (plaintextLength % blockSize != 0) {
                plaintextLength += blockSize - plaintextLength % blockSize;
            }

            byte[] plaintext = new byte[plaintextLength];
            System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);

            SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
            IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());
            cipher.init(1, keyspec, ivspec);
            byte[] encrypted = cipher.doFinal(plaintext);

            return new Base64().encodeToString(encrypted);
        }
        catch (Exception e) {
            e.printStackTrace();
        }return null;
    }

    public static String desEncrypt(String data, String key, String iv)
            throws Exception
    {
        try
        {
            byte[] encrypted1 = new Base64().decode(data);
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
            IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());
            cipher.init(2, keyspec, ivspec);
            byte[] original = cipher.doFinal(encrypted1);

            String originalString = new String(original, StandardCharsets.UTF_8);
            return originalString;
        } catch (Exception e) {
            e.printStackTrace();
        }return null;
    }

    public static String encrypt(String data)
            throws Exception
    {
        return encrypt(data, KEY, IV);
    }

    public static String desEncrypt(String data)
            throws Exception
    {
        return desEncrypt(data, KEY, IV);
    }

    public static void main(String[] args)
            throws Exception
    {
        String key = "1234567890000000";
        String iv = "1234567890000000";
        String s = "baiquxin,baiqqqqq,13909910000,991";
        System.out.println("encrypt==" + encrypt(s, key, iv));
        System.out.println("desEncrypt==" + desEncrypt(encrypt(s, key, iv)));

        System.out.println("desEncrypt==" + desEncrypt("Ka9whQbMu9RHGl3h2CgjAYWLeWcohivtq3ZB8S1vlI9dTA0Vezfdsg0AX3h0A6kn"));
    }
}
