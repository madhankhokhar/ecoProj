package com.example.ecoProj.Service;

import com.example.ecoProj.model.MyUserDetail;
import com.example.ecoProj.model.User;
import com.example.ecoProj.repository.RolePermissionRepo;
import com.example.ecoProj.repository.UserRepo;
import com.example.ecoProj.repository.UserRoleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserRoleRepo userRoleRepo;

    @Autowired
    private RolePermissionRepo rolePermissionRepo;

    @Override
    public UserDetails loadUserByUsername(String email) {

        User user = userRepo.findByEmail(email);

        if(user == null){
            throw new UsernameNotFoundException("User not found");
        }

        return new MyUserDetail(user, userRoleRepo, rolePermissionRepo);

    }
}
