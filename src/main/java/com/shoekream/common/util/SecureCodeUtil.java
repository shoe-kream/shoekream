package com.shoekream.common.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.UUID;

public class SecureCodeUtil {
    public static String getCertificationNumber() throws NoSuchAlgorithmException {
        String result;

        do {
            int i = SecureRandom.getInstanceStrong().nextInt(999999);
            result = String.valueOf(i);
        } while (result.length() != 6);

        return result;
    }

    public static String getTempPassword() throws NoSuchAlgorithmException {
        return SecureCodeUtil.getCertificationNumber() + UUID.randomUUID().toString().substring(0, 8);
    }
}
