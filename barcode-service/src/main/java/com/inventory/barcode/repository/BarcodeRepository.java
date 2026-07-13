package com.inventory.barcode.repository;

import com.inventory.barcode.entity.Barcode;
import com.inventory.barcode.enums.BarcodeType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BarcodeRepository extends JpaRepository<Barcode, Long> {

    boolean existsByCode(String code);

    boolean existsByProductIdAndType(Long productId, BarcodeType type);

    long countByProductIdAndType(Long productId, BarcodeType type);

    List<Barcode> findByProductId(Long productId);
}