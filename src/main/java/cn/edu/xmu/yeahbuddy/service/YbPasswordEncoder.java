package cn.edu.xmu.yeahbuddy.service;

import cn.edu.xmu.yeahbuddy.utils.PasswordUtils;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Base64;

public class YbPasswordEncoder implements PasswordEncoder {
    @Override
    public String encode(CharSequence rawPassword) {
        byte[] salt = PasswordUtils.generateSalt();
        byte[] hash = PasswordUtils.hash(rawPassword.toString().toCharArray(), salt);
        Base64.Encoder base64e = Base64.getEncoder();
        return base64e.encodeToString(salt) + "$" + base64e.encodeToString(hash);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        String salt = encodedPassword.split("\\$", 2)[0];
        String hash = encodedPassword.split("\\$", 2)[1];
        Base64.Decoder base64d  = Base64.getDecoder();

        return PasswordUtils.isExpectedPassword(rawPassword.toString().toCharArray(),
                base64d.decode(salt),
                base64d.decode(hash)
        );
    }
}
