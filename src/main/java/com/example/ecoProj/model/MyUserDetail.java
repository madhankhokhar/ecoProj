package com.example.ecoProj.model;

import com.example.ecoProj.repository.PermissionApiRepo;
import com.example.ecoProj.repository.UserRoleRepo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

public class MyUserDetail implements UserDetails {

    private static final Logger logger = LogManager.getLogger(MyUserDetail.class);

    private final User user;
    private final UserRoleRepo userRoleRepo;
    private final PermissionApiRepo permissionApiRepo;

    public MyUserDetail(User user,
                        UserRoleRepo userRoleRepo,
                        PermissionApiRepo permissionApiRepo) {
        this.user = user;
        this.userRoleRepo = userRoleRepo;
        this.permissionApiRepo = permissionApiRepo;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        logger.debug("Loading authorities for user: {}", user.getEmail());

        // 🔥 Use Set to avoid duplicates
        Set<String> authoritySet = new HashSet<>();

        // ✅ 1. Add Roles
        List<UserRole> roles = userRoleRepo.findByUser(user);
        for (UserRole ur : roles) {
            Role role = ur.getRole();

            String roleName = "ROLE_" + role.getRoleName();
            authoritySet.add(roleName);

            logger.debug("Added role: {}", roleName);
        }

        // ✅ 2. Fetch APIs in ONE query (optimized)
        List<Api> apis = permissionApiRepo.findApisByUser(user);

        for (Api api : apis) {
            String perm = api.getMethod() + ":" + api.getPath();
            authoritySet.add(perm);

            logger.debug("Added permission: {}", perm);
        }

        // ✅ Convert to GrantedAuthority list
        List<GrantedAuthority> authorities = authoritySet.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        logger.debug("Total unique authorities assigned: {}", authorities.size());

        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}