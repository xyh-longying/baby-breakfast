package com.babybreakfast.auth;

import com.babybreakfast.auth.dto.AuthResponse;
import com.babybreakfast.auth.dto.LoginRequest;
import com.babybreakfast.auth.dto.SwitchRoleRequest;
import com.babybreakfast.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    @GetMapping("/session")
    public ApiResponse<AuthResponse> session() {
        return ApiResponse.success(authService.currentSession());
    }

    @PostMapping("/switch-role")
    public ApiResponse<AuthResponse> switchRole(@Valid @RequestBody SwitchRoleRequest request) {
        return ApiResponse.success(authService.switchRole(request));
    }
}
