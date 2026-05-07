package com.example.ecoProj.repository;

import com.example.ecoProj.model.Api;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApiRepo extends JpaRepository<Api, Integer> {

    Optional<Api> findByMethodAndPath(String method, String path);
}