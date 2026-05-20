package com.example.ecoProj.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
public class ProductResponse {

    private Integer id;

    private String name;

    private String descr;

    private String brand;

    private BigDecimal price;

    private String category;

    private Date releaseDate;

    private boolean available;

    private int quantity;
}