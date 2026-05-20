package com.example.ecoProj.Controller;

import com.example.ecoProj.Service.ProductService;
import com.example.ecoProj.dto.request.ProductRequest;
import com.example.ecoProj.dto.response.ProductResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ProductService service;

    @PostMapping("/product")
    public ResponseEntity<ProductResponse>
    addProduct(
            @Valid @RequestBody ProductRequest request
    ) {

        return ResponseEntity.ok(
                service.addProduct(request)
        );
    }

    @PutMapping("/product/{id}")
    public ResponseEntity<ProductResponse>
    updateProduct(
            @PathVariable int id,
            @Valid @RequestBody ProductRequest request
    ) {

        return ResponseEntity.ok(
                service.updateProduct(id, request)
        );
    }

    @DeleteMapping("/product/{id}")
    public ResponseEntity<String>
    deleteProduct(
            @PathVariable int id
    ) {

        service.deleteProduct(id);

        return ResponseEntity.ok(
                "Product deleted"
        );
    }

    @DeleteMapping("/user/{id}")
    public String deleteUser(
            @PathVariable int id
    ) {

        return "Delete User API";
    }

    @GetMapping("/profile")
    public String profile() {

        return "Admin Profile";
    }
}