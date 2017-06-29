package cn.edu.xmu.yeahbuddy.config;

import cn.edu.xmu.yeahbuddy.service.AdministratorService;
import cn.edu.xmu.yeahbuddy.service.TeamService;
import cn.edu.xmu.yeahbuddy.service.YbPasswordEncodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Order(2)
    @Configuration
    public static class AdministratorSecurityConfig extends WebSecurityConfigurerAdapter {
        private final AdministratorService administratorService;

        @Autowired
        public AdministratorSecurityConfig(AdministratorService administratorService) {
            this.administratorService = administratorService;
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests().antMatchers("/webjars/**").permitAll();
            http
                    .authorizeRequests()
                    .anyRequest()
                    .authenticated()
                    // log in
                    .and()
                    .formLogin()
                    .loginPage("/login")
                    .permitAll()
                    .failureUrl("/login-error")
                    .defaultSuccessUrl("/admin")
                    // logout
                    .and().logout().logoutUrl("/**/logout")
                    .permitAll()
                    .logoutSuccessUrl("/login").deleteCookies("JSESSIONID").and()
                    .csrf()
                    .disable();
        }

        @Override
        public void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(administratorService).passwordEncoder(new YbPasswordEncodeService());
        }
    }

    @Order(1)
    @Configuration
    public static class TeamSecurityConfig extends WebSecurityConfigurerAdapter {

        private final TeamService teamService;

        @Autowired
        public TeamSecurityConfig(TeamService teamService) {
            this.teamService = teamService;
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .antMatcher("/team/**")
                    .authorizeRequests()
                    .anyRequest()
                    .authenticated()
                    // log in
                    .and()
                    .formLogin()
                    .loginPage("/team/login")
                    .permitAll()
                    .failureUrl("/team/login-error")
                    .defaultSuccessUrl("/team")
                    // logout
                    .and().logout().logoutUrl("/team/**/logout")
                    .permitAll()
                    .logoutSuccessUrl("/team/login").deleteCookies("JSESSIONID").and()
                    .csrf()
                    .disable();
        }

        @Override
        public void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(teamService).passwordEncoder(new YbPasswordEncodeService());
        }
    }
}