package com.example.ecoProj.Service;

import com.example.ecoProj.dto.request.LoginRequest;
import com.example.ecoProj.dto.request.RegisterRequest;
import com.example.ecoProj.dto.response.UserResponse;
import com.example.ecoProj.exception.BadRequestException;
import com.example.ecoProj.exception.UnauthorizedException;
import com.example.ecoProj.model.User;
import com.example.ecoProj.repository.UserRepo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private static final Logger logger =
            LogManager.getLogger(UserService.class);

    @Autowired
    private UserRepo repo;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private EmailService emailService;

    private final BCryptPasswordEncoder encoder =
            new BCryptPasswordEncoder(12);

    //  REGISTER 

    public UserResponse register(RegisterRequest request) {

        logger.info(
                "Registering user: {}",
                request.getEmail()
        );

        // CHECK EXISTING EMAIL

        User existingUser =
                repo.findByEmail(request.getEmail());

        if (existingUser != null) {

            logger.warn(
                    "Registration failed. Email already exists: {}",
                    request.getEmail()
            );

            throw new BadRequestException(
                    "Email already registered"
            );
        }

        // DTO -> ENTITY

        User user = new User();

        user.setEmail(request.getEmail());

        user.setPassword(
                encoder.encode(request.getPassword())
        );

        // SAVE USER

        User savedUser = repo.save(user);

        logger.info(
                "User registered successfully: {}",
                savedUser.getEmail()
        );

        // SEND EMAIL ASYNC

        emailService.sendRegistrationEmail(
                savedUser.getEmail(),
                savedUser.getEmail()
        );

        // ENTITY -> RESPONSE DTO

        return new UserResponse(
                savedUser.getId(),
                savedUser.getEmail()
        );
    }

    //  LOGIN 

    public String verify(LoginRequest request) {

        logger.info(
                "Authenticating user: {}",
                request.getEmail()
        );

        try {

            Authentication authentication =
                    authManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    request.getEmail(),
                                    request.getPassword()
                            )
                    );

            UserDetails userDetails =
                    (UserDetails) authentication.getPrincipal();

            List<String> roles =
                    new ArrayList<>();

            List<String> permissions =
                    new ArrayList<>();

            userDetails.getAuthorities().forEach(auth -> {

                String value =
                        auth.getAuthority();

                if (value.startsWith("ROLE_")) {

                    roles.add(value);

                } else {

                    permissions.add(value);
                }
            });

            logger.info(
                    "User authenticated successfully: {}",
                    request.getEmail()
            );

            return jwtService.generateToken(
                    request.getEmail(),
                    roles,
                    permissions
            );

        } catch (Exception e) {

            logger.error(
                    "Authentication failed for user: {}",
                    request.getEmail(),
                    e
            );

            throw new UnauthorizedException(
                    "Invalid email or password"
            );
        }
    }
}