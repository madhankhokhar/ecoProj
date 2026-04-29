package com.example.ecoProj.repository;


import com.example.ecoProj.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PRoductRepo extends JpaRepository<Product, Integer> {
    

}
