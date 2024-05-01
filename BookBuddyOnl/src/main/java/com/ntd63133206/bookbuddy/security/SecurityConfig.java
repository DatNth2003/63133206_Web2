package com.ntd63133206.bookbuddy.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect;


@Configuration
@EnableWebSecurity
public class SecurityConfig {


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http.authorizeRequests()
//            .requestMatchers("/edit/**", "/delete/**").hasRole("ADMIN")
//            .anyRequest().authenticated()
//            .and()
//            .formLogin().permitAll()
//            .and()
//            .logout().permitAll()
//            .and()
//            .exceptionHandling().accessDeniedPage("/403");
//        return http.build();
    	
    	http
        .authorizeRequests()
        	.requestMatchers("/").permitAll()
            .requestMatchers("/css/**", "/js/**", "/images/**", "/bootstrap-5.2.3-dist/**").permitAll()
            .requestMatchers("/account/register").permitAll()
            .requestMatchers("/account/login").permitAll()
            .requestMatchers("/account/login?error").permitAll()
            .requestMatchers("/account/forgot-password").permitAll()
            .requestMatchers("/account/reset-password").permitAll()
            .requestMatchers("/account/profile").permitAll()

            .anyRequest().permitAll()
            .and()
        .formLogin()
        	.loginPage("/login")
        	.defaultSuccessUrl("/")
        	.failureUrl("/login?error")
        	.permitAll()
        .and()
        .logout()
            .permitAll(); // Cho phép truy cập vào trang logout mà không cần xác thực
    	return http.build();
    }
    
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
        	
            .inMemoryAuthentication()
                .withUser("user").password("{noop}password").roles("USER")
                .and()
                .withUser("admin").password("{noop}password").roles("ADMIN");
    }
    
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withUsername("user")
            .password("password")
            .roles("USER")
            .build();

        return new InMemoryUserDetailsManager(user);
    }
    @Bean
    public SpringSecurityDialect springSecurityDialect() {
        return new SpringSecurityDialect();
    }
    
}
