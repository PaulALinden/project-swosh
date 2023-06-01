package org.example.database;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class PasswordCrypt {

    private static final int SALT_LENGTH = 16;

    public static String Encrypt(String password){

        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            digest.update(salt);

            byte[] hashedPassword = digest.digest(password.getBytes());

            return byteArrayToHexString(hashedPassword) + byteArrayToHexString(salt);
        }catch(NoSuchAlgorithmException e){
            return null;
        }
    }

    public static boolean Verify(String password, String hashedPassword) {

        String passwordHash = hashedPassword.substring(0, 64);
        String saltHex = hashedPassword.substring(64);

        byte[] salt = hexStringToByteArray(saltHex);

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            digest.update(salt);

            byte[] hashedInputPassword = digest.digest(password.getBytes());

            String hashedInputPasswordHex = byteArrayToHexString(hashedInputPassword);
            return passwordHash.equals(hashedInputPasswordHex);
        }
        catch(NoSuchAlgorithmException e){
            return false;
        }
    }

    private static String byteArrayToHexString(byte[] array) {
        StringBuilder sb = new StringBuilder();
        for (byte b : array) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
}
