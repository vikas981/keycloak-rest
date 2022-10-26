package com.viksingh.keycloak.controller;

import com.viksingh.keycloak.dto.request.LoginRequestDTO;
import com.viksingh.keycloak.dto.request.ResetPasswordDTO;
import com.viksingh.keycloak.dto.request.UserRequestDTO;
import com.viksingh.keycloak.dto.response.LoginResponse;
import com.viksingh.keycloak.service.AuthService;
import javax.annotation.security.RolesAllowed;
import lombok.NonNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/user")
    public void createUser(@RequestBody UserRequestDTO userRequestDTO){
        authService.createUser(userRequestDTO);
    }

    @PostMapping("/user/login")
    public LoginResponse loginUser(@RequestBody @NonNull LoginRequestDTO loginRequestDTO){
        return authService.login(loginRequestDTO);
    }

    @PutMapping("/reset-password")
    @RolesAllowed("user")
    public void resetPassword(@RequestBody @NonNull ResetPasswordDTO resetPasswordDTO){
        authService.resetPassword(resetPasswordDTO);
    }

    @PostMapping("/update/profile")
    @RolesAllowed("user")
    public void updateUser(@RequestBody @NonNull UserRequestDTO userRequestDTO){
        authService.updateUser(userRequestDTO);
    }
}
