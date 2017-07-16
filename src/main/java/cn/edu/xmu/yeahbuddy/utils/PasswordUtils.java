package cn.edu.xmu.yeahbuddy.utils;

import org.jetbrains.annotations.Contract;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Random;

public final class PasswordUtils {

    private static final Random random = new SecureRandom();

    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;
    private static final SecretKeyFactory skf;

    static {
        try {
            skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        } catch (NoSuchAlgorithmException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private PasswordUtils() {
    }

    @Contract(pure = true)
    public static byte[] generateSalt() {
        return generateSalt(15);
    }

    @Contract(pure = true)
    public static byte[] generateSalt(int length) {
        byte[] salt = new byte[length];
        random.nextBytes(salt);
        return salt;
    }

    @Contract(pure = true)
    public static boolean isExpectedPassword(char[] password, byte[] salt, byte[] expectedHash) {
        byte[] pwdHash = hash(password, salt);
        Arrays.fill(password, Character.MIN_VALUE);
        return MessageDigest.isEqual(pwdHash, expectedHash);
    }

    @Contract(pure = true)
    public static byte[] hash(char[] password, byte[] salt) {
        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
        Arrays.fill(password, Character.MIN_VALUE);
        try {
            return skf.generateSecret(spec).getEncoded();
        } catch (InvalidKeySpecException e) {
            throw new AssertionError("Error while hashing a password", e);
        } finally {
            spec.clearPassword();
        }
    }
}
