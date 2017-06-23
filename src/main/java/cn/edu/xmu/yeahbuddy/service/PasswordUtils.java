package cn.edu.xmu.yeahbuddy.service;

import org.springframework.stereotype.Service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

@Service
public final class PasswordUtils {

    private static final Random random = new SecureRandom();

    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;

    public static String generateSalt(){
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static byte[] hash(char[] password, String salt) {
        return hash(password, Base64.getDecoder().decode(salt));
    }

    private static byte[] hash(char[] password, byte[] salt) {
        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
        Arrays.fill(password, Character.MIN_VALUE);
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new AssertionError("Error while hashing a password: " + e.getMessage(), e);
        } finally {
            spec.clearPassword();
        }
    }

    public static boolean isExpectedPassword(char[] password, String salt, byte[] expectedHash) {
        return isExpectedPassword(password, Base64.getDecoder().decode(salt), expectedHash);
    }

    private static boolean isExpectedPassword(char[] password, byte[] salt, byte[] expectedHash) {
        byte[] pwdHash = hash(password, salt);
        Arrays.fill(password, Character.MIN_VALUE);
        return MessageDigest.isEqual(pwdHash, expectedHash);
    }
}
