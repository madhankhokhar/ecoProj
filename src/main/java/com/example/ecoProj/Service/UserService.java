package com.example.ecoProj.Service;


import com.example.ecoProj.model.User;
import com.example.ecoProj.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepo repo;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtService jwtService;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);


    public User register(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        return repo.save(user);
    }


//    public String verify(User user) {
//        Authentication authentication=
//                authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(),user.getPassword()));
//        if(authentication.isAuthenticated())
//            return jwtService.generateToken(user.getEmail());
//        return "Fail";
//    }

    public String verify(User user) {
        try {
            Authentication authentication =
                    authManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    user.getEmail(),
                                    user.getPassword()));

            System.out.println(authentication);

            if(authentication.isAuthenticated())
                return jwtService.generateToken(user.getEmail());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Fail";
    }

}
