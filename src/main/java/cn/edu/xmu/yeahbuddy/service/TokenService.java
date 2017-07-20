package cn.edu.xmu.yeahbuddy.service;

import cn.edu.xmu.yeahbuddy.domain.Review;
import cn.edu.xmu.yeahbuddy.domain.Token;
import cn.edu.xmu.yeahbuddy.domain.Tutor;
import cn.edu.xmu.yeahbuddy.domain.repo.TokenRepository;
import cn.edu.xmu.yeahbuddy.utils.PasswordUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NonNls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * 导师登录Token服务
 */
@Service
public class TokenService {

    @NonNls
    private static Log log = LogFactory.getLog(TokenService.class);

    private final TokenRepository tokenRepository;

    private final TutorService tutorService;

    /**
     * 构造函数
     * Spring Boot自动装配
     *
     * @param tokenRepository Autowired
     * @param tutorService    Autowired
     */
    @Autowired
    public TokenService(TokenRepository tokenRepository, TutorService tutorService) {
        this.tokenRepository = tokenRepository;
        this.tutorService = tutorService;
    }

    /**
     * 查找所有Token
     *
     * @return 所有Token
     */
    @Transactional(readOnly = true)
    public List<Token> findAllTokens() {
        return tokenRepository.findAll();
    }

    /**
     * 查找所有失效的Token
     *
     * @return 所有失效的Token
     */
    @Transactional(readOnly = true)
    public List<Token> findByRevokedIsTrue() {return tokenRepository.findByRevokedIsTrue(); }

    /**
     * 查找所有未失效的Token
     *
     * @return 所有未失效的Token
     */
    @Transactional(readOnly = true)
    public List<Token> findByRevokedIsFalse() {return tokenRepository.findByRevokedIsFalse(); }

    /**
     * 按登录Token值查找并验证导师与Token
     *
     * @param tokenStr 查找的登录Token值
     * @return 导师与Token
     * @throws UsernameNotFoundException 找不到Token
     * @throws BadCredentialsException   Token已经被吊销
     */
    @Transactional(readOnly = true)
    public Pair<Tutor, Token> loadAndValidateToken(@NonNls String tokenStr) throws UsernameNotFoundException, BadCredentialsException {
        log.debug("Trying to load Token " + tokenStr);
        Optional<Token> tok = tokenRepository.findById(tokenStr);
        if (!tok.isPresent()) {
            log.info("Failed to load Token " + tokenStr + ": not found");
            throw new UsernameNotFoundException(tokenStr);
        }
        Token token = tok.get();
        if (token.isRevoked()) {
            log.info("Failed to load Token " + tokenStr + ": revoked");
            throw new BadCredentialsException(tokenStr);
        }
        log.info("Loaded Token " + tokenStr + ", loading Tutor " + token.getTutorId());
        Tutor tutor = tutorService.loadById(token.getTutorId());
        return Pair.of(tutor, token);
    }


    /**
     * 创建Token
     *
     * @param tutor   导师
     * @param reviews 待填写的评议
     * @return Token值
     */
    @Transactional
    @PreAuthorize("hasAuthority('ManageTutor')")
    public Token createToken(Tutor tutor, Collection<Review> reviews, Timestamp end) {
        String tokenValue = Base64.getUrlEncoder().encodeToString(PasswordUtils.generateSalt(18));
        while (!tokenValue.matches("[a-zA-Z0-9]+")) {
            tokenValue = Base64.getUrlEncoder().encodeToString(PasswordUtils.generateSalt(18));
        }
        Token result = tokenRepository.save(new Token(tokenValue, tutor, reviews, end));
        log.debug("Created Token " + result);
        return result;
    }

    /**
     * 吊销Token
     *
     * @param token 登录Token
     */
    @Transactional
    public void revokeToken(Token token) {
        token.setRevoked();
        tokenRepository.save(token);
    }

}
