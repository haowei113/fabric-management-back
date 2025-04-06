package com.example.fabricmanagement.config;

import com.example.fabricmanagement.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;



@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // 使用新的方式禁用 CSRF
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS).permitAll() // 允许所有 OPTIONS 请求
                        .requestMatchers("/uploads/**", "/css/**", "/js/**").permitAll() // 允许访问静态资源
                        .requestMatchers("/api/auth/**").permitAll() // 允许这些接口无需认证

                        // 允许所有认证用户访问 /api/user 接口
                        .requestMatchers(HttpMethod.GET, "/api/user").authenticated()
                          // 收藏相关接口对所有已认证用户开放
                         .requestMatchers(HttpMethod.POST, "/api/fabrics/*/favorite").authenticated()
                         .requestMatchers(HttpMethod.DELETE, "/api/fabrics/*/favorite").authenticated()
                         .requestMatchers(HttpMethod.GET, "/api/fabrics/favorites").authenticated()
                        // 材质、颜色、供应商列表接口对所有已认证用户开放
                         .requestMatchers(HttpMethod.GET, "/api/materials").authenticated()
                         .requestMatchers(HttpMethod.GET, "/api/colors").authenticated()
                         .requestMatchers(HttpMethod.GET, "/api/suppliers").authenticated()

                         .requestMatchers(HttpMethod.GET, "/api/fabrics/**").permitAll() // 所有人可查看
                        .requestMatchers(HttpMethod.POST, "/api/fabrics/**").hasAuthority("supplier")
//                        .requestMatchers(HttpMethod.POST, "/api/fabrics/test").hasAuthority("supplier")
                        .requestMatchers(HttpMethod.PUT, "/api/fabrics/**").hasAuthority("supplier")
                        .requestMatchers(HttpMethod.DELETE, "/api/fabrics/**").hasAuthority("supplier")

                        .anyRequest().authenticated() // 其他请求需要认证
                )
                .cors(cors -> cors.disable()) // 使用 CorsConfig 中的配置，无需在这里重复配置
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // 添加 JWT 过滤器
                .formLogin(form -> form.disable()); // 禁用默认的登录表单

        return http.build();
    }
}