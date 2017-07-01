package cn.edu.xmu.yeahbuddy.config;

import cn.edu.xmu.yeahbuddy.domain.Token;
import cn.edu.xmu.yeahbuddy.domain.Tutor;
import cn.edu.xmu.yeahbuddy.service.TokenService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthTokenAuthenticationProvider implements AuthenticationProvider {

    private static Log log = LogFactory.getLog(PreAuthenticatedAuthenticationProvider.class);

    private final TokenService tokenService;

    @Autowired
    public AuthTokenAuthenticationProvider(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.info("Trying to authenticate Token " + authentication.getCredentials());
        final Pair<Tutor, Token> tutorTokenPair = tokenService.loadToken(authentication.getCredentials().toString());
        log.info("Loaded Token " + authentication.getCredentials() + " for " + tutorTokenPair.getFirst().toString());
        return new PreAuthenticatedAuthenticationToken(tutorTokenPair.getFirst(), tutorTokenPair.getSecond());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return AuthTokenFilter.TokenAuthentication.class.isAssignableFrom(authentication);
    }
}
