package com.sipe.mailSync.auth.presentation;

import com.sipe.mailSync.auth.application.AuthService;
import com.sipe.mailSync.auth.dto.LoginRequest;
import com.sipe.mailSync.auth.dto.RegisterRequest;
import com.sipe.mailSync.auth.dto.RegisterResponse;
import com.sipe.mailSync.security.dto.CustomUserDetail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
@Tag(name = "auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Login", description = "사용자 로그인 인증 후, 인증 성공시 토큰 발행")
    public void login(@RequestBody LoginRequest loginRequest) {
        // 이 메서드는 실제로 작동하지 않습니다.
        // UsernamePasswordAuthenticationFilter가 이 경로를 가로채어 인증을 처리합니다.
        throw new IllegalStateException("This method should not be called. It's handled by Spring Security filter chain.");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest body) {
        try {
            RegisterResponse response = authService.register(body);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetail userDetails = (CustomUserDetail) authentication.getPrincipal();
            return ResponseEntity.status(HttpStatus.OK).body(userDetails.getEmail());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}
