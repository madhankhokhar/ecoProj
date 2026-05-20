package com.example.ecoProj.Service;

import com.example.ecoProj.dto.request.ProductRequest;
import com.example.ecoProj.dto.response.ProductResponse;
import com.example.ecoProj.exception.ResourceNotFoundException;
import com.example.ecoProj.model.Product;
import com.example.ecoProj.repository.PRoductRepo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private static final Logger logger =
            LogManager.getLogger(ProductService.class);

    @Autowired
    private PRoductRepo repo;

    public List<ProductResponse> getAllProducts() {

        logger.info("Fetching all products");

        return repo.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public ProductResponse getProductById(int id) {

        logger.info(
                "Fetching product with id: {}",
                id
        );

        Product product = repo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with id: " + id
                        )
                );

        return mapToResponse(product);
    }

    public ProductResponse addProduct(
            ProductRequest request
    ) {

        logger.info(
                "Adding product: {}",
                request.getName()
        );

        Product product = mapToEntity(request);

        Product savedProduct =
                repo.save(product);

        return mapToResponse(savedProduct);
    }

    public ProductResponse updateProduct(
            int id,
            ProductRequest request
    ) {

        logger.info(
                "Updating product with id: {}",
                id
        );

        Product existing = repo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with id: " + id
                        )
                );

        existing.setName(request.getName());
        existing.setDescr(request.getDescr());
        existing.setBrand(request.getBrand());
        existing.setPrice(request.getPrice());
        existing.setCategory(request.getCategory());
        existing.setReleaseDate(request.getReleaseDate());
        existing.setAvailable(request.isAvailable());
        existing.setQuantity(request.getQuantity());

        Product updated =
                repo.save(existing);

        return mapToResponse(updated);
    }

    public void deleteProduct(int id) {

        logger.info(
                "Deleting product with id: {}",
                id
        );

        Product product = repo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with id: " + id
                        )
                );

        repo.delete(product);
    }

    // =========================
    // MAPPERS
    // =========================

    private ProductResponse mapToResponse(
            Product product
    ) {

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescr(),
                product.getBrand(),
                product.getPrice(),
                product.getCategory(),
                product.getReleaseDate(),
                product.isAvailable(),
                product.getQuantity()
        );
    }

    private Product mapToEntity(
            ProductRequest request
    ) {

        Product product = new Product();

        product.setName(request.getName());
        product.setDescr(request.getDescr());
        product.setBrand(request.getBrand());
        product.setPrice(request.getPrice());
        product.setCategory(request.getCategory());
        product.setReleaseDate(request.getReleaseDate());
        product.setAvailable(request.isAvailable());
        product.setQuantity(request.getQuantity());

        return product;
    }
}