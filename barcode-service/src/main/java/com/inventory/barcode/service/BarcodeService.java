package com.inventory.barcode.service;

import com.inventory.barcode.client.ProductClient;
import com.inventory.barcode.dto.BarcodeCreateRequest;
import com.inventory.barcode.dto.BarcodeResponse;
import com.inventory.barcode.dto.ProductResponse;
import com.inventory.barcode.entity.Barcode;
import com.inventory.barcode.enums.BarcodeType;
import com.inventory.barcode.exception.ResourceNotFoundException;
import com.inventory.barcode.generator.BarcodeGeneratorFacade;
import com.inventory.barcode.mapper.BarcodeMapper;
import com.inventory.barcode.repository.BarcodeRepository;
import com.inventory.barcode.validation.BarcodeRuleValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BarcodeService {

    private final BarcodeRepository barcodeRepository;
    private final ProductClient productClient;
    private final BarcodeRuleValidator barcodeRuleValidator;
    private final BarcodeGeneratorFacade barcodeGeneratorFacade;

    public BarcodeResponse create(BarcodeCreateRequest request) {
        ProductResponse product = productClient.getProductById(request.productId());

        barcodeRuleValidator.validate(product, request.type());

        long sequence = getNextSequence(product.id(), request.type());

        String barcodeCode = barcodeGeneratorFacade.generate(
                request.type(),
                product.code(),
                sequence
        );

        Barcode barcode = Barcode.builder()
                .code(barcodeCode)
                .type(request.type())
                .productId(product.id())
                .build();

        Barcode savedBarcode = barcodeRepository.save(barcode);

        return BarcodeMapper.toResponse(savedBarcode);
    }

    public List<BarcodeResponse> getAll() {
        return barcodeRepository.findAll()
                .stream()
                .map(BarcodeMapper::toResponse)
                .toList();
    }

    public BarcodeResponse getById(Long id) {
        Barcode barcode = barcodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Barcode not found with id: " + id));

        return BarcodeMapper.toResponse(barcode);
    }

    public List<BarcodeResponse> getByProductId(Long productId) {
        return barcodeRepository.findByProductId(productId)
                .stream()
                .map(BarcodeMapper::toResponse)
                .toList();
    }

    public void delete(Long id) {
        Barcode barcode = barcodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Barcode not found with id: " + id));

        barcodeRepository.delete(barcode);
    }

    @Transactional
    public void deleteByProductId(Long productId) {
        barcodeRepository.deleteByProductId(productId);
    }

    private long getNextSequence(Long productId, BarcodeType type) {
        if (type != BarcodeType.SCALE) {
            return 0;
        }

        return barcodeRepository.countByProductIdAndType(productId, BarcodeType.SCALE) + 1;
    }
}
