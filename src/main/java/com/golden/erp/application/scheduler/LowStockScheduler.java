package com.golden.erp.application.scheduler;

import com.golden.erp.domain.product.entity.Product;
import com.golden.erp.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class LowStockScheduler {

    private final ProductRepository productRepository;

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional(readOnly = true)
    public void checkLowStock() {
        List<Product> lowStockProducts = productRepository.findAllWithLowStock();

        if (lowStockProducts.isEmpty()) {
            log.info("[Scheduler] Todos os produtos estão com estoque adequado");
            return;
        }

        log.warn("[Scheduler] {} produto(s) com estoque abaixo do mínimo:", lowStockProducts.size());
        for (Product product : lowStockProducts) {
            log.warn("[Scheduler] Produto '{}' (SKU: {}) - Estoque: {}, Mínimo: {}",
                    product.getNome(), product.getSku(), product.getEstoque(), product.getEstoqueMinimo());
        }
    }
}
