package com.example.ecoProj.Service;


import com.example.ecoProj.model.MyUserDetail;
import com.example.ecoProj.model.User;
import com.example.ecoProj.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private static final Logger logger = LogManager.getLogger(UserService.class);

    @Autowired
    private UserRepo repo;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private MyUserDetailsService userDetailsService;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public User register(User user) {
        logger.info("Registering user: {}", user.getEmail());
        user.setPassword(encoder.encode(user.getPassword()));
        return repo.save(user);
    }

    public String verify(User user) {
        try {
            logger.info("Authenticating user: {}", user.getEmail());

            Authentication authentication =
                    authManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    user.getEmail(),
                                    user.getPassword()));

            if (authentication.isAuthenticated()) {

                logger.info("User authenticated successfully: {}", user.getEmail());

                // 🔥 IMPORTANT: get authorities from Spring Security
                UserDetails userDetails =
                        (UserDetails) authentication.getPrincipal();

                List<String> roles = new ArrayList<>();
                List<String> permissions = new ArrayList<>();

                userDetails.getAuthorities().forEach(auth -> {
                    String value = auth.getAuthority();

                    if (value.startsWith("ROLE_")) {
                        roles.add(value);
                    } else {
                        permissions.add(value);
                    }
                });

                return jwtService.generateToken(
                        user.getEmail(),
                        roles,
                        permissions
                );
            }

        } catch (Exception e) {
            logger.error("Authentication failed for user: {}", user.getEmail(), e);
        }

        return "Fail";
    }
}
