package cn.edu.xmu.yeahbuddy.service;

import cn.edu.xmu.yeahbuddy.domain.Token;
import cn.edu.xmu.yeahbuddy.domain.Tutor;
import cn.edu.xmu.yeahbuddy.domain.repo.TokenRepository;
import cn.edu.xmu.yeahbuddy.utils.PasswordUtils;
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

@Service
public class TokenService {

    private final TokenRepository tokenRepository;

    private final TutorService tutorService;

    @Autowired
    public TokenService(TokenRepository tokenRepository, TutorService tutorService) {
        this.tokenRepository = tokenRepository;
        this.tutorService = tutorService;
    }

    @Transactional(readOnly = true)
    public Pair<Tutor, Token> loadToken(String tokenStr) throws UsernameNotFoundException {
        Optional<Token> tok = tokenRepository.findById(tokenStr);
        if (!tok.isPresent()) {
            throw new UsernameNotFoundException(tokenStr);
        }
        try {
            Token token = tok.get();
            if(token.isRevoked()){
                throw new BadCredentialsException(tokenStr);
            }
            Tutor tutor = tutorService.loadTutorById(token.getTutorId());
            return Pair.of(tutor, token);
        } catch (Exception e){
            throw new UsernameNotFoundException(tokenStr, e);
        }
    }

    @Transactional
    @PreAuthorize("hasAuthority('RegisterTutor')")
    public String createToken(Tutor tutor, int stage, Collection<Integer> teamIds){
        String tokenValue = Base64.getUrlEncoder().encodeToString(PasswordUtils.generateSalt(18));
        tokenRepository.saveAndFlush(new Token(tokenValue, tutor.getId(), stage, teamIds));
        return tokenValue;
    }
}
