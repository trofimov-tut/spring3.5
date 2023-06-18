package com.trofimov.sequrity.spring32.configs;

import com.trofimov.sequrity.spring32.service.UserDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final SuccessUserHandler successUserHandler;

    private final UserDetailService userDetailsService;

    public WebSecurityConfig(UserDetailService userDetailsService, SuccessUserHandler successUserHandler) {
        this.successUserHandler = successUserHandler;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/login", "/users/add").permitAll()
                .antMatchers("/users/lk").hasAnyRole("USER", "ADMIN")
                .antMatchers("/users/**").hasRole("ADMIN")
                .antMatchers("/admin").hasRole("ADMIN")
                .anyRequest().permitAll()
                .and()
                .formLogin().successHandler(successUserHandler)
                .permitAll()
                .and().
                logout().logoutUrl("/logout").logoutSuccessUrl("/");
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(getPasswordEncoder());
    }

}