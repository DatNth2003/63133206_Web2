package com.ntd63133206.bookbuddy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
	@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
	}
	@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .requestMatchers("/").permitAll()
                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers("/account/register/**").permitAll()
                .requestMatchers("/account/login/**").permitAll()
                .requestMatchers("/account/forgot-password/**").permitAll()
                .requestMatchers("/account/reset-password/**").permitAll()
                .requestMatchers("/account/profile").permitAll()
                .requestMatchers("register","forgotPassword","resetPassword/**", "resetPassword").permitAll()
                .requestMatchers("/account/profile").authenticated()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().permitAll()
            .and()
            .formLogin(formLogin ->
            formLogin
                .loginPage("/account/login")
                .defaultSuccessUrl("/",true)
                .permitAll()
        )
        .logout(logout ->
            logout
                .logoutUrl("/account/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
        ).csrf().disable();
        return http.build();
    }
}
