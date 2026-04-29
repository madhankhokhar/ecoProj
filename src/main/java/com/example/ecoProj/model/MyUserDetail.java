package com.example.ecoProj.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.example.ecoProj.repository.UserRoleRepo;
import com.example.ecoProj.repository.RolePermissionRepo;



import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MyUserDetail implements UserDetails {

    private User user;
    private UserRoleRepo userRoleRepo;
    private RolePermissionRepo rolePermissionRepo;

    public MyUserDetail(User user,
                        UserRoleRepo userRoleRepo,
                        RolePermissionRepo rolePermissionRepo) {
        this.user = user;
        this.userRoleRepo = userRoleRepo;
        this.rolePermissionRepo = rolePermissionRepo;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        List<GrantedAuthority> authorities = new ArrayList<>();

        List<UserRole> roles = userRoleRepo.findByUser(user);

        for(UserRole ur : roles){
            Role role = ur.getRole();

            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleName()));

            List<RolePermission> permissions = rolePermissionRepo.findByRole(role);

            for(RolePermission rp : permissions){
                authorities.add(
                        new SimpleGrantedAuthority(
                                rp.getPermission().getPermissionName()
                        )
                );
            }
        }

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
