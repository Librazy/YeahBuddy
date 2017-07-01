package cn.edu.xmu.yeahbuddy.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.Contract;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class AuthTokenFilter extends AbstractAuthenticationProcessingFilter {

    private static Log log = LogFactory.getLog(AuthTokenFilter.class);

    AuthTokenFilter(String defaultFilterProcessesUrl, String defaultTargetUrl, AuthenticationManager authenticationManager) {
        super(defaultFilterProcessesUrl);
        super.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(defaultFilterProcessesUrl));
        setAuthenticationManager(authenticationManager);
        setAuthenticationSuccessHandler(new SimpleUrlAuthenticationSuccessHandler(defaultTargetUrl) {
            @Override
            public boolean isAlwaysUseDefaultTargetUrl() {
                return true;
            }
        });
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        Map<String, String[]> params = request.getParameterMap();
        log.debug("Authentication recevied at " + request.getRequestURI());
        if (!params.isEmpty() && params.containsKey("auth_token")) {
            String token = params.get("auth_token")[0];
            if (token != null) {
                log.debug("Authenticating token " + token);
                return getAuthenticationManager().authenticate(new TokenAuthentication(token));
            }
        }
        log.info("No auth_token found on " + request.toString());
        throw new BadCredentialsException("auth_token");
    }

    class TokenAuthentication implements Authentication {

        private static final long serialVersionUID = 443170173055281706L;

        private String token;

        private TokenAuthentication(String token) {
            this.token = token;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return new ArrayList<>(0);
        }

        @Override
        public Object getCredentials() {
            return token;
        }

        @Override
        public Object getDetails() {
            return null;
        }

        @Override
        public Object getPrincipal() {
            return null;
        }

        @Override
        public boolean isAuthenticated() {
            return false;
        }

        @Contract("true -> fail")
        @Override
        public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
            if (isAuthenticated) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public String getName() {
            return token;
        }
    }

}