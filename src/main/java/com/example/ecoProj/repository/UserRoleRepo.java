package com.example.ecoProj.repository;

import com.example.ecoProj.model.User;
import com.example.ecoProj.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepo extends JpaRepository<UserRole, Integer> {
    List<UserRole> findByUser(User user);
}

