package com.example.fabricmanagement.config;

import com.example.fabricmanagement.service.JwtService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }


   @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
//       System.out.println("Incoming request: " + request.getMethod() + " " + request.getRequestURI());
//       // 打印请求头
//       System.out.println("Request Headers:");
//       request.getHeaderNames().asIterator().forEachRemaining(headerName -> {
//           System.out.println(headerName + ": " + request.getHeader(headerName));
//       });
//
//       // 打印请求参数
//       System.out.println("Request Parameters:");
//       request.getParameterMap().forEach((key, value) -> {
//           System.out.println(key + ": " + String.join(", ", value));
//       });


       String authHeader = request.getHeader("Authorization");
       System.out.println("Authorization Header: " + authHeader);
       // 检查是否有 Authorization Header
       if (authHeader == null || !authHeader.startsWith("Bearer ")) {
           System.out.println("Authorization header is missing or does not start with 'Bearer '");
           filterChain.doFilter(request, response);
           return;
       }
       String token = authHeader.substring(7); // 去掉 "Bearer " 前缀
       System.out.println("Extracted Token: " + token);

       try {
           // 从 Token 中提取用户名
           String username = jwtService.extractUsername(token);
           System.out.println("Extracted Username: " + username);

           // 检查是否已经设置了认证信息
           if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
               // 验证 Token 并获取认证信息
               var authentication = jwtService.getAuthentication(token, request);
               if (authentication != null) {
                   System.out.println("Authentication successful for user: " + username);
                   SecurityContextHolder.getContext().setAuthentication(authentication);
               } else {
                   System.out.println("Authentication failed for token: " + token);
               }
           }
       } catch (Exception e) {
           // 捕获异常并打印错误信息
           System.out.println("JWT validation failed: " + e.getMessage());
       }

       // 继续过滤链
       filterChain.doFilter(request, response);
   }
}