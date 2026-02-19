package com.golden.erp.application.scheduler;

import com.golden.erp.domain.order.entity.Order;
import com.golden.erp.domain.order.repository.OrderRepository;
import com.golden.erp.domain.order.valueobject.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class LateOrderScheduler {

    private final OrderRepository orderRepository;

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void markLateOrders() {
        LocalDateTime threshold = LocalDateTime.now().minusHours(48);
        List<Order> lateOrders = orderRepository.findByStatusAndDataCriacaoBefore(
                OrderStatus.CREATED, threshold);

        if (lateOrders.isEmpty()) {
            log.info("[Scheduler] Nenhum pedido atrasado encontrado");
            return;
        }

        int count = 0;
        for (Order order : lateOrders) {
            order.markAsLate();
            orderRepository.save(order);
            count++;
        }

        log.info("[Scheduler] {} pedido(s) marcado(s) como LATE", count);
    }
}
