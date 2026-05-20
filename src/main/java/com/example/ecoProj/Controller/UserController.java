package com.example.ecoProj.Controller;

import com.example.ecoProj.Service.InventoryClientService;
import com.example.ecoProj.Service.ProductService;
import com.example.ecoProj.dto.response.InventoryProductDTO;
import com.example.ecoProj.dto.response.ProductResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private ProductService service;

    @Autowired
    private InventoryClientService inventoryClientService;

    @GetMapping("/products")
    public ResponseEntity<List<ProductResponse>>
    getAllProducts() {

        return ResponseEntity.ok(
                service.getAllProducts()
        );
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ProductResponse>
    getProductById(
            @PathVariable int id
    ) {

        return ResponseEntity.ok(
                service.getProductById(id)
        );
    }

    @GetMapping("/profile")
    public String profile() {

        return "User Profile";
    }
    @GetMapping("/inventory-products")
    public ResponseEntity<List<InventoryProductDTO>>
    getInventoryProducts() {

        return ResponseEntity.ok(
                inventoryClientService.getInventoryProducts()
        );
    }

    @PostMapping("/inventory-product")
    public ResponseEntity<InventoryProductDTO>
    addInventoryProduct(
            @RequestBody InventoryProductDTO product
    ) {

        return ResponseEntity.ok(
                inventoryClientService
                        .addInventoryProduct(product)
        );
    }

}