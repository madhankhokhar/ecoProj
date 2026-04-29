package com.example.ecoProj.repository;

import com.example.ecoProj.model.Role;
import com.example.ecoProj.model.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolePermissionRepo extends JpaRepository<RolePermission, Integer> {
    List<RolePermission> findByRole(Role role);
}

