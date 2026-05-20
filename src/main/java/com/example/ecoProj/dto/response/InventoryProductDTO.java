package com.example.ecoProj.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryProductDTO {

    private Integer id;
    private String name;
    private int stock;
}