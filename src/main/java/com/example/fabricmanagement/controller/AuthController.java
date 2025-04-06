package com.example.fabricmanagement.controller;

import com.example.fabricmanagement.dto.LoginRequest;
import com.example.fabricmanagement.dto.LoginResponse;
import com.example.fabricmanagement.dto.UserRegistrationRequest; // 更新导入路径
import com.example.fabricmanagement.model.User;
import com.example.fabricmanagement.service.JwtService;
import com.example.fabricmanagement.service.UserService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationRequest request) {
        // 检查用户名和邮箱是否已存在

        if (userService.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        // 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setEmail(request.getEmail());
        user.setUserType(request.getUserType());
        user.setPhone(request.getPhone());

        // 如果是供应商，传递供应商信息
        User registeredUser = userService.registerUser(user, request.getSupplierName(), request.getDescription());
        return ResponseEntity.ok(registeredUser);
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<User> userOptional = userService.findByUsername(request.getUsername());
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }

        User user = userOptional.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }

        // 生成 JWT Token
        String token = jwtService.generateToken(user.getUsername(), user.getUserType());
        System.out.println("Token: " + token);
        System.out.println("UserType: " + user.getUserType());

        return ResponseEntity.ok(new LoginResponse(token, user.getUserType()));
    }
    @GetMapping("/check-login")
    public ResponseEntity<String> checkLogin(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.ok("当前用户未登录");
        }

        String token = authHeader.substring(7);
        try {
            String username = jwtService.extractUsername(token);
            Claims claims = jwtService.getClaims(token);
            String userType = claims.get("userType", String.class);

            return ResponseEntity.ok("当前用户已登录，用户名：" + username + "\n权限：" + userType);
        } catch (Exception e) {
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body("Token 无效或已过期");
        }
    }
}