package com.alkemy.marvel.client.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

@Component
public class MarvelAuthUtils {
    
    public String generateHash(String timestamp, String privateKey, String publicKey) {
        String stringToHash = timestamp + privateKey + publicKey;
        return DigestUtils.md5Hex(stringToHash);
    }
    
    public String generateTimestamp() {
        return String.valueOf(System.currentTimeMillis());
    }
}