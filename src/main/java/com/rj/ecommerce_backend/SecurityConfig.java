package com.rj.ecommerce_backend;

import com.rj.ecommerce_backend.domain.user.UserDetailsImpl;
import com.rj.ecommerce_backend.domain.user.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/user/**").hasRole("USER")
                        .requestMatchers("/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(formLogin -> formLogin
                        .usernameParameter("email")
                        .permitAll() // Allow all to access the login URL
                );
//                .formLogin(form -> form
////                        .loginPage("/login")
//                        .usernameParameter("email")
//                        .permitAll()
//                )
//                .logout(LogoutConfigurer::permitAll
//                );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return userEmail -> userRepository.findUserByEmail(userEmail)
                .map(UserDetailsImpl::build)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + userEmail));
    }
}
