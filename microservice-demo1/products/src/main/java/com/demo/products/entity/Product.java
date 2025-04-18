package com.demo.products.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Entity
@AllArgsConstructor
@Table(name = "products")
public class Product {
    @Id
    private String id;

    @Column(name = "description")
    private String desc;

    private Double price;
}
