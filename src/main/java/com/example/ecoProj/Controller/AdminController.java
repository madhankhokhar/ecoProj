package com.example.ecoProj.Controller;

import com.example.ecoProj.Service.ProductService;
import com.example.ecoProj.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ProductService service;

    @PostMapping("/product")
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {

        Product savedProduct = service.addProduct(product);

        return ResponseEntity.ok(savedProduct);
    }

    @PutMapping("/product/{id}")
    public ResponseEntity<String> updateProduct(@PathVariable int id,
                                                @RequestBody Product product) {

        Product existing = service.getProductById(id);

        if (existing != null) {

            product.setId(id);

            service.updateProduct(product);

            return ResponseEntity.ok("Product updated");
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/product/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable int id) {

        Product product = service.getProductById(id);

        if (product != null) {

            service.deleteProduct(id);

            return ResponseEntity.ok("Product deleted");
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/user/{id}")
    public String deleteUser(@PathVariable int id) {

        return "Delete User API";
    }

    @GetMapping("/profile")
    public String profile() {

        return "Admin Profile";
    }
}