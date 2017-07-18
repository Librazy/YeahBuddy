package cn.edu.xmu.yeahbuddy.service;

import cn.edu.xmu.yeahbuddy.utils.PasswordUtils;
import org.jetbrains.annotations.Contract;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Base64;

/**
 * 密码编码服务
 */
@Service
public class YbPasswordEncodeService implements PasswordEncoder {

    /**
     * 编码密码
     *
     * @param rawPassword 密码明文
     * @return 编码后的密码密文
     */
    @Override
    @Contract(pure = true)
    public String encode(CharSequence rawPassword) {
        byte[] salt = PasswordUtils.generateSalt();
        byte[] hash = PasswordUtils.hash(rawPassword.toString().toCharArray(), salt);
        Base64.Encoder base64e = Base64.getEncoder();
        return String.format("%s$%s", base64e.encodeToString(salt), base64e.encodeToString(hash));
    }

    /**
     * 匹配密码
     *
     * @param rawPassword     待匹配明文
     * @param encodedPassword 编码后的密码密文
     * @return 是否匹配
     */
    @Override
    @Contract(pure = true)
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        String[] s = encodedPassword.split("\\$", 2);
        Base64.Decoder base64d = Base64.getDecoder();

        return PasswordUtils.isExpectedPassword(rawPassword.toString().toCharArray(),
                base64d.decode(s[0]),
                base64d.decode(s[1])
        );
    }
}
