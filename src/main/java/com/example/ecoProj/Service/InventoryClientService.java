package com.example.ecoProj.Service;

import com.example.ecoProj.dto.response.InventoryProductDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class InventoryClientService {

    @Autowired
    private WebClient webClient;

    public List<InventoryProductDTO> getInventoryProducts() {

        return webClient
                .get()
                .uri("/inventory/products")
                .retrieve()
                .bodyToMono(
                        new ParameterizedTypeReference<
                                List<InventoryProductDTO>>() {}
                )
                .block();
    }

    public InventoryProductDTO addInventoryProduct(
            InventoryProductDTO product
    ) {

        return webClient
                .post()
                .uri("/inventory/product")
                .bodyValue(product)
                .retrieve()
                .bodyToMono(InventoryProductDTO.class)
                .block();
    }

}