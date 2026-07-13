package com.inventory.product.entity;

import com.inventory.product.enums.Unit;

import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDateTime;

@Entity

@Table(

        name = "products",

        uniqueConstraints = {

                @UniqueConstraint(name = "uk_product_name", columnNames = "name"),

                @UniqueConstraint(name = "uk_product_code", columnNames = "code")

        }

)

@Data

@NoArgsConstructor

@AllArgsConstructor

@Builder

public class Product {

    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    @Column(nullable = false, unique = true)

    private String name;

    @Column(nullable = false, unique = true, length = 5)

    private String code;

    @Column(nullable = false, length = 2)

    private String categoryCode;

    @Column(nullable = false)

    private String brand;

    @Enumerated(EnumType.STRING)

    @Column(nullable = false)

    private Unit unit;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist

    public void prePersist() {

        createdAt = LocalDateTime.now();

        updatedAt = LocalDateTime.now();

    }

    @PreUpdate

    public void preUpdate() {

        updatedAt = LocalDateTime.now();

    }

}
