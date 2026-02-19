package com.golden.erp.application.product.service;

import com.golden.erp.application.product.dto.CreateProductRequest;
import com.golden.erp.application.product.dto.ProductResponse;
import com.golden.erp.application.product.dto.UpdateProductRequest;
import com.golden.erp.domain.exception.DuplicateFieldException;
import com.golden.erp.domain.exception.EntityNotFoundException;
import com.golden.erp.domain.product.entity.Product;
import com.golden.erp.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl {

    private final ProductRepository productRepository;

    public ProductResponse create(CreateProductRequest request) {
        if (productRepository.existsBySku(request.getSku())) {
            throw new DuplicateFieldException("sku", request.getSku());
        }

        Product product = Product.builder()
                .sku(request.getSku())
                .nome(request.getNome())
                .precoBruto(request.getPrecoBruto())
                .estoque(request.getEstoque())
                .estoqueMinimo(request.getEstoqueMinimo())
                .ativo(request.getAtivo() != null ? request.getAtivo() : true)
                .build();

        Product saved = productRepository.save(product);
        log.info("Produto criado com id: {}", saved.getId());
        return ProductResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto", id));
        return ProductResponse.from(product);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> findAll(Boolean ativo, Pageable pageable) {
        return productRepository.findAll(ativo, pageable)
                .map(ProductResponse::from);
    }

    public ProductResponse update(Long id, UpdateProductRequest request) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto", id));

        if (productRepository.existsBySkuAndIdNot(request.getSku(), id)) {
            throw new DuplicateFieldException("sku", request.getSku());
        }

        existing.setSku(request.getSku());
        existing.setNome(request.getNome());
        existing.setPrecoBruto(request.getPrecoBruto());
        existing.setEstoque(request.getEstoque());
        existing.setEstoqueMinimo(request.getEstoqueMinimo());
        existing.setAtivo(request.getAtivo());

        Product saved = productRepository.save(existing);
        log.info("Produto atualizado com id: {}", saved.getId());
        return ProductResponse.from(saved);
    }

    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new EntityNotFoundException("Produto", id);
        }
        productRepository.deleteById(id);
        log.info("Produto removido com id: {}", id);
    }
}
