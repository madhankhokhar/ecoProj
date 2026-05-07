package com.example.ecoProj.Service;

import com.example.ecoProj.model.MyUserDetail;
import com.example.ecoProj.model.User;
import com.example.ecoProj.repository.PermissionApiRepo;
import com.example.ecoProj.repository.RolePermissionRepo;
import com.example.ecoProj.repository.UserRepo;
import com.example.ecoProj.repository.UserRoleRepo;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepo userRepo;
    private final UserRoleRepo userRoleRepo;
    private final PermissionApiRepo permissionApiRepo;

    public MyUserDetailsService(UserRepo userRepo,
                                UserRoleRepo userRoleRepo,
                                PermissionApiRepo permissionApiRepo) {
        this.userRepo = userRepo;
        this.userRoleRepo = userRoleRepo;
        this.permissionApiRepo = permissionApiRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {

        User user = userRepo.findByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return new MyUserDetail(user, userRoleRepo, permissionApiRepo);
    }
}
