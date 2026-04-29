package com.example.ecoProj.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name ="permissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Permission {
    @Id
    private Integer permissionId;

    private String permissionName;
}
