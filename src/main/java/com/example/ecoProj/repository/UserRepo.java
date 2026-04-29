package com.example.ecoProj.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.ecoProj.model.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User, Integer> {
    User findByEmail(String email);
}
