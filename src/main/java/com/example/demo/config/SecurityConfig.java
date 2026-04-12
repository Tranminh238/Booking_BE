package com.example.demo.config;

import com.example.demo.service.UserDetailSevicesImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final UserDetailSevicesImpl userDetailServices;

    /**
     * Bean AuthenticationProvider
     * Chịu trách nhiệm load user từ DB và verify password
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailServices);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * Bean AuthenticationManager
     * Quản lý quá trình authentication
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Bean PasswordEncoder
     * Sử dụng BCrypt để mã hóa password
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Bean SecurityFilterChain
     * Cấu hình chính cho Spring Security
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF (vì sử dụng JWT - stateless)
                .csrf(AbstractHttpConfigurer::disable)

                // Enable CORS với cấu hình custom
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Cấu hình session management - STATELESS vì dùng JWT
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/auth/login",          
                                "/auth/login-partner",       
                                "/auth/register",       
                                "/client/register",
                                "/client/register-partner",    
                                "/api/hotel/**",
                                "/api/room/**",
                                "/api/amenities/**"     
                        ).permitAll()

                        // Tất cả các request còn lại cần authentication
                        .anyRequest().authenticated()
                )

                // Thêm custom authentication provider
                .authenticationProvider(authenticationProvider())

                // Thêm JWT filter trước UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Bean CORS Configuration
     * Cấu hình CORS cho phép gọi API từ frontend
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Cho phép tất cả origins (trong production nên chỉ định cụ thể)
        configuration.setAllowedOrigins(Arrays.asList("*"));

        // Cho phép các HTTP methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // Cho phép tất cả headers
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // Không cho phép credentials khi allowedOrigins là "*"
        configuration.setAllowCredentials(false);

        // Expose headers (cho phép client đọc các headers này)
        configuration.setExposedHeaders(Arrays.asList("Authorization"));

        // Áp dụng cấu hình cho tất cả các đường dẫn
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}