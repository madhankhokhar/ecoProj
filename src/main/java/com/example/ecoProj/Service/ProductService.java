package com.example.ecoProj.Service;

import com.example.ecoProj.model.Product;
import com.example.ecoProj.repository.PRoductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

@Service
public class ProductService {


    private static final Logger logger = LogManager.getLogger(ProductService.class);

    @Autowired
    private PRoductRepo repo;

    public List<Product> getAllProducts(){
        return repo.findAll();
    }
    public Product getProductById(int id){
        return repo.findById(id).orElse(null);
    }

    public Product addProduct(Product product) {
        return repo.save(product);
    }
    public Product updateProduct(Product product) {
        return repo.save(product);
    }

    public void deleteProduct(int id) {
        repo.deleteById(id);
    }


}
