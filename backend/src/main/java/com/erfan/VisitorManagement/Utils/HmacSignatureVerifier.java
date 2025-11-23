package com.erfan.VisitorManagement.Utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class HmacSignatureVerifier {
    public static boolean verifySha256(String appSecret, String payload, String header) {
        try {
            if (header == null || !header.startsWith("sha256=")) return false;
            String expected = header.substring("sha256=".length());
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(appSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] digest = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            String computed = sb.toString();
            return computed.equalsIgnoreCase(expected);
        } catch (Exception e) {
            return false;
        }
    }
}
