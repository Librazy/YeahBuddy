package cn.edu.xmu.yeahbuddy.config;

import cn.edu.xmu.yeahbuddy.service.AdministratorService;
import cn.edu.xmu.yeahbuddy.service.YbPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter{

    private final AdministratorService administratorService;

    @Autowired
    public SecurityConfig(AdministratorService administratorService) {
        this.administratorService = administratorService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
            .anyRequest()
            .authenticated()
            // log in
            .and()
            .formLogin()
            .loginPage("/login")
            .permitAll()
            .loginProcessingUrl("/doLogin")
            .permitAll()
            .failureUrl("/login?error=loginError")
            .defaultSuccessUrl("/postLogin")
            // logout
            .and().logout().logoutUrl("/**/logout")
            .permitAll()
            .logoutSuccessUrl("/login").deleteCookies("JSESSIONID").and()
            .csrf()
            .disable();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(administratorService).passwordEncoder(new YbPasswordEncoder());
    }
}