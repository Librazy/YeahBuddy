package cn.edu.xmu.yeahbuddy.service;

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

import java.util.Base64;
import java.util.Collection;
import java.util.Optional;

/**
 * 导师登录Token服务
 */
@Service
public class TokenService {

    private static Log log = LogFactory.getLog(TokenService.class);

    private final TokenRepository tokenRepository;

    private final TutorService tutorService;

    /**
     * @param tokenRepository Autowired
     * @param tutorService    Autowired
     */
    @Autowired
    public TokenService(TokenRepository tokenRepository, TutorService tutorService) {
        this.tokenRepository = tokenRepository;
        this.tutorService = tutorService;
    }


    /**
     * 按登录Token值查找导师与Token
     *
     * @param tokenStr 查找的登录Token值
     * @return 导师与Token
     * @throws UsernameNotFoundException 找不到Token
     */
    @Transactional(readOnly = true)
    public Pair<Tutor, Token> loadToken(@NonNls String tokenStr) throws UsernameNotFoundException {
        log.debug("Trying to load Token " + tokenStr);
        Optional<Token> tok = tokenRepository.findById(tokenStr);
        if (!tok.isPresent()) {
            log.info("Failed to load Token " + tokenStr + ": not found");
            throw new UsernameNotFoundException(tokenStr);
        }
        try {
            Token token = tok.get();
            if (token.isRevoked()) {
                log.info("Failed to load Token " + tokenStr + ": revoked");
                throw new BadCredentialsException(tokenStr);
            }
            log.info("Loaded Token " + tokenStr + ", loading Tutor " + token.getTutorId());
            Tutor tutor = tutorService.loadTutorById(token.getTutorId());
            return Pair.of(tutor, token);
        } catch (Exception e) {
            log.info("Failed to load Token " + tokenStr + ": " + e.getMessage(), e);
            throw new UsernameNotFoundException(tokenStr, e);
        }
    }


    /**
     * 创建Token
     *
     * @param tutor   导师
     * @param stage   阶段
     * @param teamIds 待评价的团队ID
     * @return Token值
     */
    @Transactional
    @PreAuthorize("hasAuthority('RegisterTutor')")
    public String createToken(Tutor tutor, int stage, Collection<Integer> teamIds) {
        String tokenValue = Base64.getUrlEncoder().encodeToString(PasswordUtils.generateSalt(18));
        while(!tokenValue.matches("[a-zA-Z0-9]+")){
            tokenValue = Base64.getUrlEncoder().encodeToString(PasswordUtils.generateSalt(18));
        }
        tokenRepository.save(new Token(tokenValue, tutor.getId(), stage, teamIds));
        return tokenValue;
    }
}
