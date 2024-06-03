package com.ntd63133206.bookbuddy.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.ntd63133206.bookbuddy.service.UserService;




@Configuration
public class SecurityConfig {


	@Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
	}
	@Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/", "/css/**", "/js/**", "/images/**", "/pdfs/**").permitAll()
                .requestMatchers("/account/register/**", "/account/login/**", "/books/**").permitAll()
                .requestMatchers("/account/forgot-password/**", "/account/reset-password/**").permitAll()
                .requestMatchers("/purchases/**").authenticated()
                .requestMatchers("/account/profile").authenticated()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().permitAll()
            )
            .formLogin(formLogin -> formLogin
                .loginPage("/account/login")
                .loginProcessingUrl("/account/login")
                .usernameParameter("email")
                .defaultSuccessUrl("/", true)
                .failureUrl("/account/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/account/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
            )
            .csrf(csrf -> csrf.disable());
        return http.build();
    }
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    
}
