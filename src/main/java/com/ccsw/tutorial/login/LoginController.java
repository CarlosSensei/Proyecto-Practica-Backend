package com.ccsw.tutorial.login;

import com.ccsw.tutorial.login.model.LoginDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class LoginController {

    private final JwtUtil jwtUtil;

    public LoginController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDto dto) {

        if ("admin".equals(dto.getUser()) && "admin".equals(dto.getPassword())) {

            String token = jwtUtil.generateToken(dto.getUser());
            return ResponseEntity.ok(token);
        }

        throw new IllegalArgumentException("Logged in as a Basic User");
    }
}