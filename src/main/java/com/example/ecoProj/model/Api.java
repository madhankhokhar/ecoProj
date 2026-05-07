package com.example.ecoProj.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "apis",
        uniqueConstraints = @UniqueConstraint(columnNames = {"method", "path"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Api {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer apiId;

    @Column(nullable = false)
    private String method;

    @Column(nullable = false)
    private String path;
}
