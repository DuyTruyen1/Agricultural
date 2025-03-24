package com.agricultural.agricultural.config;

import com.agricultural.agricultural.filters.JwtTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class WebSecurityConfig {
    private final JwtTokenFilter jwtTokenFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        return http
//                .csrf(AbstractHttpConfigurer::disable) // Tắt CSRF để test API
//                .authorizeHttpRequests(requests -> requests
//                        // Cho phép mọi người truy cập vào login và register
//                        .requestMatchers("/api/users/login", "/api/users/register").permitAll()
//
//                        // Phân quyền: cho phép ADMIN truy cập vào các API dưới đường dẫn "/admin/**"
//                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
//
//                        // Phân quyền: cho phép USER và ADMIN truy cập vào các API dưới đường dẫn "/user/**"
//                        .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN")
//
//                        // Các API khác yêu cầu xác thực người dùng
//                        .anyRequest().authenticated()
//                )
//                .build();

//        return http
//                .csrf(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(requests -> requests
//                        .requestMatchers("/**").permitAll() // ✅ Cho phép tất cả request (Tạm thời để test)
//                )
//                .build();

        return http
                .csrf(AbstractHttpConfigurer::disable) // Tắt CSRF để test API
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Không dùng session
                .authorizeHttpRequests(requests -> requests
                                .requestMatchers("/api/users/login", "/api/users/register").permitAll() // ✅ Cho phép không cần token
//                        .requestMatchers("/api/forum/**").authenticated() // ✅ Yêu cầu đăng nhập với API forum
                                .requestMatchers("/api/orders/**").authenticated() // Yêu cầu xác thực cho API orders
                                .requestMatchers("/api/v1/forum/reply/**").permitAll()

                                .anyRequest().permitAll()
                )
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class) // ✅ Thêm filter kiểm tra JWT
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173")); // React app URL
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
