package com.inventory.barcode.entity;

import com.inventory.barcode.enums.BarcodeType;

import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDateTime;

@Entity

@Table(

        name = "barcodes",

        uniqueConstraints = {

                @UniqueConstraint(name = "uk_barcode_code", columnNames = "code")

        }

)

@Getter

@Setter

@NoArgsConstructor

@AllArgsConstructor

@Builder

public class Barcode {

    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    @Column(nullable = false, unique = true)

    private String code;

    @Enumerated(EnumType.STRING)

    @Column(nullable = false)

    private BarcodeType type;

    @Column(nullable = false)

    private Long productId;

    private LocalDateTime createdAt;

    @PrePersist

    public void prePersist() {

        createdAt = LocalDateTime.now();

    }

}
