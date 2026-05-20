package com.example.ecoProj.Controller;

import com.example.ecoProj.Service.UserService;
import com.example.ecoProj.dto.request.LoginRequest;
import com.example.ecoProj.dto.request.RegisterRequest;
import com.example.ecoProj.dto.response.AuthResponse;
import com.example.ecoProj.dto.response.UserResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService service;

    @PostMapping("/register")
    public UserResponse register(
            @Valid @RequestBody RegisterRequest request) {

        return service.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(
            @Valid @RequestBody LoginRequest request) {

        return new AuthResponse(
                service.verify(request)
        );
    }
}